package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "STATUS")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Status.findAll", query = "SELECT s FROM Status s")})
public class Status implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "STATUS_ID")
  private Long statusId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 30)
  @Column(name = "STATUS")
  private String status;
  
  public Status() {
  }

  public Status(Long statusId) {
    this.statusId = statusId;
  }

  public Status(Long statusId, String status) {
    this.statusId = statusId;
    this.status = status;
  }

  public Long getStatusId() {
    return statusId;
  }

  public void setStatusId(Long statusid) {
    this.statusId = statusid;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }  

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (statusId != null ? statusId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Status)) {
      return false;
    }
    Status other = (Status) object;
    if ((this.statusId == null && other.statusId != null) || (this.statusId != null && !this.statusId.equals(other.statusId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Status[ statusId=" + statusId + " ]";
  }

}
