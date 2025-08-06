package sk.atos.fri.rest.service;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.dao.LogTypeStatus;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.service.UserRole2BamUserService;
import sk.atos.fri.dao.libias.service.UserRoleService;
import sk.atos.fri.dao.libias.service.UserService;
import sk.atos.fri.dao.libias.service.WorkplaceService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.ChangePasswordRequest;
import sk.atos.fri.rest.model.UserRequest;

@RestController
@RequestMapping(path = "/user")
public class UserController {

  @Autowired
  private Logger LOG;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRoleService userRoleService;

  @Autowired
  private UserRole2BamUserService userRole2BamUserService;

  @Autowired
  private HeaderParserService headerParserService;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private MessageSource messageSource;

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return valid BamUser based on username from httpServletRequest
   */
  @RequestMapping(path = "/loggedUserInfo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public BamUser getLoggedInUserInfo(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();

      return headerParserService.getUser(httpServletRequest);
    } catch (NoResultException e) {
      LOG.error(username, Error.GET_USER, e);
      return null;
    }
  }

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of all users in app
   */
  @RequestMapping(path = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  @Secured({Constants.ROLE_ADMIN})
  public List<BamUser> listUsers(HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      List<BamUser> users = userService.findAll();
      for (BamUser user : users) {
        user.setUserRoleCollection(userRoleService.getAllUserRoles(user.getUserId()));
      }
      return users;
    } catch (Exception e) {
      LOG.error(username, Error.LIST_USERS, e);
      throw e;
    }
  }

  /**
   *
   * @param request - including new user info
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return valid new BamUser
   * @throws NoSuchAlgorithmException
   */
  @RequestMapping(path = "/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Constants.ROLE_ADMIN})
  public BamUser createUser(@RequestBody UserRequest request, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Creating new user " + request.getUsername());
      BamUser newUser = new BamUser();
      newUser.setUsername(request.getUsername());
      newUser.setFirstName(request.getFirstName());
      newUser.setLastName(request.getLastName());
      newUser.setPassword(passwordEncoder.encode(request.getPassword()));
      newUser.setUserRoleCollection(new ArrayList<>());
      if (StringUtils.isNotBlank(request.getWorkplaceId())) {
        newUser.setWorkplaceId(request.getWorkplaceId());
      }
      if (!ArrayUtils.isEmpty(request.getUserRoleIds())) {
        for (Long roleId : request.getUserRoleIds()) {
          newUser.getUserRoleCollection().add(userRoleService.get(roleId));
        }
      }

      newUser.setActive((short) 1);
      BamUser createdUser = userService.persist(newUser);
      userRole2BamUserService.saveRoles(createdUser);

      String logMsg = messageSource.getMessage("log.user_create", new Object[]{createdUser.getUsername(), username, createdUser.toString()}, new Locale("en"));
      LOG.infoDB(username, logMsg, LogTypeStatus.CreateUser);
      LOG.info(username, logMsg);
      return createdUser;
    } catch (Exception e) {
      LOG.debug(username, "Cannot create new user: " + new ReflectionToStringBuilder(request, ToStringStyle.JSON_STYLE).setExcludeFieldNames("password").toString());
      LOG.error(username, Error.CREATE_NEW_USER, e);
      throw e;
    }
  }

  /**
   *
   * @param request - including info of user to update
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return updated BamUser
   * @throws NoSuchAlgorithmException
   */
  @RequestMapping(path = "/update", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Constants.ROLE_ADMIN})
  public BamUser updateUser(@RequestBody UserRequest request, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Updating user " + request.getUsername());
      BamUser u = userService.find(request.getUsername());
      if (u == null) {
        throw new IllegalArgumentException("User not found");
      }
      if (StringUtils.isNotBlank(request.getPassword())) {
        u.setPassword(passwordEncoder.encode(request.getPassword()));
      }
      if (StringUtils.isNotBlank(request.getWorkplaceId())) {
        u.setWorkplaceId(request.getWorkplaceId());
      }
      if (!ArrayUtils.isEmpty(request.getUserRoleIds())) {
        u.getUserRoleCollection().clear();
        for (Long roleId : request.getUserRoleIds()) {
          u.getUserRoleCollection().add(userRoleService.get(roleId));
        }
        userRole2BamUserService.removeUserRoleRecords(u.getUserId());
        userRole2BamUserService.saveRoles(u);
      }
      u.setActive(request.getActive());
      BamUser updatedUser = userService.persist(u);

      String logMsg = messageSource.getMessage("log.user_update", new Object[]{request.getUsername(), username, updatedUser.toString()}, new Locale("en"));
      LOG.infoDB(username, logMsg, LogTypeStatus.UpdateUser);
      LOG.info(username, logMsg);
      return updatedUser;
    } catch (Exception e) {
      LOG.debug(username, "Cannot update user: " + new ReflectionToStringBuilder(request, ToStringStyle.JSON_STYLE).setExcludeFieldNames("password").toString());
      LOG.error(username, Error.UPDATE_USER ,e);
      throw e;
    }
  }

  /**
   *
   * @param request - user info
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return BamUser, which is logged out from app - deleted private and public key from dB
   * @throws NoSuchAlgorithmException
   */
  @RequestMapping(path = "/disconnect", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  @Secured({Constants.ROLE_ADMIN})
  public BamUser disconnect(@RequestBody UserRequest request, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
    String username = httpServletRequest.getUserPrincipal().getName();
    LOG.info(username, "User " + username + " is disconnecting user " + request.getUsername());
    BamUser u = userService.find(request.getUsername());
    if (u == null) {
      throw new IllegalArgumentException("User not found");
    }
    u.setPrivateSecret(null);
    u.setPublicSecret(null);
    LOG.info(username, "User " + username + " disconnected user " + request.getUsername());
    return userService.persist(u);
  }

  /**
   *
   * @param request - ChangePasswordRequest - including old and new password
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return BamUser
   * @throws NoSuchAlgorithmException
   *
   * In dB is for BamUser changed old password to new password
   */
  @RequestMapping(path = "/changePassword", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @Transactional
  public BamUser changePassword(@RequestBody ChangePasswordRequest request, HttpServletRequest httpServletRequest) throws NoSuchAlgorithmException {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "User is changing his/her password");
      if (StringUtils.isBlank(username)) {
        throw new IllegalArgumentException("Unauthenticated. User not provided.");
      }

      BamUser u = headerParserService.getUser(httpServletRequest);
      if (u == null) {
        throw new IllegalArgumentException("User not found");
      }
      if (!passwordEncoder.matches(request.getOldPassword(), u.getPassword())) {
        throw new IllegalArgumentException("Old password do not match.");
      }
      if (StringUtils.isNotBlank(request.getPassword())) {
        u.setPassword(passwordEncoder.encode(request.getPassword()));
      }
      LOG.info(username, "User changed his/her password");
      return userService.persist(u);
    } catch (Exception e) {
      LOG.error(username, Error.CHANGE_PASSWORD, e);
      throw e;
    }
  }

}
