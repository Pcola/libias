package sk.atos.fri.dao.cognitec.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.cognitec.model.CognitecImages;

/**
 * Repository class to get Cognitec image from recordId sent as parameter
 */
@Repository
public class CognitecImagesService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;


  public CognitecImages get(String recordId) {
    try {
      Query query = entityManager.createQuery("select i from CognitecImages i where i.recordid = :recordid");
      query.setParameter("recordid", recordId);
      return (CognitecImages) query.getSingleResult();
    } catch (NoResultException e) {
    }
    return null;
  }

  public int countAll() {
    Query query = entityManager.createNamedQuery("CognitecImages.countAll");
    return ((Long) query.getSingleResult()).intValue();
  }
}
