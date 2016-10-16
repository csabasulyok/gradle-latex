package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to run bibTex on an aux file
 */
class BibTexTask extends DefaultTask {
  final String group = 'Latex'
  final String description = 'Runs bibtex on all *.bib files in project'

  @InputFiles
  FileCollection getInputFiles() {
    project.latex.bibInputs
  }

  @TaskAction
  void bibtex() {
    project.latex.withBib().each { tex, obj ->
      project.latex.withRecursive(tex) { subObj ->
        project.latex.utils.exec "pdflatex -aux-directory=${project.latex.auxDir} -job-name=${subObj.pdf} ${project.latex.quiet?'-quiet':''} ${subObj.tex}"
      }
      project.latex.utils.exec "bibtex ${project.latex.quiet?'-quiet':''} ${project.latex.auxDir}/${obj.pdf}"
    }
  }
}