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
 * @author Tomas Simon
 */
@Entity
@Table(name = "NATIONALITY")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Nationality.findAll", query = "SELECT n FROM Nationality n")
})
public class Nationality implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "NATIONALITY_ID")
  private Long nationalityId;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 128)
  @Column(name = "NATIONALITY")
  private String nationality;

  public Nationality() {
  }

  public Nationality(Long nationalityId) {
    this.nationalityId = nationalityId;
  }

  public Nationality(Long nationalityId, String nationality) {
    this.nationalityId = nationalityId;
    this.nationality = nationality;
  }

  public Long getNationalityId() {
    return nationalityId;
  }

  public void setNationalityId(Long nationalityId) {
    this.nationalityId = nationalityId;
  }

  public String getNationality() {
    return nationality;
  }

  public void setNationality(String nationality) {
    this.nationality = nationality;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (nationalityId != null ? nationalityId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Nationality)) {
      return false;
    }
    Nationality other = (Nationality) object;
    if ((this.nationalityId == null && other.nationalityId != null) || (this.nationalityId != null && !this.nationalityId.equals(other.nationalityId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Nationality[ nationalityId=" + nationalityId + " ]";
  }

}
