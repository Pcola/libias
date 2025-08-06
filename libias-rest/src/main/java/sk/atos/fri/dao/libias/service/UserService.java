package sk.atos.fri.dao.libias.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.BamUser;

@Repository
@Transactional
public class UserService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  /**
   *
   * @return all BamUsers in dB
   */
  public List<BamUser> findAll() {
    Query query = entityManager.createNamedQuery("BamUser.findAll");    
    return query.getResultList();
  }

  /**
   *
   * @param userId
   * @return specific BamUser with user ID
   */
  public BamUser get(Long userId) {
    return entityManager.find(BamUser.class, userId);
  }

  /**
   *
   * @param username
   * @return
   *
   * Find BamUser based on username
   */
  public BamUser find(String username) {
    Query createQuery = entityManager.createQuery("select u from " + BamUser.class.getName() + " u where u.username = :username");
    createQuery.setParameter("username", username.trim());
    try {
      return (BamUser) createQuery.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public void delete(BamUser bamUser) {
    entityManager.remove(entityManager.merge(bamUser));
    entityManager.flush();
  }

  /**
   *
   * @param user
   * @return
   *
   * Using for persist new and updated BamUser
   */
  public BamUser persist(BamUser user) {
    if (user.getUserId() == null) {
      entityManager.persist(user);
    } else {
      entityManager.merge(user);
    }
    entityManager.flush();
    return user;
  }

}
