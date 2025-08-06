package sk.atos.fri.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.libias.model.DbIdentProbes;
import sk.atos.fri.dao.libias.model.DbIdentResults;
import sk.atos.fri.dao.libias.service.ImageService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;

@Component
public class DbIdentReport {

  private static final Logger LOG = new Logger();

  private static final ObjectReader READER = new ObjectMapper().readerFor(Map.class);
  private static Map<String, String> EN_TRANSLATION;
  private static Map<String, String> DE_TRANSLATION;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##");

  private static final boolean LANDSCAPE = true;
  private static final float WIDTH_PCTG = 90f;
  private static final int MAX_CANDIDATES = 4;

  @Autowired
  ImageService imageService;

  static {
    try {
      EN_TRANSLATION = READER.readValue(new ClassPathResource("i18n/en.json").getInputStream());
      DE_TRANSLATION = READER.readValue(new ClassPathResource("i18n/de.json").getInputStream());
    } catch (IOException e) {
      LOG.error(Error.READ_FILE_I18M, e);
      throw new RuntimeException(e);
    }
    DECIMAL_FORMAT.setRoundingMode(RoundingMode.HALF_DOWN);
  }

  public byte[] createReport(List<DbIdentProbes> personProbes, String username, String lang) throws DocumentException, IOException {
    if (personProbes == null || personProbes.size() == 0) {
      return null;
    }

    Document document = new Document();
    document.setPageSize(LANDSCAPE ? PageSize.A4.rotate() : PageSize.A4);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);
    document.open();

    for (DbIdentProbes probe : personProbes) {
      document.add(createLogosHeader());
      //document.add(createSeparatorTable(10f));
      document.add(createReportHeader(probe, username, lang));
      document.add(createSeparatorTable(12f));
      document.add(createReportTable(probe, lang));
      document.newPage();
    }

    document.close();
    return outputStream.toByteArray();
  }

  private PdfPTable createLogosHeader() throws DocumentException, IOException {
    PdfPTable table = new PdfPTable(2);
    table.setWidthPercentage(WIDTH_PCTG);

    table.addCell(createImageCell(this.getClass().getClassLoader().getResourceAsStream(Constants.BAMF_LOGO), 12.5f, Element.ALIGN_LEFT));
    table.addCell(createImageCell(this.getClass().getClassLoader().getResourceAsStream(Constants.LIBIAS_LOGO), 80f, Element.ALIGN_RIGHT));

    return table;
  }

  private PdfPTable createReportHeader(DbIdentProbes probe, String username, String lang) /*throws DocumentException*/ {
    Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);

    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(WIDTH_PCTG);
 
    table.addCell(createTextCell(new Phrase(translate(lang, "Bilddatei") + ":", bold), Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(probe.getProbeid(), normal), Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.Datum") + ":", bold), Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(DATE_FORMAT.format(new Date()), normal), Element.ALIGN_LEFT));

    return table;
  }

  private PdfPTable createReportTable(DbIdentProbes probe, String lang) throws DocumentException, IOException {
    Font bold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, GrayColor.GRAYBLACK);
    Font normal = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, GrayColor.GRAYBLACK);

    List<DbIdentResults> results = getFirstResults(probe);

    PdfPTable table = new PdfPTable(MAX_CANDIDATES + 1);
    table.setWidthPercentage(WIDTH_PCTG);

    table.addCell(createTableTextCell(new Phrase(translate(lang, "Externes Bild"), bold), Element.ALIGN_CENTER));
    for (int i = 1; i <= MAX_CANDIDATES; i++) {
      table.addCell(createTableTextCell(new Phrase(translate(lang, "Kandidat" + " " + i), bold), Element.ALIGN_CENTER));
    }

    table.addCell(createTableImageCell(probe.getImgData()));
    for (DbIdentResults result : results) {
      PdfPCell cell;
      sk.atos.fri.dao.libias.model.Image image = imageService.get(Long.parseLong(result.getGalleryid()));
      if (image != null) {
        cell = createTableImageCell(image.getImageData());
      } else {
        cell = createTableTextCell(new Phrase(Constants.IMAGE_DELETED, normal), Element.ALIGN_CENTER);
        cell.setUseAscender(true);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
      }
      table.addCell(cell);
    }
    for (int i = 1 + results.size(); i <= MAX_CANDIDATES; i++) {
      table.addCell(createTableTextCell(new Phrase("", normal), Element.ALIGN_CENTER));
    }

    String[][] data = createReportTableData(results, lang);
    for (int row = 0; row < data.length; row++) {
      for (int column = 0; column < data[0].length; column++) {
   	    table.addCell(createTableTextCell(new Phrase(data[row][column], normal), column == 0 ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT));
      }
    }

    return table;    
  }

  private List<DbIdentResults> getFirstResults(DbIdentProbes probe) {
    List<DbIdentResults> firstResults = new ArrayList<DbIdentResults>(MAX_CANDIDATES);

    for (DbIdentResults result : probe.getResults()) {
      if (result == null || result.getApplicantOid() == null) {
        continue;
      }
      firstResults.add(result);
      if (firstResults.size() == MAX_CANDIDATES) {
        break;
      }
    }

    return firstResults;
  }

  private String[][] createReportTableData(List<DbIdentResults> results, String lang) {
    String[][] data = new String[12][MAX_CANDIDATES + 1];

    data[0][0] = translate(lang, "label.Similarity") + ":";
    data[1][0] = translate(lang, "label.PKZ") + ":";
    data[2][0] = translate(lang, "label.FileNumber") + ":";
    data[3][0] = translate(lang, "label.ApplicantType") + ":";
    data[4][0] = translate(lang, "label.LastName") + ":";
    data[5][0] = translate(lang, "label.FirstName") + ":";
    data[6][0] = translate(lang, "label.BirthDate") + ":";
    data[7][0] = translate(lang, "label.BirthPlace") + ":";
    data[8][0] = translate(lang, "label.Nationality") + ":";
    data[9][0] = translate(lang, "label.Sex") + ":";
    data[10][0] = translate(lang, "label.AZRNumber") + ":";
    data[11][0] = translate(lang, "label.DNumber") + ":";

    int i = 1;
    for (DbIdentResults result : results) {
      data[0][i] = DECIMAL_FORMAT.format(result.getScore() * 100.0) + "%";
      data[1][i] = result.getPkz() != null ? result.getPkz().toString() : "";
      data[2][i] = result.getFileNumber();
      data[3][i] = result.getApplicantType();
      data[4][i] = result.getLastName();
      data[5][i] = result.getFirstName();
      data[6][i] = result.getBirthDate() != null ? DATE_FORMAT.format(result.getBirthDate()) : "";
      data[7][i] = result.getBirthPlace();
      data[8][i] = result.getNationality();
      data[9][i] = result.getGender() != null ? translate(lang, "label.Sex." + result.getGender()) : "";
      data[10][i] = result.getAzrNumber();
      data[11][i] = result.getDNumber();
      i++;
    }

    for (; i <= MAX_CANDIDATES; i++) {
      data[0][i] = "";
      data[1][i] = "";
      data[2][i] = "";
      data[3][i] = "";
      data[4][i] = "";
      data[5][i] = "";
      data[6][i] = "";
      data[7][i] = "";
      data[8][i] = "";
      data[9][i] = "";
      data[10][i] = "";
      data[11][i] = "";
    }

    return data;
  }

  private PdfPTable createSeparatorTable(float height) {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(WIDTH_PCTG);

    PdfPCell c = new PdfPCell();
    c.setBorder(Rectangle.NO_BORDER);
    c.setFixedHeight(height);
    table.addCell(c);

    return table;
  }

  private static PdfPCell createTextCell(Phrase phrase, int horizontalAlignment) {
    PdfPCell cell = new PdfPCell(phrase);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setHorizontalAlignment(horizontalAlignment);
    return cell;
  }

  private static PdfPCell createImageCell(InputStream is, float scale, int horizontalAlignment) throws DocumentException, IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    IOUtils.copy(is, outputStream);
    Image img = Image.getInstance(outputStream.toByteArray());
    IOUtils.closeQuietly(is);
    img.scalePercent(scale);

    PdfPCell cell = new PdfPCell(img, false);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setHorizontalAlignment(horizontalAlignment);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    cell.setPaddingRight(20f);
    return cell;
  }

  private static PdfPCell createTableTextCell(Phrase phrase, int horizontalAlignment) {
    PdfPCell cell = new PdfPCell(phrase);
    cell.setBorder(Rectangle.BOX);
    cell.setHorizontalAlignment(horizontalAlignment);
    cell.setPaddingLeft(5f);
    cell.setPaddingRight(5f);
    return cell;
  }

  private static PdfPCell createTableImageCell(byte[] bytes) throws DocumentException, IOException {
    Image img = Image.getInstance(bytes);
    if (LANDSCAPE) {
      img.scaleToFit(120f, 160f);
    } else {
      img.scaleToFit(90f, 120f);
    }

    PdfPCell cell = new PdfPCell(img, false);
    cell.setBorder(Rectangle.BOX);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPaddingTop(5f);
    cell.setPaddingBottom(5f);
    cell.setPaddingLeft(5f);
    cell.setPaddingRight(5f);
    return cell;    
  }

  private String translate(String lang, String key) {
    String val = key;
    String tempVal;
    if (Constants.LANG_EN.equalsIgnoreCase(lang)) {
      tempVal = EN_TRANSLATION.get(key);
    } else {
      tempVal = DE_TRANSLATION.get(key);
    }

    if (tempVal != null) {
      val = tempVal;
    }

    return val;
  }

}
