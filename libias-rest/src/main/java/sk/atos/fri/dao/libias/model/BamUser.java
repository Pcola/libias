package sk.atos.fri.dao.libias.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
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
@Table(name = "BAM_USER")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "BamUser.findAll", query = "SELECT b FROM BamUser b")})
public class BamUser implements Serializable {

  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @Column(name = "USER_ID")
  @SequenceGenerator(name = "USER_SEQ", sequenceName = "USER_SEQ", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "USER_SEQ")
  @JsonIgnore
  private Long userId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 20)
  @Column(name = "USERNAME")
  private String username;
  @Size(min = 1, max = 100)
  @Column(name = "PASSWORD")
  @JsonIgnore
  private String password;
  @Size(min = 1, max = 100)
  @Column(name = "FIRST_NAME")
  private String firstName;
  @Size(min = 1, max = 100)
  @Column(name = "LAST_NAME")
  private String lastName;
  @Column(name = "ACTIVE")
  private short active; 

  @Basic
  @Column(name = "PUBLIC_SECRET")
  @Size(max = 100)
  @JsonIgnore
  private String publicSecret;

  @Basic
  @Column(name = "PRIVATE_SECRET")
  @Size(max = 100)
  @JsonIgnore
  private String privateSecret;

  @JsonInclude()
  @Transient
  private Collection<UserRole> userRoleCollection = new ArrayList<>();

  @JsonInclude()
  @Size(min = 1, max = 32)
  @Column(name = "DIENSTSTELLE_ID")
  private String workplaceId;

  @JsonInclude()
  @Transient
  private Collection<Workplace> workplace = new ArrayList<>();

  public BamUser() {
  }

  public BamUser(Long userId) {
    this.userId = userId;
  }

  public BamUser(Long userId, String username, String password, String firstName, String lastName, short active) {
    this.userId = userId;
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.active = active;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public short getActive() {
    return active;
  }

  public void setActive(short active) {
    this.active = active;
  }

  public void setActive(boolean active) {
    this.active = active ? (short) 1 : (short) 0;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (userId != null ? userId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof BamUser)) {
      return false;
    }
    BamUser other = (BamUser) object;
    if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {    
    String rolesStr = "";   
    ArrayList<UserRole> roles = new ArrayList<>(userRoleCollection); 
    for (int i = 0; i < roles.size(); i++) {
      if (i == roles.size() - 1) {
        rolesStr += String.valueOf(roles.get(i).getRoleId());
      } else {
        rolesStr += String. valueOf(roles.get(i).getRoleId()) + ", ";
      }
    }              
    
    return "sk.atos.fri.dao.libias.model.BamUser[ userId=" + userId + ", username=" + username + ", firstName=" + firstName + ", lastName=" + lastName + ", workplaceId=" + workplaceId + ", active=" + String.valueOf(active) + ", roles=" + rolesStr + "]";
  }

  public String getPublicSecret() {
    return publicSecret;
  }

  public void setPublicSecret(String publicSecret) {
    this.publicSecret = publicSecret;
  }

  public String getPrivateSecret() {
    return privateSecret;
  }

  public void setPrivateSecret(String privateSecret) {
    this.privateSecret = privateSecret;
  }

  @XmlTransient
  public Collection<UserRole> getUserRoleCollection() {
    return userRoleCollection;
  }

  public void setUserRoleCollection(Collection<UserRole> userRoleCollection) {
    this.userRoleCollection = userRoleCollection;
  }

  public String getWorkplaceId() {
    return workplaceId;
  }

  public void setWorkplaceId(String workplaceId) {
    this.workplaceId = workplaceId;
  }

  public Collection<Workplace> getWorkplace() {
    return workplace;
  }

  public void setWorkplace(Collection<Workplace> workplace) {
    this.workplace = workplace;
  }
}
