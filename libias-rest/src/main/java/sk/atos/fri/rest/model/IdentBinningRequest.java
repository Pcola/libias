package sk.atos.fri.rest.model;

import javax.validation.constraints.NotNull;

public class IdentBinningRequest {

  @NotNull
  private byte[] img;
  private ImageType imgType;
  private int maxMatches;
  private int minScore;

  public byte[] getImg() {
    return img;
  }

  public void setImg(byte[] img) {
    this.img = img;
  }

  public ImageType getImgType() {
    return imgType;
  }

  public void setImgType(ImageType imgType) {
    this.imgType = imgType;
  }

  public int getMaxMatches() {
    return maxMatches;
  }

  public void setMaxMatches(int maxMatches) {
    this.maxMatches = maxMatches;
  }

  public int getMinScore() {
    return minScore;
  }

  public void setMinScore(int minScore) {
    this.minScore = minScore;
  }

}
