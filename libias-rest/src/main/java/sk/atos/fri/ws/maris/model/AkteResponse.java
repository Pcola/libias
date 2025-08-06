package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;

/**
 *
 * @author Jaroslav Kollar
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AkteResponse {
  //@JsonProperty("_embedded")
  @JsonProperty("list")
  ArrayList<Record> records; 

  public ArrayList<Record> getRecords() {
    return records;
  }

  public void setRecords(ArrayList<Record> records) {
    this.records = records;
  }
}
