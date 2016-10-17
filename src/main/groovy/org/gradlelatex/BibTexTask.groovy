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

  File tex
  File pdf
  
  @InputFile
  File bib
  
  @OutputFile
  File output
  
  @TaskAction
  void bibtex() {
    project.latex.with(tex.toString()) { subObj ->
      project.latex.utils.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${subObj.pdf} ${project.latex.quiet?'-quiet':''} ${subObj.tex}"
    }
    project.latex.utils.exec "bibtex ${project.latex.quiet?'-quiet':''} ${project.latex.auxDir}/${pdf}"
  }
  
  void setProps(LatexObj obj) {
    tex = obj.tex
    pdf = obj.pdf
    bib = obj.bib
    output = new File(project.latex.auxDir, "${pdf}.bbl")
  }
}