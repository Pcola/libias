package sk.atos.fri.dao.libias.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "USER_ROLE")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "UserRole.findAll", query = "SELECT u FROM UserRole u")})
public class UserRole implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "ROLE_ID")
  private Long roleId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 30)
  @Column(name = "ROLE")
  private String role;

  @Size(max = 64)
  @Column(name = "IDM_ROLE")
  private String idm_role;

  @JsonInclude()
  @Transient
  private Collection<BamUser> bamUserCollection;

  public UserRole() {
  }

  public UserRole(Long roleId) {
    this.roleId = roleId;
  }

  public UserRole(Long roleId, String role) {
    this.roleId = roleId;
    this.role = role;
  }

  public UserRole(Long roleId, String role, String idm_role) {
    this.roleId = roleId;
    this.role = role;
    this.idm_role = idm_role;
  }

  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getIdm_role() {
    return idm_role;
  }

  public void setIdm_role(String idm_role) {
    this.idm_role = idm_role;
  }

  @XmlTransient
  public Collection<BamUser> getBamUserCollection() {
    return bamUserCollection;
  }

  public void setBamUserCollection(Collection<BamUser> bamUserCollection) {
    this.bamUserCollection = bamUserCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (roleId != null ? roleId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof UserRole)) {
      return false;
    }
    UserRole other = (UserRole) object;
    if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.UserRole[ roleId=" + roleId + " ]";
  }

}
