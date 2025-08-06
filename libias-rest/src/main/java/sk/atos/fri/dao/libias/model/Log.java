package sk.atos.fri.dao.libias.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kristian
 */
@Entity
@Table(name = "LOG")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l")})
public class Log implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "LOG_ID")
  @SequenceGenerator(name = "LOG_SEQ", sequenceName = "LOG_SEQ", allocationSize = 1, initialValue = 1)
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "LOG_SEQ")
  private BigDecimal logId;
  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 2000)
  @Column(name = "MESSAGE")
  private String message;
  @Basic(optional = false)
  @NotNull
  @Column(name = "TIMESTAMP")
  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp;
  @JoinColumn(name = "TYPE", referencedColumnName = "TYPE_ID")
  @ManyToOne(optional = false)
  private LogType type;
  @Size(max = 10)
  @Column(name = "SEVERITY")
  private String severity;
  @Size(max = 200)
  @Column(name = "METHOD")
  private String method;
  @Size(max = 20)
  @Column(name = "USERNAME")
  private String username;
  
  public Log() {}
  
  public Log(String message, Date timestamp, String username, LogType logType,
      String severity, String method) {    
    this.message = message;
    this.timestamp = timestamp;
    this.username = username;
    this.type = logType;   
    this.severity = severity;
    this.method = method;
  }

  public BigDecimal getLogId() {
    return logId;
  }

  public void setLogId(BigDecimal logId) {
    this.logId = logId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public LogType getType() {
    return type;
  }

  public void setType(LogType type) {
    this.type = type;
  }
  
  public void setSeverity(String severity) {
    this.severity = severity;
  }

  public String getSeverity() {
    return severity;
  }
  
  public void setMethod(String method) {
    this.method = method;
  }

  public String getMethod() {
    return method;
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
    hash += (logId != null ? logId.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Log)) {
      return false;
    }
    Log other = (Log) object;
    if ((this.logId == null && other.logId != null) || (this.logId != null && !this.logId.equals(other.logId))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "sk.atos.fri.dao.libias.model.Log[ logId=" + logId + " ]";
  }

}
