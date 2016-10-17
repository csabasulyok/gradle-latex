package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to run bibTex on an aux file
 */
class BibTexTask extends DefaultTask {
  final String group = 'Latex'
  final String description = 'Runs bibtex on all *.bib files in project'

  String objName
  String jobName
  
  File pdf
  
  @InputFile
  File bib
  
  @OutputFile
  File bbl
  
  
  @TaskAction
  void bibtex() {
    project.latex.with(objName) { subObj ->
      String jobName = subObj.pdf.name.take(subObj.pdf.name.lastIndexOf('.'))
      project.latex.utils.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${jobName} ${project.latex.quiet?'-quiet':''} ${subObj.tex}"
    }
    project.latex.utils.exec "bibtex ${project.latex.quiet?'-quiet':''} ${project.latex.auxDir}/${jobName}"
  }
  
  void setProps(LatexObj obj) {
    objName = obj.name
    pdf = obj.pdf
    bib = obj.bib
    
    jobName = pdf.name.take(pdf.name.lastIndexOf('.'))
    bbl = new File(project.latex.auxDir, "${jobName}.bbl")
  }
}