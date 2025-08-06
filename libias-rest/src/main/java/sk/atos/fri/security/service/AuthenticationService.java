package sk.atos.fri.security.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import sk.atos.fri.common.Constants;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.service.UserRole2BamUserService;
import sk.atos.fri.dao.libias.service.UserRoleService;
import sk.atos.fri.dao.libias.service.UserService;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.LoginRequest;
import sk.atos.fri.security.config.SecurityUser;
import sk.atos.fri.security.hmac.HmacException;
import sk.atos.fri.security.hmac.HmacSecurityFilter;
import sk.atos.fri.security.hmac.HmacSigner;
import sk.atos.fri.security.hmac.HmacToken;
import sk.atos.fri.security.hmac.HmacUtils;

@Service
public class AuthenticationService {

  public static final String JWT_APP_COOKIE = "hmac-app-jwt";
  public static final String CSRF_CLAIM_HEADER = "X-HMAC-CSRF";
  public static final String JWT_CLAIM_LOGIN = "login";

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private UserService userService;

  @Autowired
  private HeaderParserService headerParserService;

  @Autowired
  private UserRoleService userRoleService;

  @Autowired
  private UserRole2BamUserService userRole2BamUserService;
  
  @Autowired
  private Logger LOG;

  @Autowired
  private ServerConfig serverConfig;

  /**
   * Authenticate a user in Spring Security The following headers are set in the response: - X-TokenAccess: JWT - X-Secret: Generated secret in base64 using SHA-256 algorithm - WWW-Authenticate: Used
   * algorithm to encode secret The authenticated user in set ine the Spring Security context The generated secret is stored in a static list for every user
   *
   * @see MockUsers
   * @param loginRequest credentials
   * @param response http response
   * @return UserDTO instance
   * @throws HmacException
   */
  public BamUser authenticate(LoginRequest loginRequest, HttpServletResponse response, HttpServletRequest request) throws HmacException {
    BamUser bamUser;

    if(!"".equals(loginRequest.getUsername()) && !"".equals(loginRequest.getPassword())) {
      UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
      Authentication authentication = authenticationManager.authenticate(authenticationToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      bamUser = userService.find(loginRequest.getUsername());
      bamUser.setUserRoleCollection(userRoleService.getAllUserRoles(bamUser.getUserId()));
    } else if(headerParserService.isHeaderValid(request)) {
      LOG.debug("HEADER NAMES = " + String.join(",", Collections.list(request.getHeaderNames())));
      LOG.debug("HEADER HTTP_CN = " + request.getHeader(Constants.HTTP_HEADER_USERNAME));
      LOG.debug("HEADER HTTP_DEPARTMENTNUMBER = " + request.getHeader(Constants.HTTP_HEADER_DEPARTMENT));
      LOG.debug("HEADER HTTP_TITLE = " + request.getHeader(Constants.HTTP_HEADER_TITLE));
      LOG.debug("HEADER HTTP_SN = " + request.getHeader(Constants.HTTP_HEADER_LAST_NAME));
      LOG.debug("HEADER HTTP_GIVENNAME = " + request.getHeader(Constants.HTTP_HEADER_FIRST_NAME));
      LOG.debug("HEADER HTTP_GRP = " + request.getHeader(Constants.HTTP_HEADER_IDM_ROLES));

      bamUser = headerParserService.getUser(request);
      bamUser.setPassword(bamUser.getUsername());

      if(userService.find(bamUser.getUsername()) != null) {
        BamUser existedUser = userService.find(bamUser.getUsername());
        userRole2BamUserService.removeUserRoleRecords(existedUser.getUserId());
        userService.delete(existedUser);
      }

      userService.persist(bamUser);
      userRole2BamUserService.saveRoles(bamUser);

      Authentication authentication = new UsernamePasswordAuthenticationToken(bamUser.getUsername(), null,
              headerParserService.getGrantedAuthority(bamUser));
      SecurityContextHolder.getContext().setAuthentication(authentication);

    } else {
      LOG.debug("Could not authenticate due to MISSING CREDENTIALS");
      return null;
    }

    //Get Hmac signed token
    String csrfId = UUID.randomUUID().toString();
    Map<String, String> customClaims = new HashMap<>();
    customClaims.put(HmacSigner.ENCODING_CLAIM_PROPERTY, HmacUtils.HMAC_SHA_256);
    customClaims.put(JWT_CLAIM_LOGIN, bamUser.getUsername());
    customClaims.put(CSRF_CLAIM_HEADER, csrfId);

    //Generate a random secret
    String privateSecret = HmacSigner.generateSecret();
    String publicSecret = HmacSigner.generateSecret();

    // Jwt is generated using the private key
    HmacToken hmacToken = HmacSigner.getSignedToken(privateSecret, String.valueOf(bamUser.getUserId()), HmacSecurityFilter.JWT_TTL, customClaims);

    BamUser user = userService.get(bamUser.getUserId());
    user.setPublicSecret(publicSecret);
    user.setPrivateSecret(privateSecret);
    userService.persist(user);

    // Add jwt cookie
    Cookie jwtCookie = new Cookie(JWT_APP_COOKIE, hmacToken.getJwt());
    jwtCookie.setPath("/");
    jwtCookie.setMaxAge(HmacSecurityFilter.JWT_TTL * 60); // 6 * 60 * 60 = Cookie live 24 hours
    //Cookie cannot be accessed via JavaScript
    jwtCookie.setHttpOnly(true);

    // Set public secret and encoding in headers
    response.setHeader(HmacUtils.X_SECRET, publicSecret);
    response.setHeader(HttpHeaders.WWW_AUTHENTICATE, HmacUtils.HMAC_SHA_256);
    response.setHeader(CSRF_CLAIM_HEADER, csrfId);

    //Set JWT as a cookie
    response.addCookie(jwtCookie);

    BamUser userDTO = new BamUser();
    userDTO.setUsername(bamUser.getUsername());
    userDTO.setUserRoleCollection(bamUser.getUserRoleCollection());
    userDTO.setActive(bamUser.getActive());
    
    LOG.info(userDTO.getUsername(), "User " + userDTO.getUsername() + " logged in");
    
    return userDTO;
  }

  /**
   * Logout a user - Clear the Spring Security context - Remove the stored UserDTO secret
   */
  public void logout(HttpServletResponse httpServletResponse) {
    if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
      Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      if (principal != null && principal instanceof SecurityUser) {
        SecurityUser securityUser = (SecurityUser) principal;

        BamUser userDTO = userService.get(securityUser.getId());
        if (userDTO != null) {
          if(userDTO.getActive() == 1) {
            userDTO.setPublicSecret(null);
            userDTO.setPrivateSecret(null);
            userService.persist(userDTO);
            LOG.info(userDTO.getUsername(), "User " + userDTO.getUsername() + " logged out, redirecting to " + serverConfig.getLogoutRedirectUrlIntern());
            httpServletResponse.setHeader(Constants.HEADER_LOGOUT_LINK, serverConfig.getLogoutRedirectUrlIntern());
          } else {
            removeUser(userDTO, httpServletResponse);
          }
        }
      } else {
        BamUser userDTO = userService.find(SecurityContextHolder.getContext().getAuthentication().getName());
        removeUser(userDTO, httpServletResponse);
      }
    }
  }

  private void removeUser(BamUser userDTO, HttpServletResponse httpServletResponse) {
    if(userDTO != null) {
      userRole2BamUserService.removeUserRoleRecords(userDTO.getUserId());
      userService.delete(userDTO);
      LOG.info(userDTO.getUsername(), "User " + userDTO.getUsername() + " logged out, redirecting to " + serverConfig.getLogoutRedirectUrl());
      httpServletResponse.setHeader(Constants.HEADER_LOGOUT_LINK, serverConfig.getLogoutRedirectUrl());
    }
  }

  /**
   * Authentication for every request - Triggered by every http request except the authentication
   *
   * @see fr.redfroggy.hmac.configuration.security.XAuthTokenFilter Set the authenticated user in the Spring Security context
   * @param username username
   */
  public void tokenAuthentication(String username, HttpServletRequest request) {
    if(headerParserService.isHeaderValid(request)) {
      BamUser bamUser = headerParserService.getUser(request);

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(bamUser.getUsername(), null, headerParserService.getGrantedAuthority(bamUser));
      SecurityContextHolder.getContext().setAuthentication(authToken);
    } else {
      UserDetails details = userDetailsService.loadUserByUsername(username);
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(details, details.getPassword(), details.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);
    }
  }

  public BamUser findUser(String username) {
    return userService.find(username);
  }

}
