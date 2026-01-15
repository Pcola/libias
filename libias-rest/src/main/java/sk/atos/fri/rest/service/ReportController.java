package sk.atos.fri.rest.service;

import com.itextpdf.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.dao.libias.domain.Incident;
import sk.atos.fri.dao.libias.enums.SourceSystem;
import sk.atos.fri.dao.libias.model.Report;
import sk.atos.fri.dao.libias.service.IIncidentService;
import sk.atos.fri.dao.libias.service.ReportService;
import sk.atos.fri.dao.libias.service.UserService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.pdf.IncidentReport;

import static sk.atos.fri.rest.service.IncidentController.getSourceSystem;

@RestController
@RequestMapping(path = "/report")
public class ReportController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private HeaderParserService headerParserService;

  @Autowired
  private IIncidentService incidentService;

  @Autowired
  private UserService userService;

  @Autowired
  private IncidentReport incidentReport;

  @Autowired
  private ReportService reportService;

  /**
   *
   * @param caseId - Incident ID
   * @param request - - HttpServletRequest sent from client
   * @return byte array - report created for specific incident
   * @throws DocumentException
   * @throws IOException
   */
  @RequestMapping(path = "/create/{caseId}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] createReport(@PathVariable Long caseId, @RequestParam(value = "system", required = false) String systemParam,
                             @RequestHeader(value = "X-System", required = false) String xSystemHeader, HttpServletRequest request) throws DocumentException, IOException {
    String username = null;
    try {
      username = request.getUserPrincipal().getName();
      LOG.info(username, "Generate PDF report for caseId: " + caseId);

      SourceSystem system = resolveSystem(systemParam, xSystemHeader);

      Incident inc = (system == null)
              ? incidentService.findByCaseId(caseId)
              : incidentService.findByCaseId(caseId, system);

      if (inc == null) {
        throw new IllegalArgumentException("Incident not found for given caseId");
      }

      String fullname = headerParserService.getUserDepartment(request, true);
      byte[] reportBytes = incidentReport.createReport(inc, fullname, Constants.LANG_DE);

      // store report to DB
      Report r = new Report();
      r.setReport(reportBytes);
      r.setCreated(new Date());
      r.setUsername(username);
      r.setCaseId(inc.getCaseId());
      reportService.persist(r);

      LOG.info(username, "PDF report generated for caseId: " + caseId);
      return reportBytes;
    } catch (Exception e) {
      LOG.error(username, Error.CREATE_REPORT, e);
      throw e;
    }
  }

  /**
   *
   * @param caseId - Incident ID of requested report
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return report for specific incident ID
   * @throws Exception
   */
  @RequestMapping(path = "/reportId/{caseId}",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public String getReportId(@PathVariable Long caseId, HttpServletRequest httpServletRequest) throws Exception {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      Report report = reportService.findByCaseId(caseId);
      if (report == null) {
        throw new FileNotFoundException("Report not found for caseId: " + caseId);
      }

      return "{\"reportId\":\"" + report.getId() + "\"}";
    } catch (Exception e) {
      LOG.error(username, Error.GET_REPORT, e);
      throw e;
    }
  }

  private SourceSystem resolveSystem(String param, String header) {
    return getSourceSystem(param, header, LOG);
  }
}
