package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Task to empty auxiliary folder used in LaTeX compilation.
 * 
 * @author csabasulyok
 */
class CleanLatexAuxTask extends DefaultTask {
  
  //===============================
  // Task description (Gradle API)
  //===============================
  
  String getGroup() {
    LatexPlugin.TASK_GROUP
  }
  String getDescription() {
    "Cleans all TeX auxiliary files (out, bbl, log, etc.) from ${project.latex.auxDir}"
  }

  
  //=============
  // Task action
  //=============
  
  /**
   * Main task action.
   * Empties auxiliary directory.
   */
  @TaskAction
  void clean() {
    project.latex.utils.emptyContent(project.latex.auxDir)
  }
}