package com.github.csabasulyok.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Clean task for LaTeX, which removes a pdflatex output.
 * One such task is created for each Latex artifact.
 * 
 * @author csabasulyok
 */
class CleanLatexTask extends DefaultTask {
  Logger LOG = LoggerFactory.getLogger(CleanLatexTask)
  
  /**
   * Output file of pdflatex which should be deleted by this task.
   */
  File pdf
  
  /**
   * Inkscaped image files. Their associated pdf gets deleted by this task.
   */
  FileCollection img
  
  
  //===============================
  // Task description (Gradle API)
  //===============================
  String getGroup() {
    LatexPlugin.TASK_GROUP
  }
  
  String getDescription() {
    "Removes output PDF ${pdf.name} and associated inkscaped PDFs if any exist"
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
    LOG.quiet "Removing file $pdf"
    pdf.delete()
    
    img.each { File imgFile ->
      File pdfFile = project.latex.utils.imgFileToPdfFile(imgFile)
      if (pdfFile.exists()) {
        LOG.quiet "Removing inkscape output $pdfFile"
        pdfFile.delete()
      }
    }
  }
  
  /**
   * Set task properties based on Latex artifact. 
   * @param obj Latex artifact
   */
  void setObj(LatexArtifact obj) {
    pdf = obj.pdf
    img = obj.img
  }
}