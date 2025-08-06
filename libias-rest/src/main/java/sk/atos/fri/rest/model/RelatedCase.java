package sk.atos.fri.rest.model;

public class RelatedCase {
  private Long caseId;
  private Long pkz1;
  private Long pkz2;

  public RelatedCase() {
  }

  public RelatedCase(Long caseId, Long pkz1, Long pkz2) {
    this.caseId = caseId;
    this.pkz1 = pkz1;
    this.pkz2 = pkz2;
  }

  public Long getCaseId() {
    return caseId;
  }

  public void setCaseId(Long caseId) {
    this.caseId = caseId;
  }

  public Long getPkz1() {
    return pkz1;
  }

  public void setPkz1(Long pkz1) {
    this.pkz1 = pkz1;
  }

  public Long getPkz2() {
    return pkz2;
  }

  public void setPkz2(Long pkz2) {
    this.pkz2 = pkz2;
  }

}
