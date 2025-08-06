package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "AKTE_LOCKED")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "AkteLocked.findAll", query = "SELECT a FROM AkteLocked a ORDER BY a.fileNumber")
})
public class AkteLocked implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 32)
  @Column(name = "AKTENZEICHEN")
  private String fileNumber;

  @Column(name = "DATE_MODIFIED")
  @Temporal(TemporalType.DATE)
  private Date dateModified;

  public AkteLocked() {
  }

  public String getFileNumber() {
    return fileNumber;
  }

  public void setFileNumber(String fileNumber) {
    this.fileNumber = fileNumber;
  }

  public Date getDateModified() {
    return dateModified;
  }

  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (fileNumber != null ? fileNumber.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof AkteLocked)) {
      return false;
    }
    AkteLocked other = (AkteLocked) object;
    if ((this.fileNumber == null && other.fileNumber != null) || (this.fileNumber != null && !this.fileNumber.equals(other.fileNumber))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.AkteLocked[ fileNumber=" + fileNumber + " ]";
  }

}
