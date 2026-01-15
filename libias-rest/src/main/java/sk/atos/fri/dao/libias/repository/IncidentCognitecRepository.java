package sk.atos.fri.dao.libias.repository;

import org.eclipse.persistence.config.QueryHints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.libias.enums.SourceSystem;
import sk.atos.fri.dao.libias.model.*;

@Repository
public class IncidentCognitecRepository {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentCognitecRepository.class);


    @PersistenceContext(unitName = "libias-pu")
    private EntityManager em;

    private final ServerConfig serverConfig;

    private volatile String incidentQueryHint;

    public IncidentCognitecRepository(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public IncidentCognitecEntity findByCaseId(Long caseId) {
        return em.find(IncidentCognitecEntity.class, caseId);
    }

    public int countAll() {
        TypedQuery<Long> q = em.createQuery(
                "select count(i) from IncidentCognitecEntity i where i.filter = 0", Long.class);
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
                        "from IncidentCognitecEntity i " +
                        "where i.filter = 0 " +
                        "order by i.referenceType asc", String.class);
        addQueryHint(q);
        List<String> res = q.getResultList();
        res.removeAll(Collections.singleton(null));
        return res;
    }

    public List<String> findAllReferenceType(SourceSystem sourceSystem) {
        TypedQuery<String> q = em.createQuery(
                "select distinct i.referenceType " +
                        "from IncidentCognitecEntity i " +
                        "where i.filter = 0 " +
                        "order by i.referenceType asc", String.class);
        addQueryHint(q);
        List<String> res = q.getResultList();
        res.removeAll(Collections.singleton(null));
        return res;
    }

    public List<String> findAllNationalities() {
        TypedQuery<String> qa = em.createQuery(
                "select i.aNationality from IncidentCognitecEntity i where i.filter = 0",
                String.class);
        TypedQuery<String> qb = em.createQuery(
                "select i.bNationality from IncidentCognitecEntity i where i.filter = 0",
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

    @Transactional
    public List<Long> getImageOidsWhereMissingPersonData() {
        List<Long> ares = em.createQuery(
                "SELECT i.probeId FROM IncidentCognitecEntity i WHERE i.filter = 0 AND i.aApplicantOid IS NULL",
                Long.class
        ).getResultList();

        List<Long> bres = em.createQuery(
                "SELECT i.galleryId FROM IncidentCognitecEntity i WHERE i.filter = 0 AND i.bApplicantOid IS NULL",
                Long.class
        ).getResultList();

        return Stream.concat(ares.stream(), bres.stream())
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Transactional
    public List<IncidentCognitecEntity> getIncidentEntities(IncidentFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<IncidentCognitecEntity> q = cb.createQuery(IncidentCognitecEntity.class);
        Root<IncidentCognitecEntity> c = q.from(IncidentCognitecEntity.class);
        q.select(c);

        List<Predicate> predicates = new ArrayList<>();
        if (filter.getFilter() != null) {
            predicates.add(cb.equal(c.get("filter"), filter.getFilter()));
        }

        if (filter.getStatus() != null) {
            predicates.add(cb.equal(c.get("status"), new Status((long) filter.getStatus().id)));
        }

        if (filter.getAktenzeichen() != null) {
            String aktenzeichen = filter.getAktenzeichen();
            predicates.add(cb.or(cb.equal(c.get("aFileNumber"), aktenzeichen), cb.equal(c.get("bFileNumber"), aktenzeichen)));
        }

        if (filter.getAntragstellerOid() != null) {
            Long angtragstellerOid = filter.getAntragstellerOid();
            predicates.add(cb.or(cb.equal(c.get("aApplicantOid"), angtragstellerOid), cb.equal(c.get("bApplicantOid"), angtragstellerOid)));
        }

        if (filter.getImageOid() != null) {
            Long imageOid = filter.getImageOid();
            predicates.add(cb.or(cb.equal(c.get("probeId"), imageOid), cb.equal(c.get("galleryId"), imageOid)));
        }

        if (!predicates.isEmpty()) {
            q.where(cb.and(predicates.toArray(new Predicate[0])));
        }

        TypedQuery<IncidentCognitecEntity> query = em.createQuery(q);
        return query.getResultList();
    }

    @Transactional
    public int updateIncident(IncidentCognitecEntity incident) {
        if (incident == null) return 0;
        em.merge(incident);
        em.flush();
        LOG.info("Case with caseId {} updated", incident.getCaseId());
        return 1;
    }

    public List<IncidentCognitecEntity> findByAkzForDeletion(String akz) {
        if (akz == null) return Collections.emptyList();

        TypedQuery<IncidentCognitecEntity> q = em.createQuery(
                "SELECT i FROM IncidentCognitecEntity i " +
                        "WHERE i.filter = 0 AND (i.aFileNumber = :akz OR i.bFileNumber = :akz)",
                IncidentCognitecEntity.class
        );
        q.setParameter("akz", akz);
        addQueryHint(q);
        return q.getResultList();
    }

    @Transactional
    public void saveOrMerge(IncidentCognitecEntity e) {
        if (e == null) return;
        if (e.getCaseId() != null && em.find(IncidentCognitecEntity.class, e.getCaseId()) != null) {
            em.merge(e);
        } else {
            em.persist(e);
        }
    }

    public List<IncidentCognitecEntity> findByPkzForDeletion(Long pkz) {
        if (pkz == null) return Collections.emptyList();

        TypedQuery<IncidentCognitecEntity> q = em.createQuery(
                "SELECT i FROM IncidentCognitecEntity i " +
                        "WHERE i.filter = 0 AND (i.aPkz = :pkz OR i.bPkz = :pkz)",
                IncidentCognitecEntity.class
        );
        q.setParameter("pkz", pkz);
        addQueryHint(q);
        return q.getResultList();
    }
}
