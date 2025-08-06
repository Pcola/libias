package sk.atos.fri.dao.libias.service;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.libias.model.Image;

@Repository
public class ImageService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  /**
   *
   * @param probeId - probeId is oid of Image from db table BILD
   * @return Image from dB table BILD
   */
  public Image get(Long probeId) {
    try {
      Query query = entityManager.createQuery("select i from Image i where i.oid = :oid" +
          " and i.imageData is not null and i.dateDeleted is null");
      query.setParameter("oid", probeId);
      return (Image) query.getSingleResult();
    } catch (NoResultException e) {
    }
    return null;
  }
}
