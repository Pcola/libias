package sk.atos.fri.security.hmac;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.Charsets;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.security.config.WrappedRequest;
import sk.atos.fri.security.service.AuthenticationService;

/**
 * Hmac verification filter.
 */
public class HmacSecurityFilter extends GenericFilterBean {  
  private final static Logger LOG = new Logger();

  public static final Integer JWT_TTL = 60 * 6; // 24 hours live token

  private final HmacRequester hmacRequester;

  public HmacSecurityFilter(HmacRequester hmacRequester) {
    this.hmacRequester = hmacRequester;
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
     
    WrappedRequest wrappedRequest = new WrappedRequest(request);
    
    try {
      Assert.notNull(hmacRequester, "hmacRequester must not be null");

      if (!hmacRequester.canVerify(request)) {
        filterChain.doFilter(wrappedRequest, response);
      } else if (isAllowedActuatorUrl(request.getRequestURI())) {
        filterChain.doFilter(wrappedRequest, response);
      } else {
        //Get Authentication header
        Cookie jwtCookie = findJwtCookie(request);
        if (jwtCookie == null) {
          throw new HmacException("No jwt cookie found");
        }

        String jwt = jwtCookie.getValue();
        if (jwt == null || jwt.isEmpty()) {
          throw new HmacException("The JWT is missing from the '" + HmacUtils.AUTHENTICATION + "' header");
        }

        String digestClient = request.getHeader(HmacUtils.X_DIGEST);

        if (digestClient == null || digestClient.isEmpty()) {
          throw new HmacException("The digest is missing from the '" + HmacUtils.X_DIGEST + "' header");
        }

        //Get X-Once header
        String xOnceHeader = request.getHeader(HmacUtils.X_ONCE);

        if (xOnceHeader == null || xOnceHeader.isEmpty()) {
          throw new HmacException("The date is missing from the '" + HmacUtils.X_ONCE + "' header");
        }

        String url = request.getRequestURL().toString().replaceFirst("^(http[s]?://)", "");
        int firstSlash = url.indexOf("/");
        if (firstSlash > 0) {
          url = url.substring(firstSlash);
        }
        if (request.getQueryString() != null) {
          url += "?" + URLDecoder.decode(request.getQueryString(), Charsets.UTF_8.displayName());
        }
        LOG.debug("HMAC url digest: " + url);

        String encoding = HmacSigner.getJwtClaim(jwt, HmacSigner.ENCODING_CLAIM_PROPERTY);
        String iss = HmacSigner.getJwtIss(jwt);

        //Get public secret key
        String secret = hmacRequester.getPublicSecret(Long.parseLong(iss));
        Assert.notNull(secret, "Secret key cannot be null");

        String message;
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod()) || "PATCH".equals(request.getMethod())) {
          message = request.getMethod().concat(wrappedRequest.getBody()).concat(url).concat(xOnceHeader);
        } else {
          message = request.getMethod().concat(url).concat(xOnceHeader);
        }

        //Digest are calculated using a public shared secret        
        String digestServer = HmacSigner.encodeMac(secret, message, encoding);
        /*
        LOG.debug("HMAC JWT: " + jwt);
        LOG.debug("HMAC url digest: " + url);
        LOG.debug("HMAC Message server: " + message);
        LOG.debug("HMAC Secret server: " + secret);
        LOG.debug("HMAC Digest server: " + digestServer);
        LOG.debug("HMAC Digest client: " + digestClient);
        */

        if (digestClient.equals(digestServer)) {
          //LOG.debug("Request is valid, digest are matching");

          Map<String, String> customClaims = new HashMap<>();
          customClaims.put(HmacSigner.ENCODING_CLAIM_PROPERTY, HmacUtils.HMAC_SHA_256);
          HmacToken hmacToken = HmacSigner.getSignedToken(secret, String.valueOf(iss), JWT_TTL, customClaims);
          response.setHeader(HmacUtils.X_TOKEN_ACCESS, hmacToken.getJwt());

          filterChain.doFilter(wrappedRequest, response);
        } else {
          //LOG.debug("Server message: " + message);
          LOG.error(Error.DIGEST_NOT_MATCHING);
          throw new HmacException("Digest are not matching! Client: " + digestClient + " / Server: " + digestServer);
        }
      }

    } catch (Exception e) {
      if ((e instanceof HmacException) || (e instanceof IllegalArgumentException) || (e.getCause() != null && (e.getCause() instanceof HmacException))) {
        response.setStatus(403);
      } else {
        response.setStatus(500);        
      }
      LOG.error(Error.CHECKING_HMAC, e);
      response.getWriter().write(e.getMessage());
    }
  }

  private boolean isAllowedActuatorUrl(String url) {
    return url.contains("/actuator") || url.contains("/dataImport");
  }

}
