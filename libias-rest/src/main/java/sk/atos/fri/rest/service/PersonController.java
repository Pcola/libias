package sk.atos.fri.rest.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.service.PersonService;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.PersonsRequest;
import sk.atos.fri.ws.maris.model.PersonResponse;

@RestController
@RequestMapping(path = "/person")
public class PersonController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private PersonService personService;

  /**
   *
   * @param request - including image IDs
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return person infos for each image
   */
  @RequestMapping(path = "/persons",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PersonResponse> getPersons(@RequestBody PersonsRequest request, HttpServletRequest httpServletRequest) {
    if (request == null || request.getOids().length == 0) {
      throw new IllegalArgumentException("No OIDs provided.");
    }    
    
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return personService.getPersons(request.getOids());
    } catch (Exception e) {
      LOG.error(username, sk.atos.fri.log.Error.GET_MARIS_BILD, e);
      throw e;
    }
  }  
}
