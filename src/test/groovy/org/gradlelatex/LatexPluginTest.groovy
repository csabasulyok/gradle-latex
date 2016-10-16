package org.gradlelatex

import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

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
    assertNotNull(p.tasks.bibTex)
    assertTrue(p.tasks.bibTex instanceof BibTexTask)
    assertNotNull(p.tasks.pdfLatex)
    assertTrue(p.tasks.pdfLatex instanceof PdfLatexTask)
    assertNotNull(p.tasks.cleanLatex)
    assertTrue(p.tasks.cleanLatex instanceof CleanLatexTask)

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
