package sk.atos.fri.rest.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.model.Workplace;
import sk.atos.fri.dao.libias.service.WorkplaceService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;

@RestController
@RequestMapping(path = "/workplace")
public class WorkplaceController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private WorkplaceService workplaceService;

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of all workplaces
   */
  @RequestMapping(path = "/all",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Workplace> getAllRecords(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return workplaceService.getAllRecords();
    } catch (Exception e) {
      LOG.error(username, Error.LIST_DIENSTELLE, e);
      throw e;
    }
  }

}
