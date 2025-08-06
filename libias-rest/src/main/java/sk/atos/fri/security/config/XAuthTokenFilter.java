package sk.atos.fri.security.config;

import java.io.IOException;
import java.text.ParseException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.security.hmac.HmacException;
import sk.atos.fri.security.hmac.HmacSigner;
import sk.atos.fri.security.service.AuthenticationService;

/**
 * Auth token filter.
 */
public class XAuthTokenFilter extends GenericFilterBean {  
  private static final Logger LOG = new Logger();

  private final AuthenticationService authenticationService;

  public XAuthTokenFilter(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;
  }

  /**
   * Find a cookie which contain a JWT
   *
   * @param request current http request
   * @return Cookie found, null otherwise
   */
  private Cookie findJwtCookie(HttpServletRequest request) {
    if (request.getCookies() == null || request.getCookies().length == 0) {
      return null;
    }
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().contains(AuthenticationService.JWT_APP_COOKIE)) {
        return cookie;
      }
    }
    return null;
  }

  @Override
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) servletRequest;
    HttpServletResponse response = (HttpServletResponse) servletResponse;

    if (request.getRequestURI().contains("/login")) {
      filterChain.doFilter(request, response);
    } else if (isAllowedActuatorUrl(request.getRequestURI())) {
      filterChain.doFilter(request, response);
    } else {
      try {
        Cookie jwtCookie = findJwtCookie(request);
        Assert.notNull(jwtCookie, "No jwt cookie found");

        String jwt = jwtCookie.getValue();
        String login = HmacSigner.getJwtClaim(jwt, AuthenticationService.JWT_CLAIM_LOGIN);
        Assert.notNull(login, "No login found in JWT");

        //Get user from cache
        BamUser user = authenticationService.findUser(login);
        Assert.notNull(user, "No user found with login: " + login);

        Assert.isTrue(HmacSigner.verifyJWT(jwt, user.getPrivateSecret()), "The Json Web Token is invalid");
        Assert.isTrue(!HmacSigner.isJwtExpired(jwt), "The Json Web Token is expired");

        String csrfHeader = request.getHeader(AuthenticationService.CSRF_CLAIM_HEADER);
        Assert.notNull(csrfHeader, "No csrf header found");

        String jwtCsrf = HmacSigner.getJwtClaim(jwt, AuthenticationService.CSRF_CLAIM_HEADER);
        Assert.notNull(jwtCsrf, "No csrf claim found in jwt");

        //Check csrf token (prevent csrf attack)
        Assert.isTrue(jwtCsrf.equals(csrfHeader));

        this.authenticationService.tokenAuthentication(login, request);
        filterChain.doFilter(request, response);
      } catch (HmacException | ParseException e) {
        LOG.error(Error.VERIFY_JWT, e);
        response.setStatus(403);
      }
    }
  }

  private boolean isAllowedActuatorUrl(String url) {
    return url.contains("/actuator") || url.contains("/dataImport");
  }

}
