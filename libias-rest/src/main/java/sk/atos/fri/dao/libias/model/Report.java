package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "REPORT")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Report.findAll", query = "SELECT r FROM Report r")})
public class Report implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @Column(name = "ID")
  @SequenceGenerator(name = "REPORT_SEQ", sequenceName = "REPORT_SEQ", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "REPORT_SEQ")
  private Long id;

  @Lob
  @Column(name = "REPORT")
  private byte[] report;
  @Column(name = "CREATED")
  @Temporal(TemporalType.TIMESTAMP)
  private Date created;
  @Column(name = "CASE_ID")
  private long caseId;
  @Size(max = 20)
  @Column(name = "USERNAME")
  private String username;

  public Report() {
  }

  public Report(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public byte[] getReport() {
    return report;
  }

  public void setReport(byte[] report) {
    this.report = report;
  }

  public Date getCreated() {
    return created;
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public long getCaseId() {
    return caseId;
  }

  public void setCaseId(long caseId) {
    this.caseId = caseId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Report)) {
      return false;
    }
    Report other = (Report) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Report[ id=" + id + " ]";
  }

}
