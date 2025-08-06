package sk.atos.fri.rest.model;

import java.io.Serializable;

public class IncidentSearchRequest implements Serializable {

  private Long caseId;  
  private Long priorityId;
  private Long pkz;
  private String createdDate;
  private String firstName;
  private String lastName;
  private Long statusId;
  private String azrNumber;
  private String dNumber;  
  private String refenceType;
  private String fileNumber;
  private String nationality;
  private Boolean showDoubleEvents;
  private Integer first;
  private Integer rows;
  private String sort;
  private Integer order;
  private String workplaceId;
  
  public Long getCaseId() {
      return caseId;
  }
  
  public void setCaseId(Long caseId) {
      this.caseId = caseId;
  }
  
  public Long getPriorityId() {
    return priorityId;
  }
  
  public void setPriorityId(Long priorityId) {
    this.priorityId = priorityId;
  }

  public Long getPkz() {
    return pkz;
  }

  public void setPkz(Long pkz) {
    this.pkz = pkz;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }  

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public String getAzrNumber() {
    return azrNumber;
  }

  public void setAzrNumber(String azrNumber) {
    this.azrNumber = azrNumber;
  }

  public String getdNumber() {
    return dNumber;
  }

  public void setdNumber(String dNumber) {
    this.dNumber = dNumber;
  }

  public String getFileNumber() {
    return fileNumber;
  }

  public void setFileNumber(String fileNumber) {
    this.fileNumber = fileNumber;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  public Boolean getShowDoubleEvents() {
    return showDoubleEvents;
  }

  public void setShowDoubleEvents(Boolean showDoubleEvents) {
    this.showDoubleEvents = showDoubleEvents;
  }

  public String getReferenceType() {
    return refenceType;
  }

  public void setReferenceType(String referenceType) {
    this.refenceType = referenceType;
  }

  public String getWorkplaceId() {
    return workplaceId;
  }

  public void setWorkplaceId(String workplaceId) {
    this.workplaceId = workplaceId;
  }
  
  public Integer getFirst() {
    return first;
  }

  public void setFirst(Integer first) {
    this.first = first;
  }
	
  public Integer getRows() {
    return rows;
  }

  public void setRows(Integer rows) {
	this.rows = rows;
  }

  public String getSort() {
    return sort;
  }

  public void setSort(String sort) {
    this.sort = sort;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }
  
}
