package sk.atos.fri.dao.libias.model;

import java.util.Date;

public class SearchRequestListItemDto {

    private Long requestId;
    private Date dateCreated;
    private String createdBy;
    private Date dateModified;
    private String modifiedBy;
    private String dienststelleId;
    private Integer maxCandidates;
    private Double minScore;
    private Integer minAge;
    private String geschlecht;
    private String staatsangehoerigkeit;
    private Double highestScore;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getDienststelleId() {
        return dienststelleId;
    }

    public void setDienststelleId(String dienststelleId) {
        this.dienststelleId = dienststelleId;
    }

    public Double getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(Double highestScore) {
        this.highestScore = highestScore;
    }

    public String getStaatsangehoerigkeit() {
        return staatsangehoerigkeit;
    }

    public void setStaatsangehoerigkeit(String staatsangehoerigkeit) {
        this.staatsangehoerigkeit = staatsangehoerigkeit;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public Integer getMaxCandidates() {
        return maxCandidates;
    }

    public void setMaxCandidates(Integer maxCandidates) {
        this.maxCandidates = maxCandidates;
    }
}
