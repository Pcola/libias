package sk.atos.fri.rest.model;

import javax.validation.constraints.NotNull;

public class SearchReportRequest {

  @NotNull
  private Long imageOid;
  @NotNull
  private String extImageOriginal;
  @NotNull
  private String extImageOptimized;
  @NotNull
  private String marisImageOptimized;
  @NotNull
  private String compImageOptimized;
  private String note;
  @NotNull
  private Double score;
  private String lang;
  private Boolean isFullName;
  private Boolean isFull;
  private Boolean isWord;

  public SearchReportRequest() {
  }

  public Long getImageOid() {
    return imageOid;
  }

  public void setImageOid(Long imageOid) {
    this.imageOid = imageOid;
  }

  public String getExtImageOriginal() {
    return extImageOriginal;
  }

  public void setExtImageOriginal(String extImageOriginal) {
    this.extImageOriginal = extImageOriginal;
  }

  public String getExtImageOptimized() {
    return extImageOptimized;
  }

  public void setExtImageOptimized(String extImageOptimized) {
    this.extImageOptimized = extImageOptimized;
  }

  public String getMarisImageOptimized() {
    return marisImageOptimized;
  }

  public void setMarisImageOptimized(String marisImageOptimized) {
    this.marisImageOptimized = marisImageOptimized;
  }

  public String getCompImageOptimized() {
    return compImageOptimized;
  }

  public void setCompImageOptimized(String compImageOptimized) {
    this.compImageOptimized = compImageOptimized;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public String getLang() {
    return lang;
  }

  public void setLang(String lang) {
    this.lang = lang;
  }

  public Boolean isFullName() {
    return isFullName;
  }

  public void setIsFullName(Boolean isFullName) {
    this.isFullName = isFullName;
  }

  public Boolean isFull() {
    return isFull;
  }

  public void setIsFull(Boolean isFull) {
    this.isFull = isFull;
  }

  public Boolean isWord() {
    return isWord;
  }

  public void setIsWord(Boolean isWord) {
    this.isWord = isWord;
  }

}
