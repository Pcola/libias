package sk.atos.fri.dao.libias.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.IncidentHistoryCognitecEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class IncidentHistoryCognitecRepository {

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager em;

    @Transactional
    public IncidentHistoryCognitecEntity save(IncidentHistoryCognitecEntity entity) {
        if (entity == null) return null;
        if (entity.getHistoryId() == null) {
            em.persist(entity);
            return entity;
        } else {
            return em.merge(entity);
        }
    }
}