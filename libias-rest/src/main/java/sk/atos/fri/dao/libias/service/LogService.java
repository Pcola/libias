package sk.atos.fri.dao.libias.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.Log;

@Repository
public class LogService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  /**
   *
   * @param log - log to persist
   * @return peristed log
   *
   * Persist log to dB
   */
  @Transactional
  public Log persist(Log log) {
    entityManager.persist(log);
    entityManager.flush();
    return log;
  }

}
