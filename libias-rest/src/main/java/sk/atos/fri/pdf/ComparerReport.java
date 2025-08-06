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
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sk.atos.fri.common.Constants;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SearchReportRequest;

@Component
public class ComparerReport {

    private static final Logger LOG = new Logger();

    private static final ObjectReader READER = new ObjectMapper().readerFor(Map.class);
    private static final Map<String, String> EN_TRANSLATION;
    private static final Map<String, String> DE_TRANSLATION;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    static {
        try {
            EN_TRANSLATION = READER.readValue(new ClassPathResource("i18n/en.json").getInputStream());
            DE_TRANSLATION = READER.readValue(new ClassPathResource("i18n/de.json").getInputStream());
        } catch (IOException e) {
            LOG.error(Error.READ_FILE_I18M, e);
            throw new RuntimeException(e);
        }
    }

    public byte[] createReport(SearchReportRequest request, String fullname, String lang) throws DocumentException, IOException {
        if (request.getMarisImageOptimized() == null || request.getExtImageOptimized() == null) {
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
        document.add(createSeparatorTableWithLine(36f));
        document.add(createSeparatorTable(12f));
        document.add(createNotes(request.getNote(), lang));
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

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{5, 5});

        table.addCell(createImageCell(Base64.getDecoder().decode(data.getMarisImageOptimized().getBytes("UTF-8")), false));
        table.addCell(createImageCell(Base64.getDecoder().decode(data.getExtImageOptimized().getBytes("UTF-8")), false));

        return table;
    }

    private PdfPTable createNotes(String notes, String lang) throws DocumentException {
        Font bold = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, GrayColor.GRAYBLACK);
        Font normal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, GrayColor.GRAYBLACK);

        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);

        table.addCell(createTextCell(new Phrase(translate(lang, "label.Comment") + ":", bold), Rectangle.NO_BORDER, Element.ALIGN_LEFT));

        PdfPCell notesCell = createTextCell(new Phrase(notes, normal), Rectangle.BOX, Element.ALIGN_LEFT);
        notesCell.setMinimumHeight(200f);
        notesCell.setVerticalAlignment(Element.ALIGN_TOP);
        table.addCell(notesCell);

        return table;
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
