package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Clean task for LaTeX
 */
class CleanLatexTask extends DefaultTask {
  final String group = 'Latex'
  final String description = 'Cleans all latex-related files in project'

  @TaskAction
  void clean() {
    if (project.latex.cleanTemp) {
      project.latex.utils.exec "rm -rf ${project.latex.auxDir}/*"
    }
    project.latex.eachObj { obj ->
      project.latex.utils.exec "rm ${obj.pdf}.pdf"
    }
  }
}