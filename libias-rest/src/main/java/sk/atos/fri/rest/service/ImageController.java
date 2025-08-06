package sk.atos.fri.rest.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.model.Image;
import sk.atos.fri.dao.libias.service.ImageService;
import sk.atos.fri.log.Logger;

/**
 *
 * @author Jaroslav Kollar
 */
@RestController
@RequestMapping(path = "/image")
public class ImageController {

  @Autowired
  private Logger LOG; 

  @Autowired
  private ImageService imageService;

  /**
   *
   * @param oid - image ID
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return Image with corresponding ID
   */
  @RequestMapping(path = "/image/{oid}",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public Image getPersonImage(@PathVariable Long oid, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.debug(username, "Get image for oid: " + oid);
      return imageService.get(oid);
    } catch (Exception e) {      
      LOG.error(username, sk.atos.fri.log.Error.GET_IMAGE, e);
      throw e;
    }
  }  
}


