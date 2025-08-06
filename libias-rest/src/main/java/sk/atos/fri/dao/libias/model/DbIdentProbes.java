package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name = "IDENT_PROBES")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "DbIdentProbes.findAll", query = "SELECT p FROM DbIdentProbes p ORDER BY p.probeid")
})
public class DbIdentProbes implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "PROBEID")
  private String probeid;

  @Lob
  @Column(name = "IMG")
  private byte[] imgData;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "PROBEID")
  @OrderBy("rank")
  private List<DbIdentResults> results;

  public DbIdentProbes() {
  }

  public String getProbeid() {
    return probeid;
  }

  public void setProbeid(String probeid) {
    this.probeid = probeid;
  }

  public byte[] getImgData() {
    return imgData;
  }

  public void setImgData(byte[] imgData) {
    this.imgData = imgData;
  }

  public List<DbIdentResults> getResults() {
    return results;
  }

  public void setResults(List<DbIdentResults> results) {
    this.results = results;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (probeid != null ? probeid.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof DbIdentProbes)) {
      return false;
    }
    DbIdentProbes other = (DbIdentProbes) object;
    if ((this.probeid == null && other.probeid != null) || (this.probeid != null && !this.probeid.equals(other.probeid))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.DbIdentProbes[ probeid=" + probeid + " ]";
  }

}
