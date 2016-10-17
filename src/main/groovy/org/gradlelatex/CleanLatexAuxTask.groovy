package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Clean task for LaTeX
 */
class CleanLatexAuxTask extends DefaultTask {
  final String group = 'Latex'
  final String description = 'Cleans auxiliary files used in LaTeX compilation'

  @TaskAction
  void clean() {
    if (project.latex.cleanTemp) {
      project.latex.utils.emptyContent(project.latex.auxDir)
    }
  }
}