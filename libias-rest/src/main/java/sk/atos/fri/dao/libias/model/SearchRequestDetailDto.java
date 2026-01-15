package sk.atos.fri.dao.libias.model;

import java.util.Date;
import java.util.List;

public class SearchRequestDetailDto {

    private Long requestId;
    private Date dateCreated;
    private String createdBy;
    private Date dateModified;
    private String modifiedBy;
    private Integer maxCandidates;
    private Double minScore;
    private String transformation;
    private String bilddaten;

    private List<SearchRequestCandidateDto> candidates;

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

    public Integer getMaxCandidates() {
        return maxCandidates;
    }

    public void setMaxCandidates(Integer maxCandidates) {
        this.maxCandidates = maxCandidates;
    }

    public Double getMinScore() {
        return minScore;
    }

    public void setMinScore(Double minScore) {
        this.minScore = minScore;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public List<SearchRequestCandidateDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<SearchRequestCandidateDto> candidates) {
        this.candidates = candidates;
    }

    public String getBilddaten() { return bilddaten; }

    public void setBilddaten(String bilddaten) { this.bilddaten = bilddaten; }
}