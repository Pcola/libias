package sk.atos.fri.common;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public abstract class Utils {

  public static String booleanConverter(Boolean b) {
    return b != null & b ? "yes" : "no";
  }

  public static BufferedImage getImage(byte[] bytes) throws IOException {
    return getImage(new ByteArrayInputStream(bytes));
  }

  public static BufferedImage getImage(InputStream is) throws IOException {
    BufferedImage bImageFromConvert = ImageIO.read(is);
    return bImageFromConvert;
  }

}
