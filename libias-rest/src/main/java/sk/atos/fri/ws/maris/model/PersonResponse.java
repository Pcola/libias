package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 *
 * @author Jaroslav Kollar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonResponse {

  private Long imageOid;
  private Long applicantOid;
  private Long pkz;
  private String fileNumber;
  private String azrNumber;
  private String dNumber;
  private String eNumber;
  private String euroDacNumber;
  private String lastName;
  private String firstName;
  private Date birthDate;
  private String birthCountry;
  private String birthPlace;
  private String originCountry;
  private Date applicantDate;
  private String applicantType;
  private String workplace;
  private String gender;
  private String nationality;
  private Date dateModified;
  private String fileLocked;

  public PersonResponse () {
  }

  @JsonProperty("applicantOid")
  public Long getApplicantOid() {
    return applicantOid;
  }

  @JsonProperty("antragstellerOid")
  public void setApplicantOid(Long applicantOid) {
    this.applicantOid = applicantOid;
  }

  public Long getImageOid() {
    return imageOid;
  }

  public void setImageOid(Long imageOid) {
    this.imageOid = imageOid;
  }

  @JsonProperty("pkz")
  public Long getPkz() {
    return pkz;
  }

  @JsonProperty("pkz")
  public void setPkz(Long pkz) {
    this.pkz = pkz;
  }

  @JsonProperty("fileNumber")
  public String getFileNumber() {
    return fileNumber;
  }

  @JsonProperty("aktenzeichen")
  public void setFileNumber(String fileNumber) {
    this.fileNumber = fileNumber;
  }

  @JsonProperty("azrNumber")
  public String getAzrNumber() {
    return azrNumber;
  }

  @JsonProperty("azrNummer")
  public void setAzrNumber(String azrNumber) {
    this.azrNumber = azrNumber;
  }

  @JsonProperty("dNumber")
  public String getdNumber() {
    return dNumber;
  }

  @JsonProperty("dnummer")
  public void setdNumber(String dNumber) {
    this.dNumber = dNumber;
  }

  @JsonProperty("eNumber")
  public String geteNumber() {
    return eNumber;
  }

  @JsonProperty("enummer")
  public void seteNumber(String eNumber) {
    this.eNumber = eNumber;
  }

  @JsonProperty("euroDacNumber")
  public String getEuroDacNumber() {
    return euroDacNumber;
  }

  @JsonProperty("euroDacNr")
  public void setEuroDacNumber(String euroDacNumber) {
    this.euroDacNumber = euroDacNumber;
  }

  @JsonProperty("lastName")
  public String getLastName() {
    return lastName;
  }

  @JsonProperty("familienname")
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @JsonProperty("firstName")
  public String getFirstName() {
    return firstName;
  }

  @JsonProperty("vorname")
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  @JsonProperty("birthDate")
  public Date getBirthDate() {
    return birthDate;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  @JsonProperty("geburtsdatum")
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  @JsonProperty("birthCountry")
  public String getBirthCountry() {
    return birthCountry;
  }

  @JsonProperty("geburtsland")
  public void setBirthCountry(String birthCountry) {
    this.birthCountry = birthCountry;
  }

  @JsonProperty("birthPlace")
  public String getBirthPlace() {
    return birthPlace;
  }

  @JsonProperty("geburtsort")
  public void setBirthPlace(String birthPlace) {
    this.birthPlace = birthPlace;
  }

  @JsonProperty("originCountry")
  public String getOriginCountry() {
    return originCountry;
  }

  @JsonProperty("herkunftsland")
  public void setOriginCountry(String originCountry) {
    this.originCountry = originCountry;
  }

  @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  @JsonProperty("applicantDate")
  public Date getApplicantDate() {
    return applicantDate;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  @JsonProperty("antragsDatum")
  public void setApplicantDate(Date applicantDate) {
    this.applicantDate = applicantDate;
  }

  @JsonProperty("applicantType")
  public String getApplicantType() {
    return applicantType;
  }

  @JsonProperty("antragsTyp")
  public void setApplicantType(String applicantType) {
    this.applicantType = applicantType;
  }

  @JsonProperty("workplace")
  public String getWorkplace() {
    return workplace;
  }

  @JsonProperty("aussenstelle")
  public void setWorkplace(String workplace) {
    this.workplace = workplace;
  }

  @JsonProperty("gender")
  public String getGender() {
    return gender;
  }

  @JsonProperty("geschlecht")
  public void setGender(String gender) {
    this.gender = gender;
  }

  @JsonProperty("nationality")
  public String getNationality() {
    return nationality;
  }

  @JsonProperty("staatsangehoerigkeit")
  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  public Date getDateModified() {
    return dateModified;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

  @JsonProperty("fileLocked")
  public String getFileLocked() {
    return fileLocked;
  }

  @JsonProperty("akteGesperrt")
  public void setFileLocked(String fileLocked) {
    this.fileLocked = fileLocked;
  }

  public boolean handleFileLocked() {
    if ("N".equals(fileLocked)) {
      return false;
    }

    azrNumber = null;
    dNumber = null;
    eNumber = null;
    euroDacNumber = null;
    lastName = "Aktentresor";
    firstName = null;
    birthDate = null;
    birthCountry = null;
    birthPlace = null;
    originCountry = null;
    applicantDate = null;
    applicantType = null;
    workplace = null;
    gender = null;
    nationality = null;

    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.ws.maris.model.PersonReponse[ "
            + " imageOid=" + imageOid + ", "
            + " applicantOid=" + applicantOid + ", "
            + " pkz=" + pkz + ", "
            + " fileNumber=" + fileNumber + ", "
            + " fileLocked=" + fileLocked + ", "
            + " azrNumber=" + azrNumber + ", "
            + " dNumber=" + dNumber + ", "
            + " eNumber=" + eNumber + ", "
            + " euroDacNumber=" + euroDacNumber + ", "
            + " lastName=" + lastName + ", "
            + " firstName=" + firstName + ", "
            + " birthDate=" + birthDate + ", "
            + " birthCountry=" + birthCountry + ", "
            + " birthPlace=" + birthPlace + ", "
            + " originCountry=" + originCountry + ", "
            + " applicantDate=" + applicantDate + ", "
            + " applicantType=" + applicantType + ", "
            + " workplace=" + workplace + ", "
            + " gender=" + gender + ", "
            + " nationality=" + nationality + ", "
            + " dateModified=" + dateModified + " ]";
  }

}
