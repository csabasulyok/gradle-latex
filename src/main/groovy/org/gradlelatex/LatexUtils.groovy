package org.gradlelatex

import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LatexUtils {
  Logger LOG = LoggerFactory.getLogger(LatexUtils)

  final Project p

  LatexUtils(Project p) {
    this.p = p
  }

  void exec(String cmd) {
    LOG.quiet "Executing $cmd"
    def cmdSplit = cmd.split(' ')
    p.ant.exec(executable: cmdSplit[0], dir: p.projectDir, failonerror: true) {
      cmdSplit[1..-1].each { argv ->
        arg(value: argv)
      }
    }
  }

  void pdfLatex(LatexObj obj) {
    exec "pdflatex -aux-directory=${p.latex.auxDir} -job-name=${obj.jobName} ${p.latex.quiet?'-quiet':''} ${obj.extraArgs} ${obj.tex}"
  }

  void bibTex(LatexObj obj) {
    exec "bibtex ${p.latex.quiet?'-quiet':''} ${p.latex.auxDir}/${obj.jobName}"
  }

  void emptyContent(File dir) {
    LOG.quiet "Emptying content from folder $dir"
    p.ant.delete {
      fileset(dir: dir, includes:'**/*')
    }
  }
}
