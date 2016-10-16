package org.gradlelatex

import java.io.File
import java.util.LinkedHashMap
import java.util.Map

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

import groovy.lang.Closure;

/**
 * Gradle extension to hold all latex-file-related data.
 */
class LatexExtension {
    final Project p
    final File auxDir

    boolean quiet = true
    boolean cleanTemp = false

    private LinkedHashMap<String, LatexObj> objs = [:]

    LatexExtension(Project p) {
        this.p = p;
        this.auxDir = new File('.gradle/latex-temp')
    }

    void tex(String texName) {
        tex([tex: texName])
    }

    void tex(Map args) {
        objs[args.tex] = new LatexObj()
        objs[args.tex].tex = new File(args.tex)
        objs[args.tex].pdf = new File(args.pdf ?: args.tex.take(args.tex.lastIndexOf('.')))
        objs[args.tex].bib = args.bib ? new File(args.bib) : null
        objs[args.tex].dependsOn = args.dependsOn
        objs[args.tex].aux = args.aux ? p.files(args.aux) : null
    }

    LinkedHashMap<String, LatexObj> withBib() {
        return objs.findAll { tex, obj -> obj.bib }
    }

    FileCollection getTexInputs() {
        p.files(objs.collect { tex, obj -> obj.tex })
    }

    FileCollection getBibInputs() {
        p.files(withBib().collect { tex, obj -> obj.bib })
    }

    void withRecursive(def touched, String tex, Closure closure) {
        if (!touched.contains(tex)) {
            objs[tex].dependsOn.each { dependentTex ->
                withRecursive(touched, dependentTex, closure)
            }
            objs[tex].with(closure)
            touched << tex
        }
    }

    void withRecursive(String tex, Closure closure) {
        objs[tex].dependsOn.each { dependentTex ->
            withRecursive(dependentTex, closure)
        }
        objs[tex].with(closure)
    }

    void eachObj(Closure closure) {
        def touched = []
        objs.each { tex, obj ->
            withRecursive(touched, tex, closure)
        }
    }
}