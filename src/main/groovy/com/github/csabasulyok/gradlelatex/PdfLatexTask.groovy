package com.github.csabasulyok.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Gradle task to run pdflatex on a TeX file.
 * One such task is created for each Latex artifact.
 * 
 * @author csabasulyok
 */
class PdfLatexTask extends DefaultTask {

  /**
   * Latex artifact used to run current task.
   */
  LatexArtifact obj

  /**
   * Collection of all files whose change should trigger this task.
   * Collected for Gradle's continuous build feature.
   * Contains the following (based on the Latex artifact):
   * - main TeX file
   * - bib file, if there is one
   * - outputs (PDF) of dependent TeX files
   * - auxiliary files/folders
   */
  @InputFiles
  FileCollection inputFiles

  /**
   * Output of current task. Not used by task itself.
   * Set for Gradle's continuous build feature.
   */
  @OutputFile
  File pdf


  //===============================
  // Task description (Gradle API)
  //===============================

  String getGroup() {
    LatexPlugin.TASK_GROUP
  }

  String getDescription() {
    "Uses ${obj.pdfCommand} to compile ${obj.tex.name} into ${obj.pdf.name}"
  }


  //=============
  // Task action
  //=============

  /**
   * Main task action.
   * Empties auxiliary directory.
   */
  @TaskAction
  void pdfLatex() {
    project.latex.utils.with { LatexUtils utils ->
      utils.pdfLatex(obj)
      utils.copyOutput(obj)
      utils.pdfLatex(obj)
      utils.copyOutput(obj)
    }
  }

  /**
   * Set task properties based on Latex artifact.
   * @param obj Latex artifact
   */
  void setObj(LatexArtifact obj) {
    this.obj = obj
    this.pdf = obj.pdf
    this.inputFiles = project.files([obj.tex, obj.bib, obj.dependsOn.collect { it.pdf }, obj.aux, obj.img].flatten() - null)
  }
}