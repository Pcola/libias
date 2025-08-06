package sk.atos.fri.dao.libias.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import sk.atos.fri.dao.libias.model.Priority;

@Repository
public class PriorityService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  /**
   *
   * @return all Priority records in dB
   */
  public List<Priority> getAllRecords() {
    return entityManager.createQuery("select p from Priority p order by p.priorityId").getResultList();
  }

  /**
   *
   * @param id
   * @return specific Priority with id
   */
  public Priority get(Long id) {
    return entityManager.find(Priority.class, id);
  }

}
