package sk.atos.fri.common;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public abstract class ImageUtils {

  public static boolean isImagePng(byte[] bytes) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

    ImageInputStream iis = null;
    try {
      iis = ImageIO.createImageInputStream(bis);
      Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

      while (readers.hasNext()) {
        ImageReader read = readers.next();
        String format = read.getFormatName();
        if ("PNG".equalsIgnoreCase(format)) {
          return true;
        }
      }
      return false;
    } finally {
      closeQuietly(iis);
    }
  }

  public static byte[] convertToPng(byte[] bytes) throws IOException {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    ImageInputStream iis = null;

    try {
      iis = ImageIO.createImageInputStream(bis);
      BufferedImage buffImg = ImageIO.read(iis);

      BufferedImage newBufferedImage = new BufferedImage(buffImg.getWidth(), buffImg.getHeight(), BufferedImage.TYPE_INT_RGB);
      newBufferedImage.createGraphics().drawImage(buffImg, 0, 0, Color.WHITE, null);

      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ImageIO.write(newBufferedImage, "png", bos);
      return bos.toByteArray();

    } finally {
      closeQuietly(iis);
    }

  }

  private static void closeQuietly(ImageInputStream iis) {
    if (iis != null) {
      try {
        iis.close();
      } catch (IOException e) {
      }
    }
  }

}
