package com.github.csabasulyok.gradlelatex

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Gradle extension to create new dynamic tasks & maintain and manage latex artifacts.
 * Registered to Gradle as extension in LatexPlugin. Thereafter the instance can be accessed via project.latex
 * 
 * @author csabasulyok
 */
class LatexExtension {
  Logger LOG = LoggerFactory.getLogger(LatexExtension)
  
  //==================
  // Final properties
  //==================
  
  final Project p
  
  /**
   * Auxiliary directory where all metadata (obj, out, log, bbl files) are stored.
   */
  final File auxDir
  
  /**
   * Utilities for easy execution.
   * After adding extension, can be accessed via project.latex.utils
   */
  final LatexUtils utils
  
  /**
   * Main container of Latex artifacts.
   * build.gradle can add new entries, which will dynamically create new tasks.
   */
  protected LinkedHashMap<String, LatexArtifact> objs = [:]
  
  
  
  //=========================
  // Configurable properties
  //=========================
  
  /**
   * Flag to pass 'quiet' argument to pdflatex or not.
   * If set, pdflatex will print much information.
   */
  boolean quiet = true
  
  
  LatexExtension(Project p) {
    this.p = p
    this.utils = new LatexUtils(p)
    
    // create auxiliary directory
    this.auxDir = p.file('.gradle/latex-temp')
    this.auxDir.mkdirs()
    LOG.info "Project $p using auxiliary directory $auxDir"
  }
  
  /**
   * Run a closure on a LatexArtifact and each of its dependencies (recursively),
   * starting with the dependencies.
   * 
   * @param name Name of artifact
   * @param closure Closure to be run on artifact and each of its dependencies
   */
  void with(String name, Closure closure) {
    objs[name].dependsOn.each { dependentObj ->
      with("${dependentObj.name}", closure)
    }
    objs[name].with(closure)
  }
  
  //=================================
  // facade methods for build.gradle
  //=================================
  
  /**
   * Assign a new LaTeX artifact only based on the name of the main TeX file.
   * Every other property is deduced.
   * 
   * @param texName filename of main TeX file
   */
  LatexArtifact tex(String texName) {
    tex([tex: texName])
  }
  
  /**
   * Assign a new LaTeX artifact to build via a map of properties.
   * 
   * @param args Map of properties of new LaTeX artifact
   * @return New artifact
   */
  LatexArtifact tex(Map args) {
    
    // name = TeX file name without extension
    String name = args.tex.take(args.tex.lastIndexOf('.'))
    // nameNoPath = name without whitespaces, / and ..
    String nameNoPath = name.replaceAll('[/ ]', '_')
    
    LatexArtifact obj = new LatexArtifact()
    obj.name = name
    obj.nameNoPath = nameNoPath
    obj.tex = p.file(args.tex)
    
    // PDF either given, or same as TeX file with different extension
    obj.pdf = p.file(args.pdf ?: "${name}.pdf")
    // command to create PDF, by default "pdflatex"
    obj.pdfCommand = args.pdfCommand ?: 'pdflatex'
    // bib file optional
    obj.bib = args.bib ? p.file(args.bib) : null
    // bib command - optional, by default "bibtex"
    obj.bibCommand = args.bibCommand ?: 'bibtex'
    
    // build dependencies
    // find already existing LatexArtifact instances instead of holding just names
    obj.dependsOn = args.dependsOn.collect { dep -> objs[dep.take(dep.lastIndexOf('.')).replaceAll('[/ ]', '_')] }
    
    // assign image files/dirs as a FileCollection Gradle can watch
    obj.img = args.img ? utils.findImgFiles(args.img) : null
    // assign auxiliary files as aFileCollection Gradle can watch
    obj.aux = args.aux ? p.files(args.aux) : null
    // extra args optional
    obj.extraArgs = args.extraArgs ?: ''
    
    LOG.info "Added builder for TeX file $args.tex"
    
    // dynamically wire new tasks into workflow
    addPdfLatexTask(obj)
    addCleanLatexTask(obj)
    if (args.bib) {
      addBibTexTask(obj)
    }
    if (args.img) {
      addInkscapeTask(obj)
    }
    if (args.aux) {
      addCopyAuxTask(obj)
    }
    
    // save artifact
    objs[nameNoPath] = obj
    
    obj
  }
  
  //==============================================
  // auxiliary methods for on-the-fly task wiring
  //==============================================
  
  /**
   * Wire new pdfLatex task into workflow based on a LatexArtifact.
   * 
   * Given an artifact with name "texfile", new task "pdfLatex.texfile" is created,
   * which compiles "texfile.tex".
   * 
   * The new task is then inserted as a dependency of the main "pdfLatex" task.
   * 
   * @param obj The basis artifact of the new task. 
   */
  private void addPdfLatexTask(LatexArtifact obj) {
    LOG.info "Dynamically adding task 'pdfLatex.${obj.nameNoPath}'"
    
    // create new task and set its properties using the artifact
    PdfLatexTask pdfLatexTask = p.task("pdfLatex.${obj.nameNoPath}", type: PdfLatexTask, overwrite: true)
    pdfLatexTask.setObj(obj)
    
    // add dependency to all already existing pdfLatex tasks of dependent artifacts
    obj.dependsOn.each { depObj ->
      PdfLatexTask depPdfLatexTask = p.tasks["pdfLatex.${depObj.nameNoPath}"]
      pdfLatexTask.dependsOn depPdfLatexTask
    }
    
    // add new task as dependency of main task
    p.tasks["pdfLatex"].dependsOn pdfLatexTask
  }
  
  /**
   * Wire new cleanLatex task into workflow based on a LatexArtifact.
   *
   * Given an artifact with name "texfile", new task "cleanLatex.texfile" is created,
   * which cleans all output of "pdfLatex.texfile".
   *
   * The new task is then inserted as a dependency of the main "cleanLatex" task.
   *
   * @param obj The basis artifact of the new task.
   */
  private void addCleanLatexTask(LatexArtifact obj) {
    LOG.info "Dynamically adding task 'cleanLatex.${obj.nameNoPath}'"
    
    // create new task and set its properties using the artifact
    CleanLatexTask cleanLatexTask = p.task("cleanLatex.${obj.nameNoPath}", type: CleanLatexTask, overwrite: true)
    cleanLatexTask.setObj(obj)
    
    // add new task as dependency of main task
    p.tasks["cleanLatex"].dependsOn cleanLatexTask
  }
  
  /**
   * Wire new bibTex task into workflow based on a LatexArtifact.
   *
   * Given an artifact with name "texfile" and bib "refs.bib", new task "bibTex.texfile" is created,
   * which compiles "texfile.tex" and calls bibtex on the auxiliary.
   *
   * The new task is then inserted as a dependency of the associated "pdfLatex.texfile" task.
   *
   * @param obj The basis artifact of the new task.
   */
  private void addBibTexTask(LatexArtifact obj) {
    LOG.info "Dynamically adding task 'bibTex.${obj.nameNoPath}'"
    
    // create new task and set its properties using the artifact
    BibTexTask bibTexTask = p.task("bibTex.${obj.nameNoPath}", type: BibTexTask, overwrite: true)
    bibTexTask.setObj(obj)
    
    // add new task as dependency of associated pdfLatex task
    p.tasks["pdfLatex.${obj.nameNoPath}"].dependsOn bibTexTask
  }

  /**
   * Wire new copyAux task into workflow based on a LatexArtifact.
   * The task copies the aux files into the temp folder.
   *
   * The new task is then inserted as a dependency of the associated "pdfLatex.texfile" task.
   *
   * @param obj The basis artifact of the new task.
   */
  private void addCopyAuxTask(LatexArtifact obj) {
    LOG.info "Dynamically adding task 'copyAux.${obj.name}'"

    // create new task and set its properties using the artifact
    Copy copyAuxTask = p.task("copyAux.${obj.name}", type: Copy, overwrite: true) {
      from obj.aux
      into p.latex.auxDir
    }

    // add new task as dependency of associated pdfLatex task
    p.tasks["pdfLatex.${obj.name}"].dependsOn copyAuxTask
    // copy must run before bibtex, so also add it before that
    if (p.tasks.findByName("bibTex.${obj.name}")) {
      p.tasks["bibTex.${obj.name}"].dependsOn copyAuxTask
    }
  }
  
  /**
   * Wire new inkscape task into workflow based on a LatexArtifact.
   *
   * Given an artifact with name "texfile" and img "image.svg", new task "inkscape.texfile" is created,
   * which creates "image.pdf" using inkscape.
   *
   * The new task is then inserted as a dependency of the associated "pdfLatex.texfile" task.
   *
   * @param obj The basis artifact of the new task.
   */
  private void addInkscapeTask(LatexArtifact obj) {
    LOG.info "Dynamically adding task 'inkscape.${obj.nameNoPath}'"
    
    // create new task and set its properties using the artifact
    InkscapeTask inkscapeTask = p.task("inkscape.${obj.nameNoPath}", type: InkscapeTask, overwrite: true)
    inkscapeTask.setObj(obj)
    
    // add new task as dependency of associated pdfLatex task
    p.tasks["pdfLatex.${obj.nameNoPath}"].dependsOn inkscapeTask
    // if a bibtex task exists, add new task as dependency
    if (p.tasks.findByPath("bibTex.${obj.nameNoPath}")) {
      LOG.info "Making sure inkscape is called before bibtex"
      p.tasks["bibTex.${obj.nameNoPath}"].dependsOn inkscapeTask
    }
  }
}
