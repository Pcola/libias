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
@Table(name = "LOG_TYPE")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "LogType.findAll", query = "SELECT l FROM LogType l")})
public class LogType implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "TYPE_ID")
  private Long typeId;
  @Size(max = 50)
  @Column(name = "TYPE")
  private String type;

  public LogType() {
  }

  public LogType(Long typeId) {
    this.typeId = typeId;
  }

  public Long getTypeId() {
    return typeId;
  }

  public void setTypeId(Long typeId) {
    this.typeId = typeId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (typeId != null ? typeId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof LogType)) {
      return false;
    }
    LogType other = (LogType) object;
    if ((this.typeId == null && other.typeId != null) || (this.typeId != null && !this.typeId.equals(other.typeId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.LogType[ typeId=" + typeId + " ]";
  }

}
