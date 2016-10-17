package org.gradlelatex

import org.gradle.api.file.FileCollection

class LatexObj {
  File tex
  File bib
  File pdf
  List<LatexObj> dependsOn
  FileCollection aux
}