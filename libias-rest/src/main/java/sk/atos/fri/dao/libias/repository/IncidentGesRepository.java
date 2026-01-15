package sk.atos.fri.dao.libias.repository;

import org.eclipse.persistence.config.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.libias.model.IncidentGesEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class IncidentGesRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentGesRepository.class);

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager em;

    private final ServerConfig serverConfig;

    private volatile String incidentQueryHint;

    public IncidentGesRepository(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public IncidentGesEntity findByCaseId(Long caseId) {
        return em.find(IncidentGesEntity.class, caseId);
    }


    @Transactional
    public IncidentGesEntity saveOrMerge(IncidentGesEntity e) {
        if (e == null) return null;
        if (e.getCaseId() != null && em.find(IncidentGesEntity.class, e.getCaseId()) != null) {
            return em.merge(e);
        } else {
            em.persist(e);
            return e;
        }
    }

    public int countAll() {
        TypedQuery<Long> q = em.createQuery(
                "select count(i) from IncidentGesEntity i where i.filter = 0", Long.class);
        addQueryHint(q);
        return q.getSingleResult().intValue();
    }

    public void addQueryHint(Query query) {
        if (incidentQueryHint == null) {
            synchronized (this) {
                if (incidentQueryHint == null) {
                    String hint = serverConfig.getIncidentQueryHint();
                    incidentQueryHint = (hint != null && !hint.isEmpty()) ? ("/*+ " + hint + " */") : "";
                    LOG.info("incidentQueryHint set to {}", incidentQueryHint);
                }
            }
        }
        if (incidentQueryHint != null && !incidentQueryHint.isEmpty()) {
            try {
                query.setHint(QueryHints.HINT, incidentQueryHint);
            } catch (IllegalArgumentException ignore) {
            }
        }
    }

    public List<String> findAllReferenceType() {
        TypedQuery<String> q = em.createQuery(
                "select distinct i.referenceType " +
                        "from IncidentGesEntity i " +
                        "where i.filter = 0 " +
                        "order by i.referenceType asc", String.class);
        addQueryHint(q);
        List<String> res = q.getResultList();
        res.removeAll(Collections.singleton(null));
        return res;
    }

    public List<String> findAllNationalities() {
        TypedQuery<String> qa = em.createQuery(
                "select i.aNationality from IncidentGesEntity i where i.filter = 0",
                String.class);
        TypedQuery<String> qb = em.createQuery(
                "select i.bNationality from IncidentGesEntity i where i.filter = 0",
                String.class);
        addQueryHint(qa);
        addQueryHint(qb);
        return Stream.concat(qa.getResultList().stream(), qb.getResultList().stream())
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getNationalitiesSearcher() {
        return em.createQuery("select n.nationality from Nationality n", String.class)
                .getResultList();
    }

}