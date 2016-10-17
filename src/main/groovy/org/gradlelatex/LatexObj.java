package org.gradlelatex;

import java.io.File;
import java.util.List;

import org.gradle.api.file.FileCollection;

public class LatexObj {
  private File tex;
  private File bib;
  private File pdf;
  private List<String> dependsOn;
  private FileCollection aux;

  public File getTex() {
    return tex;
  }

  public void setTex(File tex) {
    this.tex = tex;
  }

  public File getBib() {
    return bib;
  }

  public void setBib(File bib) {
    this.bib = bib;
  }

  public File getPdf() {
    return pdf;
  }

  public void setPdf(File pdf) {
    this.pdf = pdf;
  }

  public List<String> getDependsOn() {
    return dependsOn;
  }

  public void setDependsOn(List<String> dependsOn) {
    this.dependsOn = dependsOn;
  }

  public FileCollection getAux() {
    return aux;
  }

  public void setAux(FileCollection aux) {
    this.aux = aux;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((tex == null) ? 0 : tex.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LatexObj other = (LatexObj) obj;
    if (tex == null) {
      if (other.tex != null)
        return false;
    } else if (!tex.equals(other.tex))
      return false;
    return true;
  }

}