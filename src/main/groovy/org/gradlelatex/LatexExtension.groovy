package org.gradlelatex

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Gradle extension to hold all latex-file-related data.
 */
class LatexExtension {
  Logger LOG = LoggerFactory.getLogger(LatexExtension)
  
  /**
   * Final properties
   */
  final Project p
  final File auxDir
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
    this.auxDir = p.file('.gradle/latex-temp')
    this.auxDir.mkdirs()
    LOG.info "Project $p using auxiliary directory $auxDir"
  }
  
  void tex(String texName) {
    tex([tex: texName])
  }
  
  void tex(Map args) {
    String name = args.tex.take(args.tex.lastIndexOf('.'))
    objs[name] = new LatexObj()
    
    objs[name].name = name
    objs[name].tex = p.file(args.tex)
    objs[name].pdf = p.file(args.pdf ?: "${name}.pdf")
    objs[name].bib = args.bib ? p.file(args.bib) : null
    objs[name].dependsOn = args.dependsOn.collect { dep -> objs[dep.take(dep.lastIndexOf('.'))] }
    objs[name].aux = args.aux ? p.files(args.aux) : null
    
    LOG.info "Added builder for TeX file $args.tex"
    addPdfLatexTask(objs[name])
    if (args.bib) {
      addBibTexTask(objs[name])
    }
  }
  
  void addPdfLatexTask(LatexObj obj) {
    LOG.info "Dynamically adding task 'pdfLatex.${obj.name}'"
    PdfLatexTask pdfLatexTask = p.task("pdfLatex.${obj.name}", type: PdfLatexTask)
    pdfLatexTask.setProps(obj)
    p.tasks["pdfLatex"].dependsOn "pdfLatex.${obj.name}"
    
    LOG.info "Dynamically adding task 'cleanLatex.${obj.name}'"
    CleanLatexTask cleanLatexTask = p.task("cleanLatex.${obj.name}", type: CleanLatexTask)
    cleanLatexTask.setProps(obj)
    p.tasks["cleanLatex"].dependsOn "cleanLatex.${obj.name}"
  }
  
  void addBibTexTask(LatexObj obj) {
    LOG.info "Dynamically adding task 'bibTex.${obj.name}'"
    BibTexTask bibTexTask = p.task("bibTex.${obj.name}", type: BibTexTask)
    bibTexTask.setProps(obj)
    p.tasks["pdfLatex.${obj.name}"].dependsOn "bibTex.${obj.name}"
  }
  
  void with(String name, Closure closure) {
    objs[name].dependsOn.each { dependentObj ->
      with("${dependentObj.name}", closure)
    }
    objs[name].with(closure)
  }
}