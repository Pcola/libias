package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "BILD")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Image.findAll", query = "SELECT i FROM Image i")})
public class Image implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "OID")
  private Long oid;

  @Lob
  @Column(name = "BILDDATEN")
  private byte[] imageData;  

  @Basic(optional = false)
  @NotNull
  @Column(name = "DATE_CREATED")
  @Temporal(TemporalType.DATE)
  private Date dateCreated;

  @Column(name = "DATE_DELETED")
  @Temporal(TemporalType.DATE)
  private Date dateDeleted;

  public Image() {
  }

  public Long getOid() {
    return oid;
  }

  public void setOid(Long oid) {
    this.oid = oid;
  }

  public byte[] getImageData() {
    return imageData;
  }

  public void setImageData(byte[] imageData) {
    this.imageData = imageData;
  } 

  public Date getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }

  public Date getDateDeleted() {
    return dateDeleted;
  }

  public void setDateDeleted(Date dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (oid != null ? oid.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Image)) {
      return false;
    }
    Image other = (Image) object;
    if ((this.oid == null && other.oid != null) || (this.oid != null && !this.oid.equals(other.oid))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Image[ oid=" + oid + " ]";
  }

}
