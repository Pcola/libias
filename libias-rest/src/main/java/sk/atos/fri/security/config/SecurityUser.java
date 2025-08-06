package sk.atos.fri.security.config;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import sk.atos.fri.dao.libias.model.UserRole;

/**
 * Security spring security user.
 */
public class SecurityUser extends User {

  private Long id;

  private Collection<UserRole> profiles;

  public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
  }

  public SecurityUser(Long id, String username, String password, Collection<UserRole> profiles, Collection<? extends GrantedAuthority> authorities) {
    super(username, password, authorities);
    this.id = id;
    this.profiles = profiles;
  }

  public Long getId() {
    return id;
  }

  public Collection<UserRole> getProfiles() {
    return profiles;
  }
}
