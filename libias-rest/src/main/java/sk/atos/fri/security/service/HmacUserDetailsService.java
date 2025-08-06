package sk.atos.fri.security.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.model.UserRole;
import sk.atos.fri.dao.libias.service.UserRoleService;
import sk.atos.fri.dao.libias.service.UserService;
import sk.atos.fri.log.Logger;
import sk.atos.fri.security.config.SecurityUser;

/**
 * Hmac user details service.
 */
@Component
public class HmacUserDetailsService implements UserDetailsService {

  @Autowired
  private Logger LOG;

  @Autowired
  private UserService userService;

  @Autowired
  private UserRoleService userRoleService;

  @Autowired
  private HeaderParserService headerParserService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    BamUser user = userService.find(username);

    if (user == null) {
      throw new UsernameNotFoundException("User " + username + " not found");
    }

    user.setUserRoleCollection(userRoleService.getAllUserRoles(user.getUserId()));

    List<GrantedAuthority> authorities = new ArrayList<>();
    if (user.getUserRoleCollection() != null && !user.getUserRoleCollection().isEmpty()) {
      for (UserRole r : user.getUserRoleCollection()) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getRole()));
      }
    }

    return new SecurityUser(user.getUserId(), user.getUsername(), user.getPassword(), user.getUserRoleCollection(), authorities);
  }
}
