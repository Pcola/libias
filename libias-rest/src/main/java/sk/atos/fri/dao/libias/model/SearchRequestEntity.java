package sk.atos.fri.dao.libias.model;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "SEARCH_REQUEST")
public class SearchRequestEntity {

    @Id
    @Column(name = "REQUEST_ID", nullable = false, precision = 10, scale = 0)
     @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "search_request_seq")
     @SequenceGenerator(name = "search_request_seq", sequenceName = "SEARCH_REQUEST_SEQ", allocationSize = 1)
    private Long requestId;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED")
    private Date dateCreated;

    @Column(name = "CREATED_BY", length = 20)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIFIED")
    private Date dateModified;

    @Column(name = "MODIFIED_BY", length = 20)
    private String modifiedBy;

    @Column(name = "DIENSTSTELLE_ID", length = 32)
    private String dienststelleId;

    @Lob
    @Column(name = "BILDDATEN")
    private byte[] bilddaten;

    @Column(name = "MAX_CANDIDATES", precision = 3, scale = 0)
    private Integer maxCandidates;

    @Column(name = "MIN_SCORE")
    private Double minScore;

    @Column(name = "MIN_AGE", precision = 3, scale = 0)
    private Integer minAge;
    @Column(name = "GESCHLECHT", length = 1)
    private String geschlecht;

    @Column(name = "STAATSANGEHOERIGKEIT", length = 128)
    private String staatsangehoerigkeit;

    @Column(name = "BEMERKUNG", length = 1000)
    private String bemerkung;

    @Column(name = "TRANSFORMATION", length = 1000)
    private String transformation;

    @OneToMany(mappedBy = "searchRequest", fetch = FetchType.LAZY)
    private List<SearchRequestCandidateEntity> candidates;


    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }

    public Date getDateCreated() { return dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public Date getDateModified() { return dateModified; }
    public void setDateModified(Date dateModified) { this.dateModified = dateModified; }

    public String getModifiedBy() { return modifiedBy; }
    public void setModifiedBy(String modifiedBy) { this.modifiedBy = modifiedBy; }

    public String getDienststelleId() { return dienststelleId; }
    public void setDienststelleId(String dienststelleId) { this.dienststelleId = dienststelleId; }

    public byte[] getBilddaten() { return bilddaten; }
    public void setBilddaten(byte[] bilddaten) { this.bilddaten = bilddaten; }

    public Integer getMaxCandidates() { return maxCandidates; }
    public void setMaxCandidates(Integer maxCandidates) { this.maxCandidates = maxCandidates; }

    public Double getMinScore() { return minScore; }
    public void setMinScore(Double minScore) { this.minScore = minScore; }

    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }

    public String getGeschlecht() { return geschlecht; }
    public void setGeschlecht(String geschlecht) { this.geschlecht = geschlecht; }

    public String getStaatsangehoerigkeit() { return staatsangehoerigkeit; }
    public void setStaatsangehoerigkeit(String staatsangehoerigkeit) { this.staatsangehoerigkeit = staatsangehoerigkeit; }

    public String getBemerkung() { return bemerkung; }
    public void setBemerkung(String bemerkung) { this.bemerkung = bemerkung; }

    public String getTransformation() { return transformation; }
    public void setTransformation(String transformation) { this.transformation = transformation; }

    public List<SearchRequestCandidateEntity> getCandidates() { return candidates; }
    public void setCandidates(List<SearchRequestCandidateEntity> candidates) { this.candidates = candidates; }
}