package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

/**
 *
 * @author Jaroslav Kollar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdatedApplicants {
  //@JsonProperty("_embedded")
  @JsonProperty("list")
  ArrayList<UpdatedApplicant> updatedApplicants; 

  public ArrayList<UpdatedApplicant> getUpdatedApplicants() {
    return this.updatedApplicants;
  }

  public void setUpdatedApplicants(ArrayList<UpdatedApplicant> updatedApplicants) {
    this.updatedApplicants = updatedApplicants;
  }
}
