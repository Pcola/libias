package sk.atos.fri.rest.model;

import java.io.Serializable;

/**
 *
 * @author kristian
 */
public class UserRequest implements Serializable {

  private String firstName;
  private String lastName;
  private String username;
  private String password;
  private Long[] userRoleIds;
  private String workplaceId;
  private boolean active;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Long[] getUserRoleIds() {
    return userRoleIds;
  }

  public void setUserRoleIds(Long[] userRoleIds) {
    this.userRoleIds = userRoleIds;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getWorkplaceId() {
    return workplaceId;
  }

  public void setWorkplaceId(String workplaceId) {
    this.workplaceId = workplaceId;
  }

  public boolean getActive() {
    return this.active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
