package org.gradlelatex

import org.gradle.api.file.FileCollection

class LatexObj {

  String name
  File tex
  File bib
  File pdf
  List<LatexObj> dependsOn
  FileCollection aux
  String extraArgs

  String getJobName() {
    pdf.name.take(pdf.name.lastIndexOf('.'))
  }
}