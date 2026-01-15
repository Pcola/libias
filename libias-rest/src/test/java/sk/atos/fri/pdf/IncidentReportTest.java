package sk.atos.fri.pdf;

import com.itextpdf.text.DocumentException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.atos.fri.dao.libias.domain.Incident;
import sk.atos.fri.dao.libias.service.IIncidentService;
import sk.atos.fri.dao.service.AbstractDaoTest;

import java.io.File;
import java.io.IOException;

@SpringBootTest
public class IncidentReportTest extends AbstractDaoTest {

    public static final String FILE = "/tmp/report.pdf";

    @Autowired
    private IncidentReport incidentReport;

    @Autowired
    private IIncidentService incidentService;

    @Test
    public void createReport() throws DocumentException, IOException {
        Incident inc = incidentService.findByCaseId(1L);
        if (inc != null) {
            byte[] bytes = incidentReport.createReport(inc, "admin", "de");
            FileUtils.writeByteArrayToFile(new File(FILE), bytes);
        }
    }
}
