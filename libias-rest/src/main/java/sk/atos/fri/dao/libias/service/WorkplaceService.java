package sk.atos.fri.dao.libias.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.libias.model.Workplace;

@Repository
public class WorkplaceService {

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  /**
   *
   * @return all Workplace records in dB
   */
  public List<Workplace> getAllRecords() {
    return entityManager.createQuery("select w from Workplace w order by w.id ASC").getResultList();
  }

  /**
   *
   * @param id
   * @return specific Workplace with id
   */
  public Workplace get(String id) {
    return entityManager.find(Workplace.class, id);
  }

  /**
   *
   * @param role - idm role - role from auth webgate
   * @return valid Workplace
   */
  private Workplace getWorkplaceByIdmName(String role) {
    Query query = entityManager.createQuery("select w from Workplace w where lower(w.idm_role) = lower(:role)");
    query.setParameter("role", role);

    List resultList = query.getResultList();

    if(resultList.size() > 0) {
      return (Workplace) resultList.get(0);
    } else {
      return null;
    }
  }

  /**
   *
   * @param roles - string array of workplace
   * @param findByIdmName - flag if workplacec are sent from auth webgate (including idm workplace) or not (including normal workplace names)
   * @return collection of valid Workplace
   */
  public Collection<Workplace> getWorkplaces(String[] roles, boolean findByIdmName) {
    Collection<Workplace> workplaces = new ArrayList<>();

    for(String string : roles) {
      Workplace role;

      if(findByIdmName) {
        role = getWorkplaceByIdmName(string);
      } else {
        role = get(string);
      }

      if(role != null) {
        workplaces.add(role);
      }
    }

    return workplaces;
  }
}
