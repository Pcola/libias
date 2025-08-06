package sk.atos.fri.security.service;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.service.UserService;
import sk.atos.fri.security.hmac.HmacRequester;

@Service
public class DefaultHmacRequester implements HmacRequester {

  @Autowired
  private UserService userService;

  @Override
  public Boolean canVerify(HttpServletRequest request) {
    return !request.getRequestURI().contains("/login") && !request.getRequestURI().contains("/logout");
  }

  @Override
  public String getPublicSecret(Long id) {
    BamUser userDTO = userService.get(id);
    if (userDTO != null) {
      return userDTO.getPublicSecret();
    }
    return null;
  }

  @Override
  public Boolean isSecretInBase64() {
    return true;
  }
}
