package sk.atos.fri.pdf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.BaseColor;
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
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.libias.service.ImageService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SearchReportRequest;
import sk.atos.fri.ws.maris.model.PersonResponse;

@Component
public class SearchReport {
	
  private static final Logger LOG = new Logger();

  private static final ObjectReader READER = new ObjectMapper().readerFor(Map.class);
  private static Map<String, String> EN_TRANSLATION;
  private static Map<String, String> DE_TRANSLATION;

  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

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
  }

  public byte[] createReport(SearchReportRequest request, PersonResponse person, String fullname, String lang) throws DocumentException, IOException {
    if (person == null || request.getExtImageOriginal() == null) {
      return null;
    }
    
    Document document = new Document();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    PdfWriter.getInstance(document, outputStream);
    document.open();
    document.add(createLogosHeader());
    document.add(createReportMetadata(request, fullname, lang));
    document.add(createSeparatorTableWithLine(6f));
    document.add(createSeparatorTable(12f));
    document.add(createFacesTable(request, lang));
    document.add(createSeparatorTableWithLine(request.isFull() ? 16f : 36f));
    document.add(createSeparatorTable(12f));
    document.add(createComparisonDetails(request, person, lang));
    document.close();
    return outputStream.toByteArray();
  }

  private PdfPTable createLogosHeader() throws DocumentException, IOException {
    PdfPTable table = new PdfPTable(2);
    table.getDefaultCell().setBorder(0);
    table.setWidthPercentage(100);

    table.addCell(createImageCell(this.getClass().getClassLoader().getResourceAsStream(Constants.BAMF_LOGO), 12.5f, Element.ALIGN_LEFT));
    table.addCell(createImageCell(this.getClass().getClassLoader().getResourceAsStream(Constants.LIBIAS_LOGO), 80f, Element.ALIGN_RIGHT));

    return table;
  }

  private PdfPTable createReportMetadata(SearchReportRequest data, String fullname, String lang) throws DocumentException {
    Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);

    PdfPTable table = new PdfPTable(4);
    table.setWidthPercentage(100);
    table.setWidths(new int[]{7, 10, 10, 33});

    table.addCell(createTextCell(new Phrase(translate(lang, "label.Datum") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(DATE_FORMAT.format(new Date()), normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));

    table.addCell(createTextCell(new Phrase(translate(lang, "label.Editor") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    table.addCell(createTextCell(new Phrase(fullname, normal), Rectangle.NO_BORDER, Element.ALIGN_LEFT));

    return table;
  }

  private PdfPTable createFacesTable(SearchReportRequest data, String lang) throws DocumentException, IOException {
    Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);

    PdfPTable table;
    if (data.isFull()) {
      table = new PdfPTable(3);
      table.setWidthPercentage(100);
      table.setWidths(new int[]{5, 5, 5});
    } else {
      table = new PdfPTable(2);
      table.setWidthPercentage(100);
      table.setWidths(new int[]{5, 5});
    }

    table.addCell(createTextCell(new Phrase(translate(lang, "label.MarisImageOriginal") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_CENTER));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.ExternalImageOriginal") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_CENTER));
    if (data.isFull()) {
      table.addCell(createTextCell(new Phrase(translate(lang, "label.Similarity") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_CENTER));
    }

    sk.atos.fri.dao.libias.model.Image probeImage = imageService.get(data.getImageOid());

    PdfPCell marisOriginalCell;
    if (probeImage != null) {
      marisOriginalCell = createImageCell(probeImage.getImageData(), data.isFull());
    } else {
      marisOriginalCell = createTextCell(new Phrase(Constants.IMAGE_DELETED, normal), Rectangle.NO_BORDER, Element.ALIGN_CENTER);
      marisOriginalCell.setUseAscender(true);
      marisOriginalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
    }
    table.addCell(marisOriginalCell);

    PdfPCell extOriginalCell;
    extOriginalCell = createImageCell(Base64.getDecoder().decode(data.getExtImageOriginal().getBytes("UTF-8")), data.isFull());
    table.addCell(extOriginalCell);

    if (!data.isFull()) {
      return table;
    }

    DecimalFormat df = new DecimalFormat("0.##");
    df.setRoundingMode(RoundingMode.HALF_DOWN);
    String score = df.format(data.getScore()* 100.0) + "%";

    PdfPCell similarity;
    similarity = createTextCell(new Phrase(score,normal), Rectangle.NO_BORDER, Element.ALIGN_CENTER);
    similarity.setUseAscender(true);
    similarity.setVerticalAlignment(Element.ALIGN_MIDDLE);
    table.addCell(similarity);

    PdfPCell span = new PdfPCell();
    span.setColspan(3);
    span.setBorder(Rectangle.NO_BORDER);
    span.setFixedHeight(12f);
    table.addCell(span);

    table.addCell(createTextCell(new Phrase(translate(lang, "label.MarisImageOptimized") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_CENTER));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.ExternalImageOptimized") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_CENTER));
    table.addCell(createTextCell(new Phrase(translate(lang, "label.ComparisonImage") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_CENTER));

    PdfPCell marisOptimizedCell;
    marisOptimizedCell = createImageCell(Base64.getDecoder().decode(data.getMarisImageOptimized().getBytes("UTF-8")), data.isFull());
    table.addCell(marisOptimizedCell);

    PdfPCell extOptimizedCell;
    extOptimizedCell = createImageCell(Base64.getDecoder().decode(data.getExtImageOptimized().getBytes("UTF-8")), data.isFull());
    table.addCell(extOptimizedCell);

    PdfPCell comparisonCell;
    comparisonCell = createImageCell(Base64.getDecoder().decode(data.getCompImageOptimized().getBytes("UTF-8")), data.isFull());
    table.addCell(comparisonCell);

    return table;
  }

  private PdfPTable createComparisonDetails(SearchReportRequest data, PersonResponse person, String lang) throws DocumentException {
    Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);
    Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);

    PdfPTable comparisonDataTable = new PdfPTable(3);
    comparisonDataTable.setWidthPercentage(100);
    comparisonDataTable.setWidths(new int[]{10,1,10});

    comparisonDataTable.addCell(createTextCell(new Phrase(translate(lang, "label.PersonalDataMaris") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    comparisonDataTable.addCell(createTextCell(new Phrase("",normal),Rectangle.NO_BORDER, Element.ALIGN_LEFT));
    comparisonDataTable.addCell(createTextCell(new Phrase(translate(lang, "label.CommentExternalImage") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));

    PdfPTable personData = new PdfPTable(1);
    personData.getDefaultCell().setBorder(Rectangle.NO_BORDER);
    personData.addCell(createKeyValuePair("PKZ", normal, Element.ALIGN_RIGHT, person.getPkz() != null? person.getPkz().toString() : "", normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.FileNumber"), normal, Element.ALIGN_RIGHT, person.getFileNumber(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.ApplicantType"), normal, Element.ALIGN_RIGHT, person.getApplicantType(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.LastName"), normal, Element.ALIGN_RIGHT, person.getLastName(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.FirstName"), normal, Element.ALIGN_RIGHT, person.getFirstName(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.BirthDate"), normal, Element.ALIGN_RIGHT,
      person.getBirthDate() != null ? DATE_FORMAT.format(person.getBirthDate()) : "", normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.PlaceOfBirth"), normal, Element.ALIGN_RIGHT, person.getBirthPlace(), normal, Element.ALIGN_LEFT));
    
    personData.addCell(createKeyValuePair(translate(lang, "label.Nationality"), normal, Element.ALIGN_RIGHT, person.getNationality(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.Sex"), normal, Element.ALIGN_RIGHT,
      person.getGender() != null ? translate(lang, "label.Sex." + person.getGender()) : "", normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.AZRNumber"), normal, Element.ALIGN_RIGHT, person.getAzrNumber(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.DNumber"), normal, Element.ALIGN_RIGHT, person.getdNumber(), normal, Element.ALIGN_LEFT));
    personData.addCell(createKeyValuePair(translate(lang, "label.DateOfRecording"), normal, Element.ALIGN_RIGHT,
      person.getDateModified()!= null ? DATE_FORMAT.format(person.getDateModified()) : "", normal, Element.ALIGN_LEFT));    

    PdfPCell comparisonTableWrap = new PdfPCell(personData);
    comparisonTableWrap.setBorder(Rectangle.BOX);
    comparisonDataTable.addCell(comparisonTableWrap);

    PdfPCell emptyCell = new PdfPCell();
    emptyCell.setBorder(Rectangle.NO_BORDER);
    comparisonDataTable.addCell(emptyCell);

    PdfPCell commentary = createTextCell(new Phrase(data.getNote(),normal), Rectangle.BOX, Element.ALIGN_LEFT);
    commentary.setVerticalAlignment(Element.ALIGN_TOP);
    comparisonDataTable.addCell(commentary);

    return comparisonDataTable;
  }

  private PdfPTable createSeparatorTable(float height) {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(100);

    PdfPCell c = new PdfPCell();
    c.setBorder(Rectangle.NO_BORDER);
    c.setFixedHeight(height);
    table.addCell(c);

    return table;
  }

  private PdfPTable createSeparatorTableWithLine(float height) {
    PdfPTable table = new PdfPTable(1);
    table.setWidthPercentage(100);

    PdfPCell c = new PdfPCell();
    c.setBorder(Rectangle.BOTTOM);
    c.setFixedHeight(height);
    c.setBorderColor(BaseColor.BLACK);
    c.setBorderWidth(1f);
    table.addCell(c);

    return table;
  }

  private static PdfPTable createKeyValuePair(String key, Font keyFont, int keyHorizontalAlignment, String value, Font valueFont, int valueHorizontalAlignment) {
    PdfPTable t = new PdfPTable(2);
    t.getDefaultCell().setBorder(Rectangle.NO_BORDER);

    PdfPCell keyCell;
    keyCell = new PdfPCell(new Phrase(key + ":", keyFont));
    keyCell.setPaddingTop(2f);
    keyCell.setPaddingBottom(2f);
    keyCell.setBorder(Rectangle.NO_BORDER);
    keyCell.setHorizontalAlignment(keyHorizontalAlignment);
    t.addCell(keyCell);

    PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
    valueCell.setPaddingTop(2f);
    valueCell.setPaddingBottom(2f);
    valueCell.setBorder(Rectangle.NO_BORDER);
    valueCell.setHorizontalAlignment(valueHorizontalAlignment);
    t.addCell(valueCell);

    return t;
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

  private static PdfPCell createImageCell(byte[] bytes, boolean isFull) throws DocumentException, IOException {
    Image img = Image.getInstance(bytes);
    if (isFull) {
      img.scaleToFit(100f, 126f);
    } else {
      img.scaleToFit(150f, 189f);
    }

    PdfPCell cell = new PdfPCell(img);
    cell.setBorder(Rectangle.NO_BORDER);
    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
    cell.setPaddingTop(10f);
    cell.setPaddingLeft(10f);
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
