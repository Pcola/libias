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
public class UpdatedApplicant {
  private Long applicantOid;
  private Date dateModified;
  
  @JsonProperty("applicantOid")
  public Long getApplicantOid() {
    return applicantOid;
  }

  @JsonProperty("antragstellerOid")
  public void setApplicantOid(Long applicantOid) {
    this.applicantOid = applicantOid;
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
    return "sk.atos.fri.ws.maris.model.UpdatedApplicant[ "            
            + " applicantOid=" + applicantOid + ", "            
            + " dateModified=" + dateModified + " ]";
  }
}
