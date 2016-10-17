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

  LatexObj obj

  @InputFiles
  FileCollection inputFiles

  @OutputFile
  File pdf

  @TaskAction
  void pdfLatex() {
    project.latex.utils.with { LatexUtils utils ->
      utils.pdfLatex(obj)
      utils.pdfLatex(obj)
      utils.copyOutput(obj)
    }
  }

  void setObj(LatexObj obj) {
    this.obj = obj
    this.pdf = obj.pdf
    this.inputFiles = project.files([obj.tex, obj.bib, obj.dependsOn.collect { it.pdf }, obj.aux].flatten() - null)
  }
}