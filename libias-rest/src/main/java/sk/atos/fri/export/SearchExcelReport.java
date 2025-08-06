package sk.atos.fri.export;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.libias.model.Image;
import sk.atos.fri.dao.libias.service.ImageService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SearchBulkReportRequest;
import sk.atos.fri.ws.maris.model.PersonResponse;

import java.io.*;
import java.util.*;
import java.util.List;
import java.awt.*;

@Service
public class SearchExcelReport {

    private static final ObjectReader READER = new ObjectMapper().readerFor(Map.class);

    private static Map<String, String> EN_TRANSLATION;
    private static Map<String, String> DE_TRANSLATION;

    private static final Logger LOG2 = new Logger();

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

    @Autowired
    ImageService imageService;

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public byte[] createBulkReport(SearchBulkReportRequest request, List<PersonResponse> personList, String username, String fullname, String lang) {

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Suchergebnis");

        try {
            insertData(getData(request, personList, fullname));
        } catch (IOException e) {
            LOG.error(username, Error.EXPORT_CANNOT_CREATE_FORM, e);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            if (workbook != null) {
                workbook.write(byteArrayOutputStream);
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
                workbook.close();
            } catch (IOException e) {
            }
        }
    }

    private void insertImage(byte[] bytes, int rowCount, int columnCount) {
        int pictureIndex = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);

        ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
        anchor.setCol1(columnCount);
        anchor.setCol2(columnCount + 2);
        anchor.setRow1(rowCount);
        anchor.setRow2(rowCount + 1);

        Picture pict = sheet.createDrawingPatriarch().createPicture(anchor, pictureIndex);
        if (rowCount == 1 && columnCount > 1) {
            pict.resize(1, 0.5);
        } else {
            pict.resize(1);
        }
    }

    private List<Object[]> getData(SearchBulkReportRequest request, List<PersonResponse> personList, String fullname) throws IOException {
        InputStream is1 = getClass().getClassLoader().getResourceAsStream(Constants.BAMF_LOGO);
        byte[] logo1 = IOUtils.toByteArray(is1);
        IOUtils.closeQuietly(is1);

        InputStream is2 = getClass().getClassLoader().getResourceAsStream(Constants.LIBIAS_LOGO);
        byte[] logo2 = IOUtils.toByteArray(is2);
        IOUtils.closeQuietly(is2);

        List<Object[]> data = new ArrayList<Object[]>();
        Object[] row1 = {logo1, "", "", "", "", "", "", "", "", "", logo2};
        Object[] row2 = {"Externes Bild", "", "", "", "Erstellt von:", fullname, "", "Erstellt am:", new Date()};
        Object[] row3 = {Base64.getDecoder().decode(request.getExtImage().getBytes("UTF-8")), "", "", "", "Bemerkung:", request.getNote()};
        Object[] row4 = {"", ""};
        Object[] row5 = {"Bild", "", "GalleryID", "Rank", "Score",
        		         "AntragstellerOID", "PKZ", "Aktenzeichen", "AZR-Nummer", "D-Nummer", "E-Nummer", "Eurodac-Nr",
                         "Familienname", "Vorname", "Geburtsdatum", "Geburtsland", "Geburtsort", "Herkunftsland",
                         "Antragsdatum", "Antragstyp", "Außenstelle", "Geschlecht", "Staatsangehörigkeit", "Änderungsdatum"};
        data.add(row1);
        data.add(row2);
        data.add(row3);
        data.add(row4);
        data.add(row5);

        int i = 0;
        for (PersonResponse person : personList) {
            Image image = imageService.get(person.getImageOid());
            Object[] row = {image != null ? image.getImageData() : Constants.IMAGE_DELETED, "", person.getImageOid(), "" + (i+1), request.getScoreList().get(i),
                person.getApplicantOid(), person.getPkz(), person.getFileNumber(), person.getAzrNumber(), person.getdNumber(), person.geteNumber(), person.getEuroDacNumber(),
                person.getLastName(), person.getFirstName(), person.getBirthDate(), person.getBirthCountry(), person.getBirthPlace(), person.getOriginCountry(),
                person.getApplicantDate(), person.getApplicantType(), person.getWorkplace(), person.getGender(), person.getNationality(), person.getDateModified()};
            data.add(row);
            i++;
        }

        return data;
    }

    private void insertData(List<Object[]> data) {
        int rowCount = 0;
        for (Object[] aBook : data) {
            Row row = sheet.createRow(++rowCount);
            sheet.addMergedRegion(new CellRangeAddress(rowCount, rowCount, 1, 2));

            int columnCount = 0;
            for (Object field : aBook) {
                Cell cell = row.createCell(++columnCount);
                if (field instanceof String) {
                    cell.setCellValue((String) field);
                } else if (field instanceof Integer) {
                    cell.setCellValue((Integer) field);
                } else if (field instanceof Long) {
                    cell.setCellValue((Long) field);
                } else if (field instanceof Double) {
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Double) field);
                } else if (field instanceof Date) {
                    CellStyle cellStyle = workbook.createCellStyle();
                    cellStyle.setDataFormat(workbook.createDataFormat().getFormat("dd.mm.yyyy"));
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue((Date) field);
                } else if (field instanceof byte[]) {
                    if (rowCount == 1) {
                        row.setHeight((short) (310 * 4));
                    } else {
                        row.setHeight((short) (310 * 7));
                    }
                    insertImage((byte[]) field, rowCount, columnCount);
                }
                //if (columnCount > 2) {
                sheet.autoSizeColumn(columnCount);
                //}
            }
        }
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
