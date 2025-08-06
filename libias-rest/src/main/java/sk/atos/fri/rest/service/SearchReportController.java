package sk.atos.fri.rest.service;

import com.itextpdf.text.DocumentException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.export.SearchExcelReport;
import sk.atos.fri.export.SearchWordReport;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.pdf.SearchReport;
import sk.atos.fri.rest.model.SearchBulkReportRequest;
import sk.atos.fri.rest.model.SearchReportRequest;
import sk.atos.fri.ws.maris.model.PersonResponse;
import sk.atos.fri.ws.maris.service.MarisWSClient;

@RestController
@RequestMapping(path = "/report/search")
public class SearchReportController {	

  @Autowired
  private Logger LOG; 

  @Autowired
  private HeaderParserService headerParserService;

  @Autowired
  private SearchReport searchReport;

  @Autowired
  private SearchWordReport searchWordReport;

  @Autowired
  private SearchExcelReport searchExcelReport;

  @Autowired
  private MarisWSClient marisClient;

  /**
   *
   * @param searchReportRequest
   * @param request - HttpServletRequest sent from client
   * @return created report
   * @throws DocumentException
   * @throws IllegalArgumentException
   * @throws IOException
   */
  @RequestMapping(path = "/create",
          method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = MediaType.APPLICATION_PDF_VALUE)
  public byte[] createReport(@RequestBody SearchReportRequest searchReportRequest, HttpServletRequest request) throws DocumentException, IllegalArgumentException, IOException {
    String username = null;	    
    try {
      username = request.getUserPrincipal().getName();
      LOG.info(username, "Generate PDF search report for image ID: " + searchReportRequest.getImageOid());

      PersonResponse person = marisClient.getPerson(searchReportRequest.getImageOid());

      String fullname = headerParserService.getUserDepartment(request, searchReportRequest.isFullName());
      if (searchReportRequest.isWord()) {
        return searchWordReport.createReport(searchReportRequest, person, username, fullname, searchReportRequest.getLang());
      } else {
        return searchReport.createReport(searchReportRequest, person, fullname, searchReportRequest.getLang());
      }
    } catch (Exception e) {
      LOG.error(username, Error.CREATE_REPORT, e);
      throw e;
    }
  }

  @RequestMapping(path = "/createBulk",
          method = RequestMethod.POST,
          consumes = MediaType.APPLICATION_JSON_VALUE,
          produces = Constants.APPLICATION_XLSX)
  public byte[] createBulkReport(@RequestBody SearchBulkReportRequest searchBulkReportRequest, HttpServletRequest request) throws DocumentException, IllegalArgumentException, IOException {
    String username = null;	    
    try {
      username = request.getUserPrincipal().getName();
      LOG.info(username, "Generate search bulk report with " + searchBulkReportRequest.getImageOidList().size() + " candidates");

      List<PersonResponse> personList = new ArrayList<PersonResponse>(searchBulkReportRequest.getImageOidList().size());
      for (Long imageOid : searchBulkReportRequest.getImageOidList()) {
        personList.add(marisClient.getPerson(imageOid));
      }

      String fullname = headerParserService.getUserDepartment(request, searchBulkReportRequest.isFullName());
      return searchExcelReport.createBulkReport(searchBulkReportRequest, personList, username, fullname, searchBulkReportRequest.getLang());
    } catch (Exception e) {
      LOG.error(username, Error.CREATE_REPORT, e);
      throw e;
    }
  }

}
