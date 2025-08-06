package sk.atos.fri.rest.model;

/**
 *
 * @author Jaroslav Kollar
 */
public class WSTestRequest {  
  private Long bildId;
  private String aktenzeichenA;    
  private String aktenzeichenB;    

  public Long getBildId() {
    return bildId;
  }

  public void setBildId(Long bildId) {
    this.bildId = bildId;
  }

  public String getAktenzeichenA() {
    return aktenzeichenA;
  }

  public void setAktenzeichenA(String aktenzeichenA) {
    this.aktenzeichenA = aktenzeichenA;
  }

  public String getAktenzeichenB() {
    return aktenzeichenB;
  }

  public void setAktenzeichenB(String aktenzeichenB) {
    this.aktenzeichenB = aktenzeichenB;
  }
}
