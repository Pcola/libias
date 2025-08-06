package sk.atos.fri.export;

import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import sk.atos.fri.common.Constants;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SearchReportRequest;
import sk.atos.fri.ws.maris.model.PersonResponse;

import java.io.*;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import static sk.atos.fri.common.Constants.*;

@Service
public class SearchWordReport {

    private static final String INSERT_IMAGE_1 = "INSERT_IMAGE_1";
    private static final String INSERT_IMAGE_2 = "INSERT_IMAGE_2";
    private static final String INSERT_FIRST_ROW_1 = "INSERT_FIRST_ROW_1";
    private static final String INSERT_FIRST_ROW_2 = "INSERT_FIRST_ROW_2";
    private static final String INSERT_SCORE = "INSERT_SCORE";
    private static final String INSERT_CREATED_BY_AT = "INSERT_CREATED_BY_AT";
    private static final String INSERT_REMARK = "INSERT_REMARK";

    private static final ObjectReader READER = new ObjectMapper().readerFor(Map.class);

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static Map<String, String> EN_TRANSLATION;
    private static Map<String, String> DE_TRANSLATION;

    private static final Logger LOG2 = new Logger();

    private static final int WIDTH = 180;
    private static final int HEIGHT = 230;

    static {
    	try {
                EN_TRANSLATION = READER.readValue(new ClassPathResource("i18n/en.json").getInputStream());
                DE_TRANSLATION = READER.readValue(new ClassPathResource("i18n/de.json").getInputStream());
        } catch (IOException e) {
                LOG2.error(Error.READ_FILE_I18M, e);
                throw new RuntimeException(e);
        }
    }	

    @Autowired
    private Logger LOG;

    private SearchReportRequest request;
    private String username;
    private String fullname;
    private PersonResponse person;

    public byte[] createReport(SearchReportRequest request, PersonResponse person, String username, String fullname, String lang) {
        this.request = request;
        this.username = username;
        this.fullname = fullname;
        this.person = person;

        File template = new File(getClass().getClassLoader().getResource(Constants.TEMPLATE_PATH_COMPARER).getFile());

        FileInputStream fInputStream;
        try {
            fInputStream = new FileInputStream(template);
        } catch (FileNotFoundException e) {
            LOG.error(username, Error.EXPORT_CANNOT_READ_TEMPLATE);
            return null;
        }

        XWPFDocument templDoc = null;
        try {
            templDoc = new XWPFDocument(fInputStream);
            updateDocument(templDoc, lang);
        } catch (IOException e) {
            LOG.error(username, Error.EXPORT_CANNOT_CREATE_FORM, e);
        } finally {
            try {
                fInputStream.close();
            } catch (IOException e) {
            }
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            if (templDoc != null) {
                templDoc.write(byteArrayOutputStream);
                return byteArrayOutputStream.toByteArray();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
            }
        }
    }

    private void updateDocument(XWPFDocument document, String lang) throws IOException{
        List<IBodyElement> documentElements = document.getBodyElements();
        for (IBodyElement elem : documentElements) {
            XWPFParagraph p;
            switch (elem.getElementType()) {
                case PARAGRAPH:
                    p = (XWPFParagraph) elem;
                    //LOG.debug(username, "Replacing paragraph: " + p.getText());
                    updateParagraph(p);
                    break;
                case TABLE:
                    XWPFTable table = (XWPFTable) elem;
                    boolean isFirstPersonInfoTable = false;
                    boolean isSecondPersonInfoTable = false;

                    for (XWPFTableRow row : table.getRows()) {
                        for (XWPFTableCell cell : row.getTableCells()) {
                            //LOG.debug(username, "Replacing cell: " + cell.getText());
                            for (XWPFParagraph tabPar : cell.getParagraphs()) {
                                if (tabPar.getText().contains(INSERT_IMAGE_1) || tabPar.getText().contains(INSERT_IMAGE_2)) {
                                    List<XWPFRun> runs = tabPar.getRuns();
                                    if (runs != null) {
                                        for (XWPFRun r : runs) {
                                            String txt = r.getText(0);

                                            if (txt != null && txt.contains(INSERT_IMAGE_1)) {
                                                txt = txt.replace(INSERT_IMAGE_1, "  ");
                                                r.setText(txt, 0);

                                                insertImage(tabPar, Base64.getDecoder().decode(request.getExtImageOptimized().getBytes("UTF-8")), true);
                                                break;
                                            }

                                            if (txt != null && txt.contains(INSERT_IMAGE_2)) {
                                                txt = txt.replace(INSERT_IMAGE_2, "  ");
                                                r.setText(txt, 0);

                                                insertImage(tabPar, Base64.getDecoder().decode(request.getMarisImageOptimized().getBytes("UTF-8")), true);
                                                break;
                                            }
                                        }
                                    }
                                } else if (tabPar.getText().contains(INSERT_FIRST_ROW_1)) {
                                    isFirstPersonInfoTable = true;
                                } else if (tabPar.getText().contains(INSERT_FIRST_ROW_2)) {
                                    isSecondPersonInfoTable = true;
                                }
                            }
                        }
                    }
                    if (isFirstPersonInfoTable) {
                        updateTable(table, null, lang);
                    }
                    if (isSecondPersonInfoTable) {
                        updateTable(table, person, lang);
                    }
                default:
                    break;
            }
        }
    }

    void insertImage(XWPFParagraph paragraph, byte[] image, boolean mainImage) {
        ByteArrayInputStream in = new ByteArrayInputStream(image);
        BufferedImage bImage = null;
        int width = WIDTH;
        int height = HEIGHT;

        try {
            bImage = ImageIO.read(in);
            width = bImage.getWidth();
            height = bImage.getHeight();
        } catch (IOException e) {
            LOG.error(username, Error.EXPORT_CANNOT_READ_IMAGE_SIZE);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
            }
        }

        //LOG.debug(username, "Original image width " + width + " and height " + height);
        boolean resize = false;
        if (width > WIDTH) {
            height = Math.round(height * WIDTH / width);
            width = WIDTH;
            resize = true;
        }
        if (height > HEIGHT) {
            width = Math.round(width * HEIGHT / height);
            height = HEIGHT;
            resize = true;
        }

        if (resize) {
            //LOG.debug(username, "Resizing image to target width " + width + " and height " + height);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                bImage = resize(bImage, width, height);
                ImageIO.write(bImage, "jpg", baos);
                image = baos.toByteArray();
            } catch (IOException e) {
            } finally {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }

        ByteArrayInputStream ins = new ByteArrayInputStream(image);

        try {
            String filename;
            double coefficient;
            if (mainImage) {
                filename = "Suchbild";
                coefficient = 0.75;
            } else {
                filename = "Kandidat";
                coefficient = 0.5;
            }
            paragraph.createRun().addPicture(ins, XWPFDocument.PICTURE_TYPE_JPEG, filename,
                    Units.toEMU(width * coefficient), Units.toEMU(height * coefficient));
        } catch (InvalidFormatException | IOException e) {
            LOG.error(username, Error.EXPORT_CANNOT_ADD_IMAGE);
        } finally {
            try {
                ins.close();
            } catch (IOException e) {
            }
        }
    }

    private BufferedImage resize(BufferedImage src, int targetWidth, int targetHeight) {
        BufferedImage bi = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(src, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return bi;
    }

    private void updateParagraph(XWPFParagraph p) {
        List<XWPFRun> runs = p.getRuns();
        if (runs != null) {
            for (XWPFRun r : runs) {
                String txt = r.getText(0);
                if (txt != null) {
                    //LOG.debug(username, "Replacing in template: " + txt);
                    switch (txt) {
                        case INSERT_SCORE:
                            NumberFormat format = NumberFormat.getPercentInstance(Locale.GERMAN);
                            format.setMinimumFractionDigits(2);
                            format.setMaximumFractionDigits(2);
                            txt = txt.replace(INSERT_SCORE, format.format(request.getScore()));
                            break;
                        case INSERT_CREATED_BY_AT:
                            String formattedDate = new SimpleDateFormat(STANDARD_DATE_FORMAT_COMMA).format(new Date());
                            String insertTxt = fullname + " am " + formattedDate + " Uhr";
                            txt = txt.replace(INSERT_CREATED_BY_AT, insertTxt);
                            break;
                        case INSERT_REMARK:
                            txt = txt.replace(INSERT_REMARK, request.getNote());
                            break;
                        default:
                            txt = null;
                            break;
                    }
                    if (txt != null) {
                        r.setText(txt, 0);
                        //LOG.debug(username, "Replaced text: " + txt);
                    }
                }
            }
        }
    }

    private void updateTable(XWPFTable table, PersonResponse personResponse, String lang) {
        // using the first existing row for formatting the new rows
        XWPFTableRow firstRow = table.getRow(0);
        int numberOfColumns = 2;

        getPersonalInfoMap(personResponse, lang).forEach((key, value) -> {
            XWPFTableRow tableRow = table.createRow();

            if (firstRow != null) {
                for (int col = 0; col < numberOfColumns; col++) {
                    int width = firstRow.getCell(col).getWidth();
                    tableRow.getCell(col).setWidth(Integer.toString(width));
                }
            }

            tableRow.getCell(0).setText(key);
            tableRow.getCell(1).setText(value);
        });

        //deleting the first empty row at the end
        table.removeRow(0);
    }

    @SuppressWarnings("serial")
    private Map<String, String> getPersonalInfoMap(PersonResponse person, String lang) {
        return person == null ? new LinkedHashMap<String, String>() {{
        	put("PKZ", null);
            put(translate(lang, "label.FileNumber"), null);
            put(translate(lang, "label.ApplicantType"), null);
            put(translate(lang, "label.LastName"), null);
            put(translate(lang, "label.FirstName"), null);
            put(translate(lang, "label.BirthDate"), null);
            put(translate(lang, "label.PlaceOfBirth"), null);
            put(translate(lang, "label.Nationality"), null);
            put(translate(lang, "label.Sex"), null);
            put(translate(lang, "label.AZRNumber"), null);
            put(translate(lang, "label.DNumber"), null);
            put(translate(lang, "label.DateOfRecording"), null);
        }} : new LinkedHashMap<String, String>() {{
            put("PKZ", person.getPkz().toString());
            put(translate(lang, "label.FileNumber"), person.getFileNumber());
            put(translate(lang, "label.ApplicantType"), person.getApplicantType());
            put(translate(lang, "label.LastName"), person.getApplicantType());
            put(translate(lang, "label.FirstName"), person.getFirstName());
            put(translate(lang, "label.BirthDate"), person.getBirthDate() != null ? DATE_FORMAT.format(person.getBirthDate()) : "");
            put(translate(lang, "label.PlaceOfBirth"), person.getBirthPlace());
            put(translate(lang, "label.Nationality"), person.getNationality());
            put(translate(lang, "label.Sex"), person.getGender() != null ? translate(lang, "label.Sex." + person.getGender()) : "");
            put(translate(lang, "label.AZRNumber"), person.getAzrNumber());
            put(translate(lang, "label.DNumber"), person.getdNumber());
            put(translate(lang, "label.DateOfRecording"), person.getDateModified()!= null ? DATE_FORMAT.format(person.getDateModified()) : "");
        }};
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
