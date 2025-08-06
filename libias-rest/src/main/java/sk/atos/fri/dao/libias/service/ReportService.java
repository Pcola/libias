package sk.atos.fri.dao.libias.service;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.Report;

@Repository
@Transactional
public class ReportService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  /**
   *
   * @param report report to persist
   *
   * If report already exists, it will only merge
   */
  @Transactional
  public void persist(Report report) {
    if (report.getId() == null) {
      entityManager.persist(report);
    } else {
      entityManager.merge(report);
    }
    entityManager.flush();
  }

  /**
   *
   * @param caseId - Incident ID
   * @return report created for specific Incident
   */
  public Report findByCaseId(Long caseId) {
    Query q = entityManager.createQuery("select r from Report r where r.caseId = :caseId order by r.id desc ");
    q.setParameter("caseId", caseId);
    List resultList = q.getResultList();
    if (resultList == null || resultList.isEmpty()) {
      return null;
    }
    return (Report) resultList.get(0);
  }

}
