package sk.atos.fri.rest.model;

import javax.validation.constraints.NotNull;

/**
 *
 * @author kristian
 */
public class AnalyzePortraitRequest {
  @NotNull
  private byte[] img;

  public byte[] getImg() {
    return img;
  }

  public void setImg(byte[] img) {
    this.img = img;
  }

}
