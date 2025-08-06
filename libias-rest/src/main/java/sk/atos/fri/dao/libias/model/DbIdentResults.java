package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "IDENT_RESULTS")
@XmlRootElement
public class DbIdentResults implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "ROWID")
  private String rowid;

  @Basic(optional = false)
  @NotNull
  @Column(name = "PROBEID")
  private String probeid;

  @Basic(optional = false)
  @NotNull
  @Column(name = "GALLERYID")
  private String galleryid;

  @Column(name = "RANK")
  private Short rank;

  @Column(name = "SCORE")
  private Double score;

  @Column(name = "ANTRAGSTELLER_OID")
  private Long applicantOid;

  @Column(name = "PKZ")
  private Long pkz;  

  @Column(name = "AKTENZEICHEN")
  private String fileNumber;  

  @Column(name = "AZRNUMMER")
  private String azrNumber;  

  @Column(name = "DNUMMER")
  private String dNumber;  

  @Column(name = "ENUMMER")
  private String eNumber;  

  @Column(name = "EURODACNR")
  private String euroDacNumber;  

  @Column(name = "FAMILIENNAME")
  private String lastName;  

  @Column(name = "VORNAME")
  private String firstName;    

  @Column(name = "GEBURTSDATUM")
  private Date birthDate;  

  @Column(name = "GEBURTSLAND")
  private String birthCountry;  

  @Column(name = "GEBURTSORT")
  private String birthPlace;  

  @Column(name = "HERKUNFTSLAND")
  private String originCountry;    

  @Column(name = "ANTRAGSDATUM")
  private Date applicantDate;  

  @Column(name = "ANTRAGSTYP")
  private String applicantType;  

  @Column(name = "AUSSENSTELLE")
  private String workplace;  

  @Column(name = "GESCHLECHT")
  private String gender;  

  @Column(name = "STAATSANGEHOERIGKEIT")
  private String nationality;    

  @Column(name = "DATE_MODIFIED")
  private Date dateModified;  

  @Column(name = "PERS_DATA")
  private String persData;

  public DbIdentResults() {
  }

  public String getProbeid() {
    return probeid;
  }

  public void setProbeid(String probeid) {
    this.probeid = probeid;
  }

  public String getGalleryid() {
    return galleryid;
  }

  public void setGalleryid(String galleryid) {
    this.galleryid = galleryid;
  }

  public Short getRank() {
    return rank;
  }

  public void setRank(Short rank) {
    this.rank = rank;
  }

  public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public Long getApplicantOid() {
    return applicantOid;
  }

  public void setApplicantOid(Long applicantOid) {
    this.applicantOid = applicantOid;
  }

  public Long getPkz() {
    return pkz;
  }

  public void setPkz(Long pkz) {
    this.pkz = pkz;
  }

  public String getFileNumber() {
    return fileNumber;
  }

  public void setFileNumber(String fileNumber) {
    this.fileNumber = fileNumber;
  }

  public String getAzrNumber() {
    return azrNumber;
  }

  public void setAzrNumber(String azrNumber) {
    this.azrNumber = azrNumber;
  }

  public String getDNumber() {
    return dNumber;
  }

  public void setDNumber(String dNumber) {
    this.dNumber = dNumber;
  }

  public String getENumber() {
    return eNumber;
  }

  public void setENumber(String eNumber) {
    this.eNumber = eNumber;
  }

  public String getEuroDacNumber() {
    return euroDacNumber;
  }

  public void setEuroDacNumber(String euroDacNumber) {
    this.euroDacNumber = euroDacNumber;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getBirthCountry() {
    return birthCountry;
  }

  public void setBirthCountry(String birthCountry) {
    this.birthCountry = birthCountry;
  }

  public String getBirthPlace() {
    return birthPlace;
  }

  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  public String getOriginCountry() {
    return originCountry;
  }

  public void setOriginCountry(String originCountry) {
    this.originCountry = originCountry;
  }

  public Date getApplicantDate() {
    return applicantDate;
  }

  public void setApplicantDate(Date applicantDate) {
    this.applicantDate = applicantDate;
  }

  public String getApplicantType() {
    return applicantType;
  }

  public void setApplicantType(String applicantType) {
    this.applicantType = applicantType;
  }

  public String getWorkplace() {
    return workplace;
  }

  public void setWorkplace(String workplace) {
    this.workplace = workplace;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public Date getDateModified() {
    return dateModified;
  }

  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

  public String getPersData() {
    return persData;
  }

  public void setPersData(String persData) {
    this.persData = persData;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (probeid != null ? probeid.hashCode() : 0);
    hash += (galleryid != null ? galleryid.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof DbIdentResults)) {
      return false;
    }
    DbIdentResults other = (DbIdentResults) object;
    if ((this.probeid == null && other.probeid != null) || (this.probeid != null && !this.probeid.equals(other.probeid))) {
      return false;
    }
    if ((this.galleryid == null && other.galleryid != null) || (this.galleryid != null && !this.galleryid.equals(other.galleryid))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.DbIdentResults[ probeid=" + probeid + ", galleryid=" + galleryid + " ]";
  }

}
