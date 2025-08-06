package sk.atos.fri.dao.libias.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.libias.model.IncidentHistory;
import sk.atos.fri.log.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @author : A761498, Kamil Macek
 * @since : 8/1/2019
 **/
@Repository
public class IncidentHistoryService {

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager entityManager;

    /**
     *
     * @return new IncidentHistory ID
     *
     * Generating id based on max value +1 in IncidentHistory PK column
     */
    public Long getNewID() {
        Query q = entityManager.createQuery("select max(ic.historyId) from IncidentHistory ic");
        String result = String.valueOf(q.getSingleResult());

        if(result.equals("null")) {
            return 1L;
        } else {
            Long newID = Long.valueOf(result);
            return newID + 1;
        }
    }
}
