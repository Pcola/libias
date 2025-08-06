package sk.atos.fri.rest.service;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.pdf.ComparerReport;
import sk.atos.fri.rest.model.SearchReportRequest;

@RestController
@RequestMapping(path = "/report/comparer")
public class ComparerReportController {

    @Autowired
    private Logger LOG;

    @Autowired
    private HeaderParserService headerParserService;

    @Autowired
    private ComparerReport comparerReport;

    @RequestMapping(path = "/create",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_PDF_VALUE)
    public byte[] createReport(@RequestBody SearchReportRequest comparerReportRequest, HttpServletRequest request) throws DocumentException, IllegalArgumentException, IOException {
        String username = null;
        try {
            username = request.getUserPrincipal().getName();
            LOG.info(username, "Generate PDF comparer report");

            String fullname = headerParserService.getUserDepartment(request, comparerReportRequest.isFullName());
            return comparerReport.createReport(comparerReportRequest, fullname, comparerReportRequest.getLang());
        } catch (Exception e) {
            LOG.error(username, Error.CREATE_REPORT, e);
            throw e;
        }
    }

}
