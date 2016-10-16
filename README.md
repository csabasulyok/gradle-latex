Gradle LaTeX plugin
===================
[![Build Status](https://travis-ci.org/csabasulyok/gradle-latex.svg?branch=master)](https://travis-ci.org/csabasulyok/gradle-latex)

This plugin allows automation of compiling PDFs using [LaTeX](https://www.latex-project.org/) and [BiBTeX](http://www.bibtex.org/).
It offers the following:
- compile multiple `tex` and `bib` files with dependencies in-between them;
- use Gradle's [continuous build feature](https://docs.gradle.org/current/userguide/continuous_build.html) to watch for changing sources
- watch auxiliary files (images, extra tex files, etc.)


Requirements
------------
- LaTeX compiler: `pdflatex` and `bibtex` on `PATH` (tested using [MikTeX](https://miktex.org/) on Windows and [TeXLive](https://www.tug.org/texlive/) on Linux)
- Java version 1.8+
- Gradle 2.0+


Gradle usage
------------
### 1. Including plugin ###
The plugin can be used by including the following in your `build.gradle`:

~~~gradle
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath group: 'org.gradlelatex', name: 'gradle-latex', version: '1.0-SNAPSHOT'
    }
}


apply plugin: 'latex'
~~~

### 2. Specifying inputs ###
Specify inputs in `build.gradle`:
~~~gradle
latex {
    // doc1.tex -> doc1.pdf
    tex 'doc1.tex'
    // doc2.tex -> customDoc1.pdf
    tex tex:'doc2.tex', pdf:'customDoc1'
    // doc3.tex, refs.bib -> doc3.pdf
    tex tex:'doc3.tex', bib:'refs.bib'
}
~~~

### 3. Calling LaTeX tasks ###

Build all PDFs:
~~~
>gradle pdfLatex
~~~

Clean output and auxiliary files:
~~~
>gradle cleanLatex
~~~

Use [continuous build ](https://docs.gradle.org/current/userguide/continuous_build.html) (will block and watch file changes):
~~~
>gradle -t pdfLatex
~~~


