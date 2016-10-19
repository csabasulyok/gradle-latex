package org.gradlelatex

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main Gradle plugin for LaTeX tasks.
 * Assigned the name 'latex' under META-INF/gradle-plugins,
 * therefore, once on classpath, it can be applied in build.gradle using:
 * 
 * apply plugin: 'latex'
 * 
 * Adds assembler tasks (no content, just there for dependencies) and an extension to the Gradle project.
 * The extension can be used to wire new tasks into the workflow, which build TeX and BiB files.
 */
class LatexPlugin implements Plugin<Project> {

  /**
   * Task group to be used by all tasks inserted throughout this plugin.
   */
  static final String TASK_GROUP = 'LaTeX'
  
  /**
   * Apply current plugin to project.
   * @param p Project to apply this plugin to.
   */
  void apply(Project p) {
    // create extension
    p.extensions.create('latex', LatexExtension, p)

    // placeholder tasks which will get dependencies for each TeX file added
    p.task('pdfLatex', group: TASK_GROUP, description: 'Compile all configured TeX files into PDFs')
    p.task('cleanLatex', group: TASK_GROUP, description: 'Clean all output PDFs')
    
    // task to clean temp files
    p.task('cleanLatexAux', type: CleanLatexAuxTask)

    // pdflatex becomes the default task
    p.defaultTasks 'pdfLatex'

  }

}