package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to run pdflatex on a TeX file
 */
class PdfLatexTask extends DefaultTask {
    final String group = 'Latex'
    final String description = 'Runs pdflatex on given tex file (default: document.tex)'

    @InputFiles
    FileCollection getInputFiles() {
        project.latex.texInputs
    }

    @TaskAction
    void pdfLatex() {
        project.latex.eachObj { obj ->
            project.ext.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${obj.pdf} ${project.latex.quiet?'-quiet':''} ${obj.tex}"
            project.ext.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${obj.pdf} ${project.latex.quiet?'-quiet':''} ${obj.tex}"
        }
    }
}