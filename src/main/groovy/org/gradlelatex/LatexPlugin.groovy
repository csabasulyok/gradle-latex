package org.gradlelatex

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main Gradle plugin for LaTeX tasks.
 */
class LatexPlugin implements Plugin<Project> {

  void apply(Project p) {
    // extension
    p.extensions.create('latex', LatexExtension, p)

    // placeholder tasks which will get dependencies for each TeX file added
    p.task('pdfLatex')
    // cleaning tasks
    p.task('cleanLatexAux', type: CleanLatexAuxTask)
    p.task('cleanLatex', dependsOn: 'cleanLatexAux')

    // pdflatex becomes the default task
    p.defaultTasks 'pdfLatex'

  }

}