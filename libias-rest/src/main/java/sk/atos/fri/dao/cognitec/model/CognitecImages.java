package sk.atos.fri.dao.cognitec.model;

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

@Entity
@Table(name = "COGNITEC_IMAGES")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "CognitecImages.findAll", query = "SELECT i FROM CognitecImages i"),
  @NamedQuery(name = "CognitecImages.countAll", query = "SELECT count(i) FROM CognitecImages i")
})
public class CognitecImages implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "RECORDID")
  private String recordid;

  @Column(name = "EYERY")
  private Double eyery;
  @Column(name = "EYELX")
  private Double eyelx;
  @Column(name = "EYERX")
  private Double eyerx;
  @Column(name = "EYELY")
  private Double eyely;

  public CognitecImages() {
  }

  public CognitecImages(String recordid) {
    this.recordid = recordid;
  }

  public Double getEyery() {
    return eyery;
  }

  public void setEyery(Double eyery) {
    this.eyery = eyery;
  }

  public Double getEyelx() {
    return eyelx;
  }

  public void setEyelx(Double eyelx) {
    this.eyelx = eyelx;
  }

  public String getRecordid() {
    return recordid;
  }

  public void setRecordid(String recordid) {
    this.recordid = recordid;
  }

  public Double getEyerx() {
    return eyerx;
  }

  public void setEyerx(Double eyerx) {
    this.eyerx = eyerx;
  }

  public Double getEyely() {
    return eyely;
  }

  public void setEyely(Double eyely) {
    this.eyely = eyely;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (recordid != null ? recordid.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof CognitecImages)) {
      return false;
    }
    CognitecImages other = (CognitecImages) object;
    if ((this.recordid == null && other.recordid != null) || (this.recordid != null && !this.recordid.equals(other.recordid))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.cognitec.model.CognitecImages[ recordid=" + recordid + " ]";
  }

}
