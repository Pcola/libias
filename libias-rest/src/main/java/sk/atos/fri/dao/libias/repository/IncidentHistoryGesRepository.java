package sk.atos.fri.dao.libias.repository;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class IncidentHistoryGesRepository {

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager entityManager;

    /**
     *
     * @return new IncidentHistory ID
     *
     * Generating id based on max value +1 in IncidentHistory PK column
     */
    public Long getNewID() {
        Query q = entityManager.createQuery("select max(ic.historyId) from IncidentHistoryGesEntity ic");
        String result = String.valueOf(q.getSingleResult());

        if(result.equals("null")) {
            return 1L;
        } else {
            Long newID = Long.valueOf(result);
            return newID + 1;
        }
    }
}