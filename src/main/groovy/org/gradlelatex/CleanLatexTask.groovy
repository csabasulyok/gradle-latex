package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Clean task for LaTeX
 */
class CleanLatexTask extends DefaultTask {
  Logger LOG = LoggerFactory.getLogger(CleanLatexTask)
  
  final String group = 'Latex'
  final String description = 'Cleans all latex-related files in project'

  File pdf
  
  @TaskAction
  void clean() {
    LOG.quiet "Removing file $pdf"
    pdf.delete()
  }
  
  void setObj(LatexObj obj) {
    pdf = obj.pdf
  }
}