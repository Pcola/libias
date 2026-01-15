package sk.atos.fri.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.enums.SourceSystem;
import sk.atos.fri.dao.libias.service.StatisticService;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SiteStatistics;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static sk.atos.fri.rest.service.IncidentController.getSourceSystem;

/**
 *
 * @author Jaroslav Kollar
 */
@RestController
@RequestMapping(path = "/statistic")
public class StatisticController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private StatisticService statisticService;

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return all statistics about incidents in app
   */
  @RequestMapping(path = "/sitestatistics",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SiteStatistics> getSiteStatistics(HttpServletRequest httpServletRequest, @RequestParam(value = "system", required = false) String systemParam,
                                                @RequestHeader(value = "X-System", required = false) String xSystemHeader) {
    SourceSystem system = resolveSystem(systemParam, xSystemHeader);
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return system == null ? statisticService.getSiteStatistics() : statisticService.getSiteStatistics(system);
    } catch (Exception e) {
      LOG.error(username, sk.atos.fri.log.Error.GET_SITE_STATISTICS, e);      
      throw e;
    }
  }

  private SourceSystem resolveSystem(String param, String header) {
    return getSourceSystem(param, header, LOG);
  }
  }
