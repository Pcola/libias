package sk.atos.fri.dao.libias.service;

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sk.atos.fri.dao.libias.model.AkteLocked;

@Repository
public class AkteLockedService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  public AkteLocked get(String fileNumber) {
    return entityManager.find(AkteLocked.class, fileNumber);
  }

  public List<AkteLocked> findAll() {
    return entityManager.createNamedQuery("AkteLocked.findAll").getResultList();
  }

  @Transactional
  public AkteLocked persist(String fileNumber, Date dateModified) {
    AkteLocked akteLocked = new AkteLocked();
    akteLocked.setFileNumber(fileNumber);
    akteLocked.setDateModified(dateModified);

    entityManager.persist(akteLocked);
    entityManager.flush();
    return akteLocked;
  }

  @Transactional
  public void delete(String fileNumber) {
    AkteLocked akteLocked = get(fileNumber);
    if (akteLocked == null) {
      return;
    }

    entityManager.remove(akteLocked);
    entityManager.flush();
  }

}
