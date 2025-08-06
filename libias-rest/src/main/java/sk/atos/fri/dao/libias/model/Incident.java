package sk.atos.fri.dao.libias.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "INCIDENT")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Incident.findAll", query = "SELECT i FROM Incident i"),
  @NamedQuery(name = "Incident.countAll", query = "SELECT count(i) FROM Incident i WHERE i.filter = 0")
})
public class Incident implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "CASE_ID")
  private Long caseId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "PROBE_ID")
  private Long probeId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "GALLERY_ID")
  private Long galleryId;
  @Basic(optional = false)
  @NotNull
  @Column(name = "SCORE")
  private Double score;
  @Column(name = "RANK")
  private Short rank;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "JOB_ID")
  private String jobid;
  @Column(name = "FILTER")
  private Short filter;
  @Column(name = "PRIORITY_ID", insertable = false, updatable = false)
  private Long priorityId;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "PRIORITY_ID", referencedColumnName = "PRIORITY_ID")  
  private Priority priority;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")  
  private Status status;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "DIENSTSTELLE_ID", referencedColumnName = "ID")  
  private Workplace workplace;
  @Size(max = 1000)
  @Column(name = "BEMERKUNG")
  private String note;
  @Size(max = 1000)
  @Column(name = "AUSSENSTELLE_BEMERKUNG")
  private String workplaceNote;
  @Column(name = "AKTENREFERENZ_OID")
  private Long fileReference;
  @Size(max = 64)
  @Column(name = "REFERENZBEZEICHNUNG")
  private String referenceType;
  @Column(name = "DATE_CREATED")
  @Temporal(TemporalType.DATE)
  private Date createdDate;
  @Column(name = "A_ANTRAGSTELLER_OID")
  private Long aApplicantOid;
  @Column(name = "A_PKZ")
  private Long aPkz;
  @Size(max = 32)
  @Column(name = "A_AKTENZEICHEN")
  private String aFileNumber;
  @Size(max = 12)
  @Column(name = "A_AZRNUMMER")
  private String aAzrNumber;
  @Size(max = 128)
  @Column(name = "A_DNUMMER")
  private String aDNumber;
  @Size(max = 128)
  @Column(name = "A_ENUMMER")
  private String aENumber;
  @Size(max = 128)
  @Column(name = "A_EURODACNR")
  private String aEuroDacNumber;
  @Size(max = 64)
  @Column(name = "A_FAMILIENNAME")
  private String aLastName;
  @Size(max = 64)
  @Column(name = "A_VORNAME")
  private String aFirstName;
  @Column(name = "A_GEBURTSDATUM")
  @Temporal(TemporalType.DATE)
  private Date aBirthDate;
  @Size(max = 1)
  @Column(name = "A_GESCHLECHT")
  private String aGender;
  @Size(max = 64)
  @Column(name = "A_STAATSANGEHOERIGKEIT")
  private String aNationality;
  @Size(max = 64)
  @Column(name = "A_HERKUNFTSLAND")
  private String aOriginCountry;
  @Size(max = 64)
  @Column(name = "A_GEBURTSLAND")
  private String aBirthCountry;
  @Size(max = 64)
  @Column(name = "A_GEBURTSORT")
  private String aBirthPlace;
  @Size(max = 64)
  @Column(name = "A_ANTRAGSTYP")
  private String aApplicantType;
  @Size(max = 64)
  @Column(name = "A_AUSSENSTELLE")
  private String aWorkplace;
  @Column(name = "A_ANTRAGSDATUM")
  @Temporal(TemporalType.DATE)
  private Date aApplicantDate;
  @Column(name = "A_DATE_MODIFIED")
  @Temporal(TemporalType.DATE)
  private Date aDateModified;
  @Column(name = "B_ANTRAGSTELLER_OID")
  private Long bApplicantOid;
  @Column(name = "B_PKZ")
  private Long bPkz;
  @Size(max = 32)
  @Column(name = "B_AKTENZEICHEN")
  private String bFileNumber;
  @Size(max = 12)
  @Column(name = "B_AZRNUMMER")
  private String bAzrNumber;
  @Size(max = 128)
  @Column(name = "B_DNUMMER")
  private String bDNumber;
  @Size(max = 128)
  @Column(name = "B_ENUMMER")
  private String bENumber;
  @Size(max = 128)
  @Column(name = "B_EURODACNR")
  private String bEuroDacNumber;
  @Size(max = 64)
  @Column(name = "B_FAMILIENNAME")
  private String bLastName;
  @Size(max = 64)
  @Column(name = "B_VORNAME")
  private String bFirstName;
  @Column(name = "B_GEBURTSDATUM")
  @Temporal(TemporalType.DATE)
  private Date bBirthDate;
  @Size(max = 1)
  @Column(name = "B_GESCHLECHT")
  private String bGender;
  @Size(max = 64)
  @Column(name = "B_STAATSANGEHOERIGKEIT")
  private String bNationality;
  @Size(max = 64)
  @Column(name = "B_HERKUNFTSLAND")
  private String bOriginCountry;
  @Size(max = 64)
  @Column(name = "B_GEBURTSLAND")
  private String bBirthCountry;
  @Size(max = 64)
  @Column(name = "B_GEBURTSORT")
  private String bBirthPlace;
  @Size(max = 64)
  @Column(name = "B_ANTRAGSTYP")
  private String bApplicantType;
  @Size(max = 64)
  @Column(name = "B_AUSSENSTELLE")
  private String bWorkplace;
  @Column(name = "B_ANTRAGSDATUM")
  @Temporal(TemporalType.DATE)
  private Date bApplicantDate;
  @Column(name = "B_DATE_MODIFIED")
  @Temporal(TemporalType.DATE)
  private Date bDateModified;
  @Column(name = "ROWID", insertable = false, updatable = false)
  private String rowid;
  @Size(max = 100)
  @Column(name = "BEM_LAST_CHANGED_BY")
  private String bemLastChangedBy;
  @Column(name = "BEM_LAST_CHANGED_ON")
  @Temporal(TemporalType.TIMESTAMP)
  private Date bemLastChangedOn;
  @Size(max = 100)
  @Column(name = "AUS_BEM_LAST_CHANGED_BY")
  private String ausLastChangedBy;
  @Column(name = "AUS_BEM_LAST_CHANGED_ON")
  @Temporal(TemporalType.TIMESTAMP)
  private Date ausLastChangedOn;
  @Column(name = "A_PERSON_DELETED")
  @Temporal(TemporalType.DATE)
  private Date aPersonDeleted;
  @Column(name = "B_PERSON_DELETED")
  @Temporal(TemporalType.DATE)
  private Date bPersonDeleted;
  @Column(name = "A_AKTE_DELETED")
  @Temporal(TemporalType.DATE)
  private Date aAkteDeleted;
  @Column(name = "B_AKTE_DELETED")
  @Temporal(TemporalType.DATE)
  private Date bAkteDeleted;
  @Column(name = "A_AKTE_LOCKED")
  @Temporal(TemporalType.DATE)
  private Date aAkteLocked;
  @Column(name = "B_AKTE_LOCKED")
  @Temporal(TemporalType.DATE)
  private Date bAkteLocked;
  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "CASE_ID")
  private List<IncidentHistory> incidentHistory;

  public Incident() {
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (caseId != null ? caseId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Incident other = (Incident) obj;
    if (!Objects.equals(this.caseId, other.caseId)) {
      return false;
    }
    return true;
  }

  public Long getCaseId() {
    return caseId;
  }

  public void setCaseId(Long caseId) {
    this.caseId = caseId;
  }

  public Long getProbeId() {
    return probeId;
  }

  public void setProbeId(Long probeId) {
    this.probeId = probeId;
  }

  public Long getGalleryId() {
    return galleryId;
  }

  public void setGalleryId(Long galleryId) {
    this.galleryId = galleryId;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public Short getRank() {
    return rank;
  }

  public void setRank(Short rank) {
    this.rank = rank;
  }

  public String getJobid() {
    return jobid;
  }

  public void setJobid(String jobid) {
    this.jobid = jobid;
  }

  public Short getFilter() {
    return filter;
  }

  public void setFilter(Short filter) {
    this.filter = filter;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority priority) {
    this.priority = priority;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
  
  public Workplace getWorkplace() {
    return workplace;
  }

  public void setWorkplace(Workplace workplace) {
    this.workplace = workplace;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getWorkplaceNote() {
    return workplaceNote;
  }

  public void setWorkplaceNote(String workplaceNote) {
    this.workplaceNote = workplaceNote;
  }

  public Long getFileReference() {
    return fileReference;
  }

  public void setFileReference(Long fileReference) {
    this.fileReference = fileReference;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  public Date getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Date createdDate) {
    this.createdDate = createdDate;
  }

  public Long getaApplicantOid() {
    return aApplicantOid;
  }

  public void setaApplicantOid(Long aApplicantOid) {
    this.aApplicantOid = aApplicantOid;
  }

  public Long getaPkz() {
    return aPkz;
  }

  public void setaPkz(Long aPkz) {
    this.aPkz = aPkz;
  }

  public String getaFileNumber() {
    return aFileNumber;
  }

  public void setaFileNumber(String aFileNumber) {
    this.aFileNumber = aFileNumber;
  }

  public String getaAzrNumber() {
    return aAzrNumber;
  }

  public void setaAzrNumber(String aAzrNumber) {
    this.aAzrNumber = aAzrNumber;
  }

  public String getaDNumber() {
    return aDNumber;
  }

  public void setaDNumber(String aDNumber) {
    this.aDNumber = aDNumber;
  }

  public String getaENumber() {
    return aENumber;
  }

  public void setaENumber(String aENumber) {
    this.aENumber = aENumber;
  }

  public String getaEuroDacNumber() {
    return aEuroDacNumber;
  }

  public void setaEuroDacNumber(String aEuroDacNumber) {
    this.aEuroDacNumber = aEuroDacNumber;
  }

  public String getaLastName() {
    return aLastName;
  }

  public void setaLastName(String aLastName) {
    this.aLastName = aLastName;
  }

  public String getaFirstName() {
    return aFirstName;
  }

  public void setaFirstName(String aFirstName) {
    this.aFirstName = aFirstName;
  }

  public Date getaBirthDate() {
    return aBirthDate;
  }

  public void setaBirthDate(Date aBirthDate) {
    this.aBirthDate = aBirthDate;
  }

  public String getaGender() {
    return aGender;
  }

  public void setaGender(String aGender) {
    this.aGender = aGender;
  }

  public String getaNationality() {
    return aNationality;
  }

  public void setaNationality(String aNationality) {
    this.aNationality = aNationality;
  }

  public String getaOriginCountry() {
    return aOriginCountry;
  }

  public void setaOriginCountry(String aOriginCountry) {
    this.aOriginCountry = aOriginCountry;
  }

  public String getaBirthCountry() {
    return aBirthCountry;
  }

  public void setaBirthCountry(String aBirthCountry) {
    this.aBirthCountry = aBirthCountry;
  }

  public String getaBirthPlace() {
    return aBirthPlace;
  }

  public void setaBirthPlace(String aBirthPlace) {
    this.aBirthPlace = aBirthPlace;
  }

  public String getaApplicantType() {
    return aApplicantType;
  }

  public void setaApplicantType(String aApplicantType) {
    this.aApplicantType = aApplicantType;
  }

  public String getaWorkplace() {
    return aWorkplace;
  }

  public void setaWorkplace(String aWorkplace) {
    this.aWorkplace = aWorkplace;
  }

  public Date getaApplicantDate() {
    return aApplicantDate;
  }

  public void setaApplicantDate(Date aApplicantDate) {
    this.aApplicantDate = aApplicantDate;
  }

  public Date getaDateModified() {
    return aDateModified;
  }

  public void setaDateModified(Date aDateModified) {
    this.aDateModified = aDateModified;
  }

  public Long getbApplicantOid() {
    return bApplicantOid;
  }

  public void setbApplicantOid(Long bApplicantOid) {
    this.bApplicantOid = bApplicantOid;
  }

  public Long getbPkz() {
    return bPkz;
  }

  public void setbPkz(Long bPkz) {
    this.bPkz = bPkz;
  }

  public String getbFileNumber() {
    return bFileNumber;
  }

  public void setbFileNumber(String bFileNumber) {
    this.bFileNumber = bFileNumber;
  }

  public String getbAzrNumber() {
    return bAzrNumber;
  }

  public void setbAzrNumber(String bAzrNumber) {
    this.bAzrNumber = bAzrNumber;
  }

  public String getbDNumber() {
    return bDNumber;
  }

  public void setbDNumber(String bDNumber) {
    this.bDNumber = bDNumber;
  }

  public String getbENumber() {
    return bENumber;
  }

  public void setbENumber(String bENumber) {
    this.bENumber = bENumber;
  }

  public String getbEuroDacNumber() {
    return bEuroDacNumber;
  }

  public void setbEuroDacNumber(String bEuroDacNumber) {
    this.bEuroDacNumber = bEuroDacNumber;
  }

  public String getbLastName() {
    return bLastName;
  }

  public void setbLastName(String bLastName) {
    this.bLastName = bLastName;
  }

  public String getbFirstName() {
    return bFirstName;
  }

  public void setbFirstName(String bFirstName) {
    this.bFirstName = bFirstName;
  }

  public Date getbBirthDate() {
    return bBirthDate;
  }

  public void setbBirthDate(Date bBirthDate) {
    this.bBirthDate = bBirthDate;
  }

  public String getbGender() {
    return bGender;
  }

  public void setbGender(String bGender) {
    this.bGender = bGender;
  }

  public String getbNationality() {
    return bNationality;
  }

  public void setbNationality(String bNationality) {
    this.bNationality = bNationality;
  }

  public String getbOriginCountry() {
    return bOriginCountry;
  }

  public void setbOriginCountry(String bOriginCountry) {
    this.bOriginCountry = bOriginCountry;
  }

  public String getbBirthCountry() {
    return bBirthCountry;
  }

  public void setbBirthCountry(String bBirthCountry) {
    this.bBirthCountry = bBirthCountry;
  }

  public String getbBirthPlace() {
    return bBirthPlace;
  }

  public void setbBirthPlace(String bBirthPlace) {
    this.bBirthPlace = bBirthPlace;
  }

  public String getbApplicantType() {
    return bApplicantType;
  }

  public void setbApplicantType(String bApplicantType) {
    this.bApplicantType = bApplicantType;
  }

  public String getbWorkplace() {
    return bWorkplace;
  }

  public void setbWorkplace(String bWorkplace) {
    this.bWorkplace = bWorkplace;
  }

  public Date getbApplicantDate() {
    return bApplicantDate;
  }

  public void setbApplicantDate(Date bApplicantDate) {
    this.bApplicantDate = bApplicantDate;
  }

  public Date getbDateModified() {
    return bDateModified;
  }

  public void setbDateModified(Date bDateModified) {
    this.bDateModified = bDateModified;
  }

  public String getBemLastChangedBy() {
    return bemLastChangedBy;
  }

  public void setBemLastChangedBy(String bemLastChangedBy) {
    this.bemLastChangedBy = bemLastChangedBy;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Berlin")
  public Date getBemLastChangedOn() {
    return bemLastChangedOn;
  }

  public void setBemLastChangedOn(Date bemLastChangedOn) {
    this.bemLastChangedOn = bemLastChangedOn;
  }

  public String getAusLastChangedBy() {
    return ausLastChangedBy;
  }

  public void setAusLastChangedBy(String ausLastChangedBy) {
    this.ausLastChangedBy = ausLastChangedBy;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Berlin")
  public Date getAusLastChangedOn() {
    return ausLastChangedOn;
  }

  public void setAusLastChangedOn(Date ausLastChangedOn) {
    this.ausLastChangedOn = ausLastChangedOn;
  }

  public Date getaPersonDeleted() {
    return aPersonDeleted;
  }

  public void setaPersonDeleted(Date aPersonDeleted) {
    this.aPersonDeleted = aPersonDeleted;
  }

  public Date getbPersonDeleted() {
    return bPersonDeleted;
  }

  public void setbPersonDeleted(Date bPersonDeleted) {
    this.bPersonDeleted = bPersonDeleted;
  }

  public Date getaAkteDeleted() {
    return aAkteDeleted;
  }

  public void setaAkteDeleted(Date aAkteDeleted) {
    this.aAkteDeleted = aAkteDeleted;
  }

  public Date getbAkteDeleted() {
    return bAkteDeleted;
  }

  public void setbAkteDeleted(Date bAkteDeleted) {
    this.bAkteDeleted = bAkteDeleted;
  }

  public Date getaAkteLocked() {
    return aAkteLocked;
  }

  public void setaAkteLocked(Date aAkteLocked) {
    this.aAkteLocked = aAkteLocked;
  }

  public Date getbAkteLocked() {
    return bAkteLocked;
  }

  public void setbAkteLocked(Date bAkteLocked) {
    this.bAkteLocked = bAkteLocked;
  }

  public List<IncidentHistory> getIncidentHistory() {
    return incidentHistory;
  }

  public void setIncidentHistory(List<IncidentHistory> incidentHistory) {
    this.incidentHistory = incidentHistory;
  }

}
