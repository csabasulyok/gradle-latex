package org.gradlelatex

import java.io.File
import java.util.List

import org.gradle.api.file.FileCollection


class LatexObj {
    File tex
    File bib
    File pdf
    List<String> dependsOn
    FileCollection aux
}