package com.github.csabasulyok.gradlelatex

import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

import com.github.csabasulyok.gradlelatex.CleanLatexAuxTask;
import com.github.csabasulyok.gradlelatex.LatexExtension;

class LatexPluginTest {
  Project p

  @Before
  void before() {
    p = ProjectBuilder.builder().build()
    p.apply plugin: 'latex'
  }

  @Test
  public void apply_addsTasks() {
    // adds 3 necessary tasks
    assertNotNull(p.tasks.pdfLatex)
    assertNotNull(p.tasks.cleanLatexAux)
    assertTrue(p.tasks.cleanLatexAux instanceof CleanLatexAuxTask)
    assertNotNull(p.tasks.cleanLatex)

    // adds default task
    assertTrue(p.defaultTasks.contains('pdfLatex'))
  }

  @Test
  public void apply_addsExtension() {
    // adds extension
    assertNotNull(p.latex)
    assertTrue(p.latex instanceof LatexExtension)
  }
}
