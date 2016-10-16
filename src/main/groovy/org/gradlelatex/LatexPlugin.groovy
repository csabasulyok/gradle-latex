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

    // create temp folder if necessary
    p.file(p.latex.auxDir).mkdirs()

    // tasks
    p.task('bibTex', type: BibTexTask).onlyIf { task -> p.latex.bibInputs }
    p.task('pdfLatex', type: PdfLatexTask, dependsOn: 'bibTex')
    p.task('cleanLatex', type: CleanLatexTask)

    // pdflatex becomes the default task
    p.defaultTasks 'pdfLatex'

  }

}