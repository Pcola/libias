package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;


/**
 *
 * @author Jaroslav Kollar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Record {  
  @JsonProperty("aktenreferenzOid")
  private Long fileReference;
  @JsonProperty("aktenzeichenA")
  private String fileNumberA;
  @JsonProperty("aktenzeichenB")
  private String fileNumberB;
  @JsonProperty("referenzBezeichnung")
  private String referenceType;
  private Date dateModified;  

  public Long getFileReference() {
    return fileReference;
  }

  public void setFileReference(Long fileReference) {
    this.fileReference = fileReference;
  }

  public String getFileNumberA() {
    return fileNumberA;
  }

  public void setFileNumberA(String fileNumberA) {
    this.fileNumberA = fileNumberA;
  }

  public String getFileNumberB() {
    return fileNumberB;
  }

  public void setFileNumberB(String fileNumberB) {
    this.fileNumberB = fileNumberB;
  }

  public String getReferenceType() {
    return referenceType;
  }

  public void setReferenceType(String referenceType) {
    this.referenceType = referenceType;
  }

  @JsonFormat(pattern="dd.MM.yyyy")
  public Date getDateModified() {
    return dateModified;
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  public void setDateModified(Date dateModified) {
    this.dateModified = dateModified;
  }
  
  @Override
  public String toString() {
    return "sk.atos.fri.ws.maris.model.Record[ "
            + " fileReference=" + fileReference + ", "
            + " fileNumberA=" + fileNumberA + ", "
            + " fileNumberB=" + fileNumberB + ", "
            + " referenceType=" + referenceType + ", "
            + " dateModified=" + dateModified + " ]";
  }
}
