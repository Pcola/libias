package sk.atos.fri.rest.model;

/**
 *
 * @author kristian
 */
public class FinishCaseRequest {

  private Long caseId;
  private String workplaceNote;

  public Long getCaseId() {
    return caseId;
  }

  public void setCaseId(Long caseId) {
    this.caseId = caseId;
  }

  public String getWorkplaceNote() {
    return workplaceNote;
  }

  public void setWorkplaceNote(String workplaceNote) {
    this.workplaceNote = workplaceNote;
  }

}
