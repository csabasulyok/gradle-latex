package org.gradlelatex

import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

class LatexUtilsTest {
  LatexUtils utils
  
  @Before
  void before() {
    Project p = ProjectBuilder.builder().build()
    utils = new LatexUtils(p)
  }
}
