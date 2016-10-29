package com.github.csabasulyok.gradlelatex

import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before

import com.github.csabasulyok.gradlelatex.LatexUtils;

class LatexUtilsTest {
  LatexUtils utils
  
  @Before
  void before() {
    Project p = ProjectBuilder.builder().build()
    utils = new LatexUtils(p)
  }
}
