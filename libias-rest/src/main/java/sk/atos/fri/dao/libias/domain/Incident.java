package sk.atos.fri.dao.libias.domain;

import sk.atos.fri.dao.libias.model.Priority;
import sk.atos.fri.dao.libias.model.Status;
import sk.atos.fri.dao.libias.model.Workplace;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Incident {

    private Long caseId;
    private Long probeId;
    private Long galleryId;
    private Double score;
    private Short rank;
    private String jobid;
    private Short filter;
    private Long priorityId;
    private Priority priority;
    private Status status;
    private Workplace workplace;
    private String note;
    private String workplaceNote;
    private Long fileReference;
    private String referenceType;
    private Date createdDate;
    private Long aApplicantOid;
    private Long aPkz;
    private String aFileNumber;
    private String aAzrNumber;
    private String aDNumber;
    private String aENumber;
    private String aEuroDacNumber;
    private String aLastName;
    private String aFirstName;
    private Date aBirthDate;
    private String aGender;
    private String aNationality;
    private String aOriginCountry;
    private String aBirthCountry;
    private String aBirthPlace;
    private String aApplicantType;
    private String aWorkplace;
    private Date aApplicantDate;
    private Date aDateModified;
    private Long bApplicantOid;
    private Long bPkz;
    private String bFileNumber;
    private String bAzrNumber;
    private String bDNumber;
    private String bENumber;
    private String bEuroDacNumber;
    private String bLastName;
    private String bFirstName;
    private Date bBirthDate;
    private String bGender;
    private String bNationality;
    private String bOriginCountry;
    private String bBirthCountry;
    private String bBirthPlace;
    private String bApplicantType;
    private String bWorkplace;
    private Date bApplicantDate;
    private Date bDateModified;
    private String rowid;
    private String bemLastChangedBy;
    private Date bemLastChangedOn;
    private String ausLastChangedBy;
    private Date ausLastChangedOn;
    private Date aPersonDeleted;
    private Date bPersonDeleted;
    private Date aAkteDeleted;
    private Date bAkteDeleted;
    private Date aAkteLocked;
    private Date bAkteLocked;
    private List<IncidentHistory> incidentHistory;

    public Incident() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Incident incident = (Incident) o;
        return Objects.equals(getCaseId(), incident.getCaseId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getCaseId());
    }

    public Short getFilter() {
        return filter;
    }

    public void setFilter(Short filter) {
        this.filter = filter;
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

    public Long getPriorityId() {
        return priorityId;
    }

    public void setPriorityId(Long priorityId) {
        this.priorityId = priorityId;
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

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
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

    public void handleAkteLocked() {
        if (aAkteLocked != null) {
            aAzrNumber = null;
            aDNumber = null;
            aENumber = null;
            aEuroDacNumber = null;
            aLastName = "Aktentresor";
            aFirstName = null;
            aBirthDate = null;
            aGender = null;
            aNationality = null;
            aOriginCountry = null;
            aBirthCountry = null;
            aBirthPlace = null;
            aApplicantType = null;
            aApplicantDate = null;
            aWorkplace = null;
        }
        if (bAkteLocked != null) {
            bAzrNumber = null;
            bDNumber = null;
            bENumber = null;
            bEuroDacNumber = null;
            bLastName = "Aktentresor";
            bFirstName = null;
            bBirthDate = null;
            bGender = null;
            bNationality = null;
            bOriginCountry = null;
            bBirthCountry = null;
            bBirthPlace = null;
            bApplicantType = null;
            bApplicantDate = null;
            bWorkplace = null;
        }
    }

}
