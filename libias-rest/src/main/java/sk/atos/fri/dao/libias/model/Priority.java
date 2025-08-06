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
 * @author Jaroslav Kollar
 */
@Entity
@Table(name = "PRIORITY")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Priority.findAll", query = "SELECT p FROM Priority p")})
public class Priority implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "PRIORITY_ID")
  private Long priorityId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 30)
  @Column(name = "PRIORITY")
  private String priority;
  
  public Priority() {
  }

  public Priority(Long priorityId) {
    this.priorityId = priorityId;
  }

  public Priority(Long priorityId, String priority) {
    this.priorityId = priorityId;
    this.priority = priority;
  }

  public Long getPriorityId() {
    return priorityId;
  }

  public void setPriorityId(Long priorityId) {
    this.priorityId = priorityId;
  }

  public String getPriority() {
    return priority;
  }

  public void setPriority(String priority) {
    this.priority = priority;
  }
  
  @Override
  public int hashCode() {
    int hash = 0;
    hash += (priorityId != null ? priorityId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Priority)) {
      return false;
    }
    Priority other = (Priority) object;
    if ((this.priorityId == null && other.priorityId != null) || (this.priorityId != null && !this.priorityId.equals(other.priorityId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Priority[ priorityId=" + priorityId + " ]";
  }

}
