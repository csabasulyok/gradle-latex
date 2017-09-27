package com.github.csabasulyok.gradlelatex

import static org.junit.Assert.*

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Before
import org.junit.Test

class LatexExtensionTest {
  LatexExtension extension
  Project p
  
  Map defaultArtifactProps = [
    texName : '',
    pdfName : '',
    bibName : null,
    dependsOn : [],
    img : null,
    aux : null,
    extraArgs : ''
  ]
  
  @Before
  void before() {
    p = ProjectBuilder.builder().withProjectDir(new File('.')).build()
    p.apply plugin: 'latex'
    extension = p.latex
  }
  
  @Test
  void tex_addsArtifactByNameOnly() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex('example.tex')
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf')
  }
  
  @Test
  void tex_addsArtifactByNameOnly_spaceInName() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex('ex ample.tex')
    // artifact is set properly
    assertArtifactProps(obj, name: 'ex ample', nameNoPath: 'ex_ample', texName: 'ex ample.tex', pdfName: 'ex ample.pdf')
  }
  
  @Test
  void tex_addsArtifactByNameOnly_texInSubfolder() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex('subdir/example.tex')
    // artifact is set properly
    assertArtifactProps(obj, name: 'subdir/example', nameNoPath: 'subdir_example', texName: 'subdir/example.tex', pdfName: 'subdir/example.pdf')
  }
  
  @Test
  void tex_addsSimpleArtifact() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'example.tex')
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf')
    // tasks are added
    assertTrue(p.tasks['pdfLatex.example'] instanceof PdfLatexTask)
    assertTrue(p.tasks['cleanLatex.example'] instanceof CleanLatexTask)
    assertTaskDependsOn('pdfLatex', 'pdfLatex.example')
    assertTaskDependsOn('cleanLatex', 'cleanLatex.example')
  }
  
  @Test
  void tex_addsSimpleArtifact_spaceInName() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'ex ample.tex')
    // artifact is set properly
    assertArtifactProps(obj, name: 'ex ample', nameNoPath: 'ex_ample', texName: 'ex ample.tex', pdfName: 'ex ample.pdf')
    // tasks are added
    assertTrue(p.tasks['pdfLatex.ex_ample'] instanceof PdfLatexTask)
    assertTrue(p.tasks['cleanLatex.ex_ample'] instanceof CleanLatexTask)
    assertTaskDependsOn('pdfLatex', 'pdfLatex.ex_ample')
    assertTaskDependsOn('cleanLatex', 'cleanLatex.ex_ample')
  }
  
  @Test
  void tex_addsSimpleArtifact_texInSubfolder() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'subdir/example.tex')
    // artifact is set properly
    assertArtifactProps(obj, name: 'subdir/example', nameNoPath: 'subdir_example', texName: 'subdir/example.tex', pdfName: 'subdir/example.pdf')
    // tasks are added
    assertTrue(p.tasks['pdfLatex.subdir_example'] instanceof PdfLatexTask)
    assertTrue(p.tasks['cleanLatex.subdir_example'] instanceof CleanLatexTask)
    assertTaskDependsOn('pdfLatex', 'pdfLatex.subdir_example')
    assertTaskDependsOn('cleanLatex', 'cleanLatex.subdir_example')
  }
  
  @Test
  void tex_addsArtifactCustomPdfName() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'example.tex', pdf: 'customOutput.pdf')
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'customOutput.pdf')
  }
  
  @Test
  void tex_addsArtifactWithDependent() {
    // add new artifact by tex name
    LatexArtifact dep = extension.tex(tex: 'dependent.tex')
    LatexArtifact obj = extension.tex(tex: 'example.tex', dependsOn:['dependent.tex'])
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf', dependsOn: [dep])
    // tasks are added and dependent
    assertTrue(p.tasks['pdfLatex.dependent'] instanceof PdfLatexTask)
    assertTrue(p.tasks['pdfLatex.example'] instanceof PdfLatexTask)
    assertTaskDependsOn('pdfLatex.example', 'pdfLatex.dependent')
  }
  
  @Test
  void tex_addsArtifactWithBib() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'example.tex', bib: 'refs.bib')
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf', bibName: 'refs.bib')
    // tasks are added and dependent
    assertTrue(p.tasks['pdfLatex.example'] instanceof PdfLatexTask)
    assertTrue(p.tasks['bibTex.example'] instanceof BibTexTask)
    assertTaskDependsOn('pdfLatex.example', 'bibTex.example')
  }
  
  @Test
  void tex_addsArtifactWithImg() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'example.tex', img:['image.svg'])
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf', img:['image.svg'])
    // tasks are added and dependent
    assertTrue(p.tasks['pdfLatex.example'] instanceof PdfLatexTask)
    assertTrue(p.tasks['inkscape.example'] instanceof InkscapeTask)
    assertTaskDependsOn('pdfLatex.example', 'inkscape.example')
  }
  
  @Test
  void tex_addsArtifactWithAux() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'example.tex', aux: ['auxFile.jpg'])
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf', aux: ['auxFile.jpg'])
  }
  
  @Test
  void tex_addsArtifactWithExtraArgs() {
    // add new artifact by tex name
    LatexArtifact obj = extension.tex(tex: 'example.tex', extraArgs: '--extra-arg')
    // artifact is set properly
    assertArtifactProps(obj, name: 'example', nameNoPath: 'example', texName: 'example.tex', pdfName: 'example.pdf', extraArgs: '--extra-arg')
  }
  
  // Auxiliary methods
  
  private void assertTaskDependsOn(String taskName, String dependentTaskName) {
    assertTrue(p.tasks[taskName].dependsOn.contains(p.tasks[dependentTaskName]))
  }
  
  private void assertArtifactProps(Map customArtifactProps, LatexArtifact obj) {
    Map props = defaultArtifactProps + customArtifactProps
    
    assertEquals(props.name, obj.name)
    assertEquals(props.nameNoPath, obj.nameNoPath)
    assertEquals(p.file(props.texName), obj.tex)
    assertEquals(p.file(props.pdfName), obj.pdf)
    assertEquals(props.dependsOn, obj.dependsOn)
    assertEquals(props.extraArgs, obj.extraArgs)
    
    if (!props.bibName) {
      assertNull(obj.bib)
    } else {
      assertEquals(p.file(props.bibName), obj.bib)
    }
    
    if (!props.img) {
      assertNull(obj.img)
    } else {
      props.img.each { assertTrue(obj.img.contains(p.file(it))) }
    }
    
    if (!props.aux) {
      assertNull(obj.aux)
    } else {
      props.aux.each { assertTrue(obj.aux.contains(p.file(it))) }
    }
  }
}