package sk.atos.fri.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.service.StatisticService;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SiteStatistics;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
  public List<SiteStatistics> getSiteStatistics(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return statisticService.getSiteStatistics();
    } catch (Exception e) {
      LOG.error(username, sk.atos.fri.log.Error.GET_SITE_STATISTICS, e);      
      throw e;
    }
  }
  }
