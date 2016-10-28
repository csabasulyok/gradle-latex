package org.gradlelatex

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFiles
import org.gradle.api.tasks.TaskAction
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Gradle task to run inkscape on unsupported image files (svg, emf).
 * One such task is created for each Latex artifact with images associated.
 * 
 * @author csabasulyok
 */
class InkscapeTask extends DefaultTask {
  Logger LOG = LoggerFactory.getLogger(InkscapeTask)
  
  /**
   * Latex artifact used to run current task.
   */
  LatexArtifact obj
  
  /**
   * Input of current task.
   * Contains unsupported images (svg/emf) which will be inkscaped to pdfs.
   */
  @InputFiles
  FileCollection img
  
  /**
   * Output of current task. Contains pdfs converted from unsupported image files (svg/emf).
   */
  @OutputFiles
  Collection<File> pdfs
  
  
  //===============================
  // Task description (Gradle API)
  //===============================
  
  String getGroup() {
    LatexPlugin.TASK_GROUP
  }
  
  String getDescription() {
    "Uses inkscape to convert unsupported images for ${obj.tex.name} with inkscape"
  }
  
  
  //=============
  // Task action
  //=============
  
  /**
   * Evaluates if an input file lacks a counterpart PDF with a later creation time. 
   * @param file File to be evaluated (e.g. "abc.svg")
   * @return true if "abc.pdf" does not exist or is older than "abc.svg"
   */
  boolean imgNeedsUpdate(File imgFile, File pdfFile) {
    LOG.info "Evaluating $imgFile for inkscape PDF creation"
    
    if (pdfFile.exists()) {
      if (pdfFile.lastModified() < imgFile.lastModified()) {
        LOG.info "$pdfFile exists, but is older than image"
        return true
      } else {
        LOG.info "$pdfFile exists and is up-to-date"
        return false
      }
    } else {
      LOG.info "$pdfFile does not exist"
      return true
    }
  }
  
  /**
   * Main task action.
   * Collects all unsupported images and converts them as necessary.
   */
  @TaskAction
  void inkscape() {
    img.each { File imgFile ->
      File pdfFile = project.latex.utils.imgFileToPdfFile(imgFile)
      if (imgNeedsUpdate(imgFile, pdfFile)) {
        project.latex.utils.inkscape(imgFile, pdfFile)
      }
    }
  }
  
  /**
   * Set task properties based on Latex artifact.
   * @param obj Latex artifact
   */
  void setObj(LatexArtifact obj) {
    this.obj = obj
    this.img = obj.img
    this.pdfs = this.img.collect { project.latex.utils.imgFileToPdfFile(it) }
  }
}