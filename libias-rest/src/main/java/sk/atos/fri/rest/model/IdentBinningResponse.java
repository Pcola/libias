package sk.atos.fri.rest.model;

import com.cognitec.IdentificationResult;

/**
 *
 * @author Jaroslav Kollar
 */
public class IdentBinningResponse {
  private IdentificationResult val;
  
  private ImageType imgType;

  public IdentificationResult getVal() {
    return val;
  }

  public void setVal(IdentificationResult val) {
    this.val = val;
  }

  public ImageType getImgType() {
    return imgType;
  }

  public void setImgType(ImageType imgType) {
    this.imgType = imgType;
  }  
}
