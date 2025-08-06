package sk.atos.fri.rest.model;

/**
 *
 * @author kristian
 */
public class SiteStatistics {

  private String siteId;
  private String siteName;
  private Long priorityId;
  private Long statusId;
  private Long count;

  public SiteStatistics(String siteId, String siteName, Long priorityId, Long statusId, Long count) {
    this.siteId = siteId;
    this.siteName = siteName;
    this.priorityId = priorityId;
    this.statusId = statusId;
    this.count = count;
  }

  public SiteStatistics() {
  }

  public String getSiteId() {
    return siteId;
  }

  public void setSiteId(String siteId) {
    this.siteId = siteId;
  }

  public String getSiteName() {
    return siteName;
  }

  public void setSiteName(String siteName) {
    this.siteName = siteName;
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

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }

}
