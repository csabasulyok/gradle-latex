package org.gradlelatex

import org.gradle.api.Project

class LatexUtils {

  final Project p

  LatexUtils(Project p) {
    this.p = p
  }

  void exec(String cmd) {
    println "Executing $cmd"
    def cmdSplit = cmd.split(' ')
    p.ant.exec(executable: cmdSplit[0], dir: p.projectDir) {
      cmdSplit[1..-1].each { argv ->
        arg(value: argv)
      }
    }
  }
}
