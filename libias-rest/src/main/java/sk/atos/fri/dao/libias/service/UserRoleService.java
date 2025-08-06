package sk.atos.fri.dao.libias.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.common.Constants;
import sk.atos.fri.dao.libias.model.UserRole;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
@Transactional
public class UserRoleService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  public List<UserRole> getAllRecords() {
    return entityManager.createQuery("select r from UserRole r order by r.roleId ASC").getResultList();
  }

  /**
   *
   * @param roleId
   * @return full user role for specific ID
   */
  public UserRole get(Long roleId) {
    return entityManager.find(UserRole.class, roleId);
  }

  /**
   *
   * @param userId
   * @return all user roles for specific user ID
   */
  public Collection<UserRole> getAllUserRoles(Long userId) {
    Query query = entityManager.createQuery("select r from UserRole2BamUser ur join UserRole r on ur.pk.roleId = r.roleId where ur.pk.userId = :userId");
    query.setParameter("userId", userId);
    return query.getResultList();
  }

  /**
   *
   * @param role - name of Role
   * @return found role by name
   */
  private UserRole getRoleByIdmName(String role) {
    Query query = entityManager.createQuery("select r from UserRole r where lower(r.idm_role) = lower(:role)");
    query.setParameter("role", role);

    List resultList = query.getResultList();

    if(resultList.size() > 0) {
      return (UserRole) resultList.get(0);
    } else {
      return null;
    }
  }

  /**
   *
   * @param roles array of roles
   * @param setAussenstellerRole flag, if set also Aussensteller role
   * @return full role object collection after persisting them
   *
   * Used when parsing roles from header
   */
  public Collection<UserRole> getRoles(String[] roles, boolean setAussenstellerRole) {
    Collection<UserRole> userRoles = new ArrayList<>();

    for(String string : roles) {
      UserRole role = getRoleByIdmName(string);

      if(role != null) {
        userRoles.add(role);
      }
    }

    if(setAussenstellerRole) {
      UserRole role = getRoleByIdmName(Constants.IDM_ROLE_AUSSENSTELLENUTZER);

      if(role != null) {
        userRoles.add(role);
      }
    }

    return userRoles;
  }
}
