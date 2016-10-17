package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to run pdflatex on a TeX file
 */
class PdfLatexTask extends DefaultTask {
  final String group = 'Latex'
  final String description = 'Runs pdflatex on given tex file (default: document.tex)'

  File tex
  @InputFiles
  FileCollection inputFiles
  @OutputFile
  File pdf
  String jobName
  
  @TaskAction
  void pdfLatex() {
    project.latex.utils.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${jobName} ${project.latex.quiet?'-quiet':''} ${tex}"
    project.latex.utils.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${jobName} ${project.latex.quiet?'-quiet':''} ${tex}"
  }
  
  void setProps(LatexObj obj) {
    tex = obj.tex
    pdf = obj.pdf
    inputFiles = project.files(tex, obj.aux)
    jobName = pdf.name.take(pdf.name.lastIndexOf('.'))
  }
}