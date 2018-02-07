package com.github.csabasulyok.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to run bibTex on an aux file.
 * One such task is created for each Latex artifact.
 * 
 * @author csabasulyok
 */
class BibTexTask extends DefaultTask {
  
  /**
   * Latex artifact used to run current task.
   */
  LatexArtifact obj
  
  /**
   * Input of current task. Not used by task itself. Set for Gradle's continuous build feature.
   * BibTex is called on the aux file produced by the main TeX file.
   * We mark the bib as input only so Gradle would only rerun this task when bib file changes.
   */
  @InputFile
  File bib
  
  /**
   * Output of current task. Not used by task itself.
   * Set for Gradle's continuous build feature.
   */
  @OutputFile
  File bbl
  
  /**
   * Command to use for the bibliography generation.
   * By default, 'bibtex', can be changed to 'biber'
   */
  String bibCommand = 'bibtex'

  
  //===============================
  // Task description (Gradle API)
  //===============================
  
  String getGroup() {
    LatexPlugin.TASK_GROUP
  }
  
  String getDescription() {
    "Uses bibtex or biber to compile references of ${obj.tex.name}"
  }

  
  //=============
  // Task action
  //=============
  
  /**
   * Main task action.
   * Empties auxiliary directory.
   */
  @TaskAction
  void bibtex() {
    project.latex.with(obj.name) { LatexArtifact subObj ->
      project.latex.utils.pdfLatex(subObj)
    }
    project.latex.utils.bibTex(obj)
  }

  /**
   * Set task properties based on Latex artifact.
   * @param obj Latex artifact
   */
  void setObj(LatexArtifact obj) {
    this.obj = obj
    this.bib = obj.bib
    this.bibCommand = obj.bibCommand
    this.bbl = new File(project.latex.auxDir, "${obj.name}.bbl")
  }
}