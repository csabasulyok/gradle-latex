package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Clean task for LaTeX
 */
class CleanLatexTask extends DefaultTask {
  final String group = 'Latex'
  final String description = 'Cleans all latex-related files in project'

  File pdf
  
  @TaskAction
  void clean() {
    project.latex.utils.exec "rm ${pdf}.pdf"
  }
  
  void setProps(LatexObj obj) {
    pdf = obj.pdf
  }
}