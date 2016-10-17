package org.gradlelatex

import org.gradle.api.Project

/**
 * Gradle extension to hold all latex-file-related data.
 */
class LatexExtension {
  
  /**
   * Final properties
   */
  final Project p
  final File auxDir = new File('.gradle/latex-temp')
  final LatexUtils utils
  protected LinkedHashMap<String, LatexObj> objs = [:]

  /**
   * Configurable properties
   */
  boolean quiet = true
  boolean cleanTemp = false


  LatexExtension(Project p) {
    this.p = p
    this.utils = new LatexUtils(p)
    
    // create auxiliary directory
    this.auxDir.mkdirs()
  }

  void tex(String texName) {
    tex([tex: texName])
  }

  void tex(Map args) {
    objs[args.tex] = new LatexObj()
    objs[args.tex].tex = new File(args.tex)
    objs[args.tex].pdf = new File(args.pdf ?: args.tex.take(args.tex.lastIndexOf('.')))
    objs[args.tex].bib = args.bib ? new File(args.bib) : null
    objs[args.tex].dependsOn = args.dependsOn.collect { dep -> objs[dep] }
    objs[args.tex].aux = args.aux ? p.files(args.aux) : null
    
    addPdfLatexTask(objs[args.tex])
    if (args.bib) {
      addBibTexTask(objs[args.tex])
    }
  }
  
  void addPdfLatexTask(LatexObj obj) {
    PdfLatexTask pdfLatexTask = p.task("pdfLatex.${obj.tex}", type: PdfLatexTask)
    pdfLatexTask.setProps(obj)
    p.tasks["pdfLatex"].dependsOn "pdfLatex.${obj.tex}"
    
    CleanLatexTask cleanLatexTask = p.task("cleanLatex.${obj.tex}", type: CleanLatexTask)
    cleanLatexTask.setProps(obj)
    p.tasks["cleanLatex"].dependsOn "cleanLatex.${obj.tex}"
  }
  
  void addBibTexTask(LatexObj obj) {
    BibTexTask bibTexTask = p.task("bibTex.${obj.tex}", type: BibTexTask)
    bibTexTask.setProps(obj)
    p.tasks["pdfLatex.${obj.tex}"].dependsOn "bibTex.${obj.tex}"
  }
  
  void with(String tex, Closure closure) {
    objs[tex].dependsOn.each { dependentObj ->
      with("${dependentObj.tex}", closure)
    }
    objs[tex].with(closure)
  }


}