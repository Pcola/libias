package sk.atos.fri.rest.model;

/**
 *
 * @author a605053
 */
public enum ImageType {
  ORIG_IMAGE(0),
  CANVAS_IMAGE(1); 

  private final int value;    

  private ImageType(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}
