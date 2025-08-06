package sk.atos.fri.rest.model;

import java.util.List;

public class SearchBulkReportRequest {

  private String extImage;
  private String note;
  private String lang;
  private Boolean isFullName;

  private List<Long> imageOidList;
  private List<Double> scoreList;

  public SearchBulkReportRequest() {
  }

  public String getExtImage() {
    return extImage;
  }

  public void setExtImage(String extImage) {
    this.extImage = extImage;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
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

  public List<Long> getImageOidList() {
    return imageOidList;
  }

  public void setImageOidList(List<Long> imageOidList) {
    this.imageOidList = imageOidList;
  }

  public List<Double> getScoreList() {
    return scoreList;
  }

  public void setScoreList(List<Double> scoreList) {
    this.scoreList = scoreList;
  }

}
