package org.gradlelatex

import org.gradle.api.Project
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
   * Execute arbitrary command with the help of ant.
   * 
   * @param cmd Command-line string to be called.
   * @param dir Directory to call command from.
   */
  void exec(String cmd, File dir = p.projectDir) {
    LOG.quiet "Executing $cmd"
    def cmdSplit = cmd.split(' ')
    p.ant.exec(executable: cmdSplit[0], dir: dir, failonerror: true) {
      cmdSplit[1..-1].each { argv ->
        arg(value: argv)
      }
    }
  }

  /**
   * Execute pdflatex command for given latex artifact.
   * All auxiliary files(aux, out, log) and the pdf output is stored in an auxiliary directory.
   * 
   * @param obj Any Latex artifact with the tex property set.
   */
  void pdfLatex(LatexArtifact obj) {
    exec "pdflatex -output-directory=${p.latex.auxDir} ${p.latex.quiet?'-quiet':''} ${obj.extraArgs} ${obj.tex}"
  }

  /**
   * Execute bibtex command for given latex artifact.
   *
   * @param obj Any Latex artifact with the tex and bib properties set.
   */
  void bibTex(LatexArtifact obj) {
    p.ant.copy(file: obj.bib, todir:p.latex.auxDir, overwrite:true, force:true)
    exec "bibtex ${obj.name}", p.latex.auxDir
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
}
