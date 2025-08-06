package sk.atos.fri.rest.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.LogTypeStatus;
import sk.atos.fri.dao.libias.model.Incident;
import sk.atos.fri.dao.libias.service.IncidentService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.FinishCaseRequest;
import sk.atos.fri.rest.model.IncidentCountResponse;
import sk.atos.fri.rest.model.IncidentResponse;
import sk.atos.fri.rest.model.IncidentSearchRequest;
import sk.atos.fri.rest.model.IncidentUpdateRequest;
import sk.atos.fri.rest.model.RelatedCase;

@RestController
@RequestMapping(path = "/incident")
public class IncidentController {

  @Autowired
  private Logger LOG;

  @Autowired
  private IncidentService incidentService;

  @Autowired
  private MessageSource messageSource;

  @RequestMapping(path = "/count",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public long getCountAll() {
    return incidentService.countAll();
  }

  /**
   *
   * @param request - IncidentSearchRequest - contains filter attributes
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return IncidentResponse - list of corresponding incidents and count
   */
  @RequestMapping(path = "/search",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public IncidentResponse getIncidents(@RequestBody IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
    String username = null;
    try {	
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Reading cases: " + ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));
      long anfang = (new Date()).getTime();
      List<Incident> result = incidentService.searchIncident(request.getShowDoubleEvents(), request, httpServletRequest);
      Long resultCount = incidentService.incidentsCount(request.getShowDoubleEvents(), request, httpServletRequest);
      long ende = (new Date()).getTime();
      LOG.debug(username, "Result: " + (result != null ? result.size() : "null") + "/" + resultCount + " in " + (ende - anfang) + " ms");
      return new IncidentResponse(result, resultCount);
    } catch (Exception e) {
      LOG.error(username, Error.LIST_INCIDENTS, e);
      throw e;
    }
  }

  /**
   *
   * @param request
   * @param httpServletRequest
   * @return only count of incidents as Long
   */
  @RequestMapping(path = "/search/count",
                  method = RequestMethod.POST,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public Long getSearchedIncidentsCount(@RequestBody IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();      
      LOG.debug(username, "Reading count of cases: " + ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));
      long anfang = (new Date()).getTime();
      Long result = incidentService.incidentsCount(request.getShowDoubleEvents(), request, httpServletRequest);
      long ende = (new Date()).getTime();
      LOG.debug(username, "Result: " + result + " in " + (ende - anfang) + " ms");
      return result;
    } catch (Exception e) {
      LOG.error(username, Error.LIST_INCIDENTS, e);
      throw e;
    }
  }

  /**
   *
   * @param caseId - Incident ID
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return Incident with caseId
   */
  @RequestMapping(path = "/{caseId}",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public Incident getIncident(@PathVariable Long caseId, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Reading case with caseId " + caseId);
      Incident incident = incidentService.findByCaseId(caseId);
      if (incident.getIncidentHistory().size() != 0) {
        LOG.debug(username, "Case " + caseId + " FOUND HISTORY RECORDS: " + incident.getIncidentHistory().size());
      } else {
        LOG.debug(username, "Case " + caseId + " NO HISTORY RECORD");
      }
      return incident;
    } catch (Exception e) {
      LOG.error(username, Error.GET_INCIDENT, e);
      throw e;
    }
  }

  /**
   *
   * @param request - IncidentUpdateRequest - holding all update infos of specific Incident
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return void
   */
  @RequestMapping(path = "/update",
                  method = RequestMethod.PUT,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public ResponseEntity<Void> updateCase(@RequestBody IncidentUpdateRequest request, HttpServletRequest httpServletRequest) {
    if (request == null) {
      throw new IllegalArgumentException("Request body was not send");
    }

    String username = null;

    int result = 0;
    Long prevState = null;

    Incident inc = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Updating case: " + ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));
      inc = incidentService.findByCaseId(request.getCaseId());
      prevState = inc.getStatus().getStatusId();
      result = incidentService.updateCase(inc, request, httpServletRequest);
    } catch (Exception e) {
      LOG.error(username, Error.UPDATE_CASE, e);
      if (e.getCause() instanceof IllegalStateException) {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      } else {
        throw e;
      }
    }

    if (result > 0 && inc != null && prevState != null && !Objects.equals(prevState, request.getStatusId())) {
      try {
        String logMsg = messageSource.getMessage("log.update_status", new Object[]{request.getCaseId().toString(), request.getStatusId(), username}, new Locale("en"));
        LOG.infoDB(username, logMsg, LogTypeStatus.CaseChangeStatus);
        LOG.info(username, logMsg);
      } catch (Exception e) {
        LOG.error(username, Error.WRITE_DB, e);
        throw e;
      }
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of all file reference of incidents in dB
   */
  @RequestMapping(path = "/referencetype/all",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getAllFileReference(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return incidentService.findAllReferenceType();
    } catch (Exception e) {
      LOG.error(username, Error.LIST_REFERENCE_NUMBER, e);
      throw e;
    }
  }

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of all nationalities of incidents
   */
  @RequestMapping(path = "/nationality/all",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getNationalities(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return incidentService.findAllNationalities();
    } catch (Exception e) {
      LOG.error(username, Error.LIST_NATIONALITIES, e);
      throw e;
    }
  }

  /**
   *
   * @param filter - from user input
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return IncidentResponse - incidents + count
   */
  @RequestMapping(path = "/aussenstellercases",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  //@Secured({Constants.ROLE_AUSSENSTELLEUSER})
  public IncidentResponse getAussenstelerCases(@RequestBody IncidentSearchRequest filter, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Reading workplace cases: " + ToStringBuilder.reflectionToString(filter, ToStringStyle.JSON_STYLE));
      long anfang = (new Date()).getTime();
      List<Incident> result = incidentService.findAussenstellerCases(filter, username);
      Long resultCount = incidentService.aussenstellerCasesCount(filter, username);
      long ende = (new Date()).getTime();
      LOG.debug(username, "Result: " + (result != null ? result.size() : "null") + "/" + resultCount + " in " + (ende - anfang) + " ms");
      return new IncidentResponse(result, resultCount);
    } catch (Exception e) {
      LOG.error(username, Error.GET_AUSSENSTELLER, e);
      throw e;
    }
  }

  /**
   *
   * @param filter - from user input
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return count for Aussensteller cases
   */
  @RequestMapping(path = "/aussenstellercases/count",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  //@Secured({Constants.ROLE_AUSSENSTELLEUSER})
  public Long getAussenstelerCasesCount(@RequestBody IncidentSearchRequest filter, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Reading count of workplace cases: " + ToStringBuilder.reflectionToString(filter, ToStringStyle.JSON_STYLE));
      long anfang = (new Date()).getTime();
      Long result = incidentService.aussenstellerCasesCount(filter, username);
      long ende = (new Date()).getTime();
      LOG.debug(username, "Result: " + result + " in " + (ende - anfang) + " ms");
      return result;
    } catch (Exception e) {
      LOG.error(username, Error.LIST_INCIDENTS, e);
      throw e;
    }
  }

  /**
   *
   * @param request - including case ID + workplace note
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return void
   *
   * Using for finish case
   */
  @RequestMapping(path = "/finish",
                  method = RequestMethod.PUT,
                  consumes = MediaType.APPLICATION_JSON_VALUE,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  //@Secured({Constants.ROLE_AUSSENSTELLEUSER})
  public ResponseEntity<Void> finishCase(@RequestBody FinishCaseRequest request, HttpServletRequest httpServletRequest) {
    if (request == null) {
      throw new IllegalArgumentException("Request body was not send");
    }
    String username = null;

    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Finishing case: " + ToStringBuilder.reflectionToString(request, ToStringStyle.JSON_STYLE));
      incidentService.finishCase(request, httpServletRequest);
      LOG.info(username, "Case with caseId " + request.getCaseId() + " finished");
    } catch (Exception e) {
      LOG.error(username, Error.FINISH_CASE, e);
      throw e;
    }

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of incidents also with counts for each status
   */
  @RequestMapping(path = "/incidentstypecount",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<IncidentCountResponse> getIncidentCounts(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return incidentService.countCasesByStatus();
    } catch (Exception e) {
      LOG.error(username, Error.GET_STATISTICS, e);
      throw e;
    }
  }

  /**
   *
   * @param caseId - Incident id
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of related cases - belong to Incident
   */
  @RequestMapping(path = "/relatedcases/{caseId}",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RelatedCase> getRelatedCases(@PathVariable Long caseId, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Reading related cases for caseId " + caseId);
      return incidentService.getRelatedCases(caseId);
    } catch (Exception e) {
      LOG.error(username, Error.GET_SITE_RELATED_CASES, e);
      throw e;
    }
  }

  /**
   *
   * @param caseId - Incident ID
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of related cases for caseId
   */
  @RequestMapping(path = "/siterelatedcases/{caseId}",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RelatedCase> getSiteRelatedCases(@PathVariable Long caseId, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Reading site related cases for caseId " + caseId);
      return incidentService.getSiteRelatedCases(caseId, httpServletRequest.getUserPrincipal().getName());
    } catch (Exception e) {
      LOG.error(username, Error.GET_SITE_RELATED_CASES, e);
      throw e;
    }
  }

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of all nationalities for searcher
   */
  @RequestMapping(path = "/nationality/searcher",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getNationalitiesSearcher(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return incidentService.getNationalitiesSearcher();
    } catch (Exception e) {
      LOG.error(username, Error.LIST_NATIONALITIES, e);
      throw e;
    }
  }

}
