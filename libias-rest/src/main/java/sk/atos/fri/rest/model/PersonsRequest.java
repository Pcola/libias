package sk.atos.fri.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kristian
 */
public class PersonsRequest {

  @NotNull
  @Size(min = 1)
  private Long[] oids;

  public Long[] getOids() {
    return oids;
  }

  public void setOids(Long[] oids) {
    this.oids = oids;
  }

}
