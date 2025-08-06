package sk.atos.fri.rest.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.rest.model.LoginRequest;
import sk.atos.fri.security.hmac.HmacException;
import sk.atos.fri.security.service.AuthenticationService;

@RestController
public class AuthenticationController {

  @Autowired
  private AuthenticationService authenticationService;

  /**
   *
   * @param request - LoginRequest that contains credentials - username and password
   * @param response - expected server response
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return
   * @throws HmacException
   *
   * Endpoint used when user put credentials and hit login
   */
  @RequestMapping(path = "/login",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE,
                  consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public BamUser login(@RequestBody LoginRequest request, HttpServletResponse response, HttpServletRequest httpServletRequest) throws HmacException {
    return authenticationService.authenticate(request, response, httpServletRequest);
  }

  /**
   *
   * @param httpServletResponse - HttpServletRequest sent from client
   *
   * Endpoint used for logout user - based on username from httpServletResponse
   */
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public void logout(HttpServletResponse httpServletResponse) {
    authenticationService.logout(httpServletResponse);
  }

}
