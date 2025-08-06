package sk.atos.fri.rest.model;

import javax.validation.constraints.NotNull;

/**
 *
 * @author kristian
 */
public class VerificationPortraitsRequest {
  @NotNull
  private byte[] img1;
  @NotNull
  private byte[] img2;

  public byte[] getImg1() {
    return img1;
  }

  public void setImg1(byte[] img1) {
    this.img1 = img1;
  }

  public byte[] getImg2() {
    return img2;
  }

  public void setImg2(byte[] img2) {
    this.img2 = img2;
  }

}
