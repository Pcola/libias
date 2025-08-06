package sk.atos.fri.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
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
import java.util.Date;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.IncidentStatus;
import sk.atos.fri.dao.libias.model.Incident;
import sk.atos.fri.dao.libias.model.Status;
import sk.atos.fri.dao.libias.service.ImageService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;

@Component
public class IncidentReport {
  
  private static final Logger LOG = new Logger();

  private static final ObjectReader READER = new ObjectMapper().readerFor(Map.class);
  private static Map<String, String> EN_TRANSLATION;
  private static Map<String, String> DE_TRANSLATION;

  @Autowired
  private ImageService imageService;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

  /**
   * Translation
   */
  static {
    try {
      EN_TRANSLATION = READER.readValue(new ClassPathResource("i18n/en.json").getInputStream());
      DE_TRANSLATION = READER.readValue(new ClassPathResource("i18n/de.json").getInputStream());
    } catch (IOException e) {
      LOG.error(Error.READ_FILE_I18M, e);
      throw new RuntimeException(e);
    }
  }

  /**
   *
   * @param inc - Incident, from which si report created
   * @param fullname - logged user full name
   * @param lang - language used
   * @return byte array - report ready to send or persist
   * @throws DocumentException
   * @throws IOException
   */
  public byte[] createReport(Incident inc, String fullname, String lang) throws DocumentException, IOException {
    if (inc == null) {
      throw new IllegalArgumentException("Can't create report on empty incident");
    }
    
    Document document = new Document();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);
    document.open();

    PdfPTable table;
    PdfPCell cell;

    createLogosTable(document);

    // info
    table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new int[]{5, 5, 5, 5});
    createInfoTable(table, inc, fullname, lang);
    cell = new PdfPCell();
    cell.setColspan(4);
    cell.setPaddingBottom(20f);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setFixedHeight(20f);
    table.addCell(cell);
    document.add(table);

    // main table
    table = new PdfPTable(1);
    table.setWidthPercentage(80);
    table.setWidths(new int[]{12});
    cell = new PdfPCell();
    cell.addElement(createDetailTable(lang, inc));
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(cell);
    document.add(table);

    // footer
    table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new int[]{5, 5, 5, 5});
    createFooterTable(table, inc, lang);
    document.add(table);

    document.close();
    return outputStream.toByteArray();
  }

  /**
   *
   * @param table
   * @param inc
   * @param fullname
   * @param lang
   *
   * Creating info table and populate with data
   */
  private void createInfoTable(PdfPTable table, Incident inc, String fullname, String lang) {
    Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);

    Status s = inc.getStatus();
    Long statusId = s != null ? s.getStatusId() : IncidentStatus.Open.id;
    
    PdfPCell cell;

    table.addCell(createTextCell(new Phrase(translate(lang, "label.CaseId") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(String.valueOf(inc.getCaseId()), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.Datum") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(DATE_FORMAT.format(new Date()), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));

    table.addCell(createTextCell(new Phrase(translate(lang, "label.Status") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.Status." + statusId), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.Editor") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(fullname, normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));

    table.addCell(createTextCell(new Phrase(translate(lang, "label.DienstStelle") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(inc.getWorkplace() != null ? inc.getWorkplace().getWorkplace() : "", normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.Priority") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(inc.getPriority().getPriority(), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
  }

  private void createFooterTable(PdfPTable table, Incident inc, String lang) {
    Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);

    PdfPCell cell;

    table.addCell(createTextCell(new Phrase(translate(lang, "label.Comment") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    cell = createTextCell(new Phrase(inc.getNote(), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT);
    cell.setColspan(3);
    table.addCell(cell);

    table.addCell(createTextCell(new Phrase(translate(lang, "label.WorkplaceComment") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    cell = createTextCell(new Phrase(inc.getWorkplaceNote(), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT);
    cell.setColspan(3);
    table.addCell(cell);
  }

  private static PdfPCell createTextCell(Phrase phrase, int border, int horizontalAlignment) {
    PdfPCell cell = new PdfPCell(phrase);
    cell.setBorder(border);
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

  private static PdfPCell createImageCell(byte[] bytes) throws DocumentException, IOException {
    Image img = Image.getInstance(bytes);
    img.scaleToFit(100f, 125f);

    PdfPCell cell = new PdfPCell(img, false);
    return cell;
  }

  private PdfPTable createDetailTable(String lang, Incident inc) throws DocumentException, IOException {
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);
    sk.atos.fri.dao.libias.model.Image imageProbeId = imageService.get(inc.getProbeId());
    sk.atos.fri.dao.libias.model.Image imageGalleryId = imageService.get(inc.getGalleryId());

    PdfPTable table;
    PdfPCell cell;

    table = new PdfPTable(4);
    table.setWidthPercentage(100);

    table.addCell(createTextCell(new Phrase("", normal), Rectangle.BOX, Element.ALIGN_LEFT));

    if (imageProbeId != null) {
      cell = createImageCell(imageProbeId.getImageData());
      cell.setPaddingTop(10f);
      cell.setPaddingBottom(10f);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setBorder(Rectangle.BOX);
    } else {
      cell = createTextCell(new Phrase(Constants.IMAGE_DELETED, normal), Rectangle.BOX, Element.ALIGN_CENTER);
      cell.setUseAscender(true);
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }
    table.addCell(cell);

    if (imageGalleryId != null) {
      cell = createImageCell(imageGalleryId.getImageData());
      cell.setPaddingTop(10f);
      cell.setPaddingBottom(10f);
      cell.setHorizontalAlignment(Element.ALIGN_CENTER);
      cell.setBorder(Rectangle.BOX);
    } else {
      cell = createTextCell(new Phrase(Constants.IMAGE_DELETED, normal), Rectangle.BOX, Element.ALIGN_CENTER);
      cell.setUseAscender(true);
      cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }
    table.addCell(cell);

    DecimalFormat df = new DecimalFormat("0.##");
    df.setRoundingMode(RoundingMode.DOWN);
    String score = df.format(inc.getScore() * 100.0) + "%";
    table.addCell(createTextCell(new Phrase(score, normal), Rectangle.BOX, Element.ALIGN_CENTER));

    addPersonData(table, "PKZ", inc.getaPkz() != null ? inc.getaPkz().toString() : "", inc.getbPkz()!= null? inc.getbPkz().toString() : "");
    addPersonData(table, translate(lang, "label.FileNumber"), inc.getaFileNumber(), inc.getbFileNumber());
    addPersonData(table, translate(lang, "label.ApplicantType"), inc.getaApplicantType(), inc.getbApplicantType());
    addPersonData(table, translate(lang, "label.Office"), inc.getaWorkplace(), inc.getbWorkplace());
    addPersonData(table, translate(lang, "label.LastName"), inc.getaLastName(), inc.getbLastName());
    addPersonData(table, translate(lang, "label.FirstName"), inc.getaFirstName(), inc.getbFirstName());
    addPersonData(table, translate(lang, "label.BirthDate"), inc.getaBirthDate() != null ? DATE_FORMAT.format(inc.getaBirthDate()) : "", inc.getbBirthDate() != null ? DATE_FORMAT.format(inc.getbBirthDate()) : "");
    addPersonData(table, translate(lang, "label.RelevantNationality"), inc.getaNationality(), inc.getbNationality());
    addPersonData(table, translate(lang, "label.BirthPlace"), inc.getaBirthPlace(), inc.getbBirthPlace());
    addPersonData(table, translate(lang, "label.Sex"), translate(lang, "label.Sex." + inc.getaGender()), translate(lang, "label.Sex." + inc.getbGender()));
    addPersonData(table, translate(lang, "label.AZRNumber"), inc.getaAzrNumber(), inc.getbAzrNumber());
    addPersonData(table, translate(lang, "label.DNumber"), inc.getaDNumber(), inc.getbDNumber());
    addPersonData(table, translate(lang, "label.FileIdExists"), inc.getReferenceType() != null ? inc.getReferenceType() : "", inc.getReferenceType() != null ? inc.getReferenceType() : "");

    return table;
  }

  private void addPersonData(PdfPTable table, String key, String value1, String value2) {
    Font f = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, GrayColor.BLACK);

    PdfPCell cell;
    cell = new PdfPCell(new Phrase(key + ":", f));
    cell.setPaddingTop(2);
    cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
    cell.setBorder(Rectangle.BOX);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase(value1, f));
    cell.setPaddingTop(2);
    cell.setPaddingLeft(5);
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell.setBorder(Rectangle.BOX);
    table.addCell(cell);

    cell = new PdfPCell(new Phrase(value2, f));
    cell.setPaddingTop(2);
    cell.setPaddingLeft(5);
    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    cell.setBorder(Rectangle.BOX);
    table.addCell(cell);

    table.addCell(createTextCell(new Phrase("", f), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
  }

  private void createLogosTable(Document document) throws DocumentException, IOException {
    PdfPTable table = new PdfPTable(2);
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(100);

    table.addCell(createImageCell(this.getClass().getClassLoader().getResourceAsStream(Constants.BAMF_LOGO), 12.5f, Element.ALIGN_LEFT));
    table.addCell(createImageCell(this.getClass().getClassLoader().getResourceAsStream(Constants.LIBIAS_LOGO), 80f, Element.ALIGN_RIGHT));

    document.add(table);
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
