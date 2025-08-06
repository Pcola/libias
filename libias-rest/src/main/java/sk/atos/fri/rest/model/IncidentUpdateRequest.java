package sk.atos.fri.rest.model;

/**
 *
 * @author kristian
 */
public class IncidentUpdateRequest {

  private Long caseId;
  private Long priorityId;
  private Long statusId;
  private String note;
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

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusId) {
    this.statusId = statusId;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getWorkplaceId() {
    return workplaceId;
  }

  public void setWorkplaceId(String workplaceId) {
    this.workplaceId = workplaceId;
  }

}
