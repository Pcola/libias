package sk.atos.fri.dao.libias.service;

import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.libias.model.Workplace;
import sk.atos.fri.rest.model.SiteStatistics;

/**
 *
 * @author Jaroslav Kollar
 */
@Repository
public class StatisticService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  @Autowired
  private WorkplaceService workplaceService;

  @Autowired
  private ServerConfig serverConfig;

  /**
   *
   * @return workplace statistics of incidents for each priority and status
   */
  public List<SiteStatistics> getSiteStatistics() {

    // count all incidents with assigned workplace
    Query q = entityManager.createQuery("select i.workplace.id, i.workplace.workplace, i.priority.priorityId, i.status.statusId, count(i.status.statusId) "
        + " from Incident i "
        + " where i.filter = 0 "
        + " group by i.workplace.id, i.workplace.workplace, i.priority.priorityId, i.status.statusId "
        + " order by i.workplace.id, i.priority.priorityId, i.status.statusId");

    List<Object[]> resultList = q.getResultList();

    List<SiteStatistics> stats = new LinkedList<>();
    if (resultList != null) {
      resultList.forEach((result) -> {
        stats.add(new SiteStatistics((String) result[0], (String) result[1], (Long) result[2], (Long) result[3], (Long) result[4]));
      });
    }

  if (serverConfig.getStatisticsDisplayZeroLines()) {
    // find all other workplaces without assigned incidents
    List<Workplace> allWorkplaces = workplaceService.getAllRecords();
    allWorkplaces.forEach(w -> {
      boolean foundDienstelle = false;
      for (SiteStatistics s : stats) {
        if (s.getSiteId().equalsIgnoreCase(w.getId())) {
          foundDienstelle = true;
          break;
        }
      }
      if (!foundDienstelle) {
        stats.add(new SiteStatistics(w.getId(), w.getWorkplace(), null, 0L, 0L));
      }
    });
  }

    // count all incidents without assigned workplace
    q = entityManager.createQuery("select '0' as id, 'NO_DIENSTSTELLE' as dienststelle, i.priority.priorityId, i.status.statusId, count(i.status.statusId) "
        + " from Incident i "
        + " where i.filter = 0 and i.workplace is null "
        + " group by i.priority.priorityId, i.status.statusId "
        + " order by i.priority.priorityId, i.status.statusId");

    resultList = q.getResultList();

    if (resultList != null) {
      resultList.forEach((result) -> {
        stats.add(new SiteStatistics((String) result[0], (String) result[1], (Long) result[2], (Long) result[3], (Long) result[4]));
      });
    }

    return stats;
  }

}
