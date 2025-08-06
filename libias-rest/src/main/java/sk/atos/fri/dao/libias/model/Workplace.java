package sk.atos.fri.dao.libias.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "DIENSTSTELLE")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Dienststelle.findAll", query = "SELECT w FROM Workplace w")})
public class Workplace implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 32)
  @Column(name = "ID")
  private String id;
  @Size(max = 32)
  @Column(name = "DIENSTSTELLE")
  private String workplace;

  @Size(max = 64)
  @Column(name = "IDM_ROLE")
  private String idm_role;
   
  public Workplace() {
  }

  public Workplace(String id, String workplace, String idm_role) {
    this.id = id;
    this.workplace = workplace;
    this.idm_role = idm_role;
  }

  public Workplace(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getWorkplace() {
    return workplace;
  }

  public void setWorkplace(String workplace) {
    this.workplace = workplace;
  }

  public String getIdm_role() {
    return idm_role;
  }

  public void setIdm_role(String idm_role) {
    this.idm_role = idm_role;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Workplace)) {
      return false;
    }
    Workplace other = (Workplace) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Workplace[ id=" + id + " ]";
  }
}
