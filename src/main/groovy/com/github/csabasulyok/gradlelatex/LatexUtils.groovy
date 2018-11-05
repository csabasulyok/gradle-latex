package com.github.csabasulyok.gradlelatex

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Utility methods related to ant.
 * 
 * @author csabasulyok
 */
class LatexUtils {
  Logger LOG = LoggerFactory.getLogger(LatexUtils)
  
  final Project p
  
  LatexUtils(Project p) {
    this.p = p
  }
  
  /**
   * Execute latex compilation command for given latex artifact.
   * All auxiliary files(aux, out, log) and the pdf output is stored in an auxiliary directory.
   * 
   * @param obj Any Latex artifact with the tex property set.
   */
  void pdfLatex(LatexArtifact obj) {
    LOG.quiet "Executing pdflatex for $obj.nameNoPath"
    
    p.ant.exec(executable: obj.pdfCommand, dir: p.projectDir, failonerror: true) {
      arg(value: "-output-directory=${p.latex.auxDir}")
      if (p.latex.quiet) {
        arg(value: '-quiet')
      }
      if (obj.extraArgs) {
        arg(value: obj.extraArgs)
      }
      arg(value: obj.tex)
    }
  }
  
  /**
   * Execute bibtex command for given latex artifact.
   *
   * @param obj Any Latex artifact with the tex and bib properties set.
   */
  void bibTex(LatexArtifact obj) {
    LOG.quiet "Executing $obj.bibCommand for $obj.nameNoPath"
    p.ant.copy(file: obj.bib, todir:p.latex.auxDir, overwrite:true, force:true)
    p.ant.exec(executable: obj.bibCommand, dir: p.latex.auxDir, failonerror: true) {
      arg(value: obj.name)
    }
  }
  
  
  /**
   * Execute inkscape for an image file, to produce a pdf.
   *
   * @param imgFile source svg/emf file for inkscape
   * @param pdfFile target pdf file
   */
  void inkscape(File imgFile, File pdfFile) {
    p.ant.exec(executable: 'inkscape', dir: p.projectDir, failonerror: true) {
      arg(value: "--export-pdf=${pdfFile}")
      arg(value: imgFile)
    }
  }
  
  
  /**
   * Copies output of pdfLatex to final destination as described in a latex artifact.
   * Should be called right after pdfLatex.
   * 
   * @param obj Any Latex artifact with the name and pdf properties set.
   */
  void copyOutput(LatexArtifact obj) {
    File src = new File("${p.latex.auxDir}/${obj.name}.pdf")
    LOG.quiet "Copying $src to ${obj.pdf}"
    p.ant.copy(file: src, tofile:obj.pdf, overwrite:true, force:true)
  }
  
  /**
   * Use ant to delete all files from a directory.
   * 
   * @param dir Directory to empty
   */
  void emptyContent(File dir) {
    LOG.quiet "Emptying content from folder $dir"
    p.ant.delete {
      fileset(dir: dir, includes:'**/*')
    }
  }
  
  
  /**
   * Reduces a FileCollection so that if directories are included,
   * only unsupported image files (svg/emf) for inkscape are tagged.
   */
  FileCollection findImgFiles(Collection<String> fileNames) {
    p.files(fileNames.collect { String imgFile ->
      if (p.file(imgFile).directory) {
        p.fileTree(dir: imgFile, include: ['**/*.svg', '**/*.emf'])
      } else {
        imgFile
      }
    })
  }
  
  /**
   * Creates expected pdf file based on image file. 
   */
  File imgFileToPdfFile(File imgFile) {
    new File("$imgFile".take("$imgFile".lastIndexOf('.')) + '.pdf')
  }
}
