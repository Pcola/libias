package sk.atos.fri.rest.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import sk.atos.fri.dao.libias.model.Priority;
import sk.atos.fri.dao.libias.service.PriorityService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;

@RestController
@RequestMapping(path = "/priority")
public class PriorityController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private PriorityService priorityService;

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of all priorities
   */
  @RequestMapping(path = "/all",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Priority> getAllRecords(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return priorityService.getAllRecords();
    } catch (Exception e) {
      LOG.error(username, Error.LIST_PRIORITIES, e);
      throw e;
    }
  }

}
