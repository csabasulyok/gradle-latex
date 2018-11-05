package com.github.csabasulyok.gradlelatex

import org.gradle.api.file.FileCollection

/**
 * Represents a TeX artifact which will can be compiled into a PDF.
 * Used to maintain a graph of dependencies.
 * 
 * @author csabasulyok
 */
class LatexArtifact {
  
  /**
   * Name of artifact - derived from the name of the input TeX file,
   * without its extension.
   * 
   * Must be unique per project, and multiple Gradle tasks are created
   * dynamically based on its value.
   */
  String name
  
  /**
   * Name of artifact used for auxiliary files. Same as name,
   * but does not contain whitespaces, folder changes, etc.
   */
  String nameNoPath
  
  /**
   * Represents tex file which is used to call bibtex, pdflatex
   * Must be set.
   */
  File tex
  
  /**
   * Represents bib file used to call bibtex/biber.
   */
  File bib
  
  /**
   * Command string to generate bbl file from bib.
   * By default 'bibtex', but 'biber' may be used instead. 
   */
  String bibCommand
  
  /**
   * Represents output pdf file.
   */
  File pdf
  
  /**
   * Command string to generate PDF file.
   * By default 'pdflatex', but 'xelatex' may be used instead.
   */
  String pdfCommand
  
  /**
   * Collection of dependencies which have to be compiled
   * in order for this one to work (e.g. used with \input). 
   */
  Collection<LatexArtifact> dependsOn
  
  /**
   * Collection of image files or directories with images
   * which have to be transformed because LaTeX cannot use them directly (e.g. svg, emf).
   * These are transformed to PDFs which then can be included in pdflatex. 
   */
  FileCollection img
  
  /**
   * Collection of dependencies which do not have to be compiled
   * for this one. Not taken into consideration at all during compilation,
   * but added to set of input files, so that up-to-date checks and continuous build
   * are triggered when they change.
   */
  FileCollection aux
  
  /**
   * Extra arguments to be passed to pdflatex when building this artifact.
   */
  String extraArgs
  
  @Override
  String toString() {
    "LatexArtifact [name=" + name + ", nameNoPath=" + nameNoPath + ", tex=" + tex + ", bib=" + bib + ", bibCommand=" + bibCommand +
        ", pdf=" + pdf + ", pdfCommand=" + pdfCommand + ", dependsOn=" + dependsOn + ", img=" + img + ", aux=" + aux +
        ", extraArgs=" + extraArgs + "]"
  }
}