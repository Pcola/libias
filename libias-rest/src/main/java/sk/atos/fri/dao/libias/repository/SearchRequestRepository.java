package sk.atos.fri.dao.libias.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.libias.model.SearchRequestEntity;

import sk.atos.fri.rest.model.SearchRequestSearchRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class SearchRequestRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SearchRequestEntity.class);

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager em;

    public Optional<SearchRequestEntity> findById(Long requestId) {
        return Optional.ofNullable(em.find(SearchRequestEntity.class, requestId));
    }

    @Transactional
    public SearchRequestEntity save(SearchRequestEntity entity) {
        if (entity.getRequestId() == null) {
            em.persist(entity);
            em.flush();
            return entity;
        } else {
            SearchRequestEntity merged = em.merge(entity);
            em.flush();
            return merged;
        }
    }

    @Transactional
    public SearchRequestEntity update(SearchRequestEntity entity) {
        SearchRequestEntity merged = em.merge(entity);
        em.flush();
        return merged;
    }

    @Transactional
    public void deleteById(Long requestId) {
        SearchRequestEntity ref = em.find(SearchRequestEntity.class, requestId);
        if (ref != null) {
            em.remove(ref);
            em.flush();
        }
    }

    public long countAll() {
        return em.createQuery("select count(sr) from SearchRequestEntity sr", Long.class)
                .getSingleResult();
    }

    public List<SearchRequestEntity> findByCreatedBy(String createdBy, int first, int max) {
        return em.createQuery("select sr from SearchRequestEntity sr where sr.createdBy = :createdBy order by sr.dateCreated desc",
                        SearchRequestEntity.class)
                .setParameter("createdBy", createdBy)
                .setFirstResult(Math.max(0, first))
                .setMaxResults(Math.max(1, Math.min(max, 500)))
                .getResultList();
    }

    @Transactional
    public void touchModified(Long requestId, Date when, String username) {
        em.createQuery("update SearchRequestEntity sr " +
                        "set sr.dateModified = :d, sr.modifiedBy = :u " +
                        "where sr.requestId = :id")
                .setParameter("d", when)
                .setParameter("u", username)
                .setParameter("id", requestId)
                .executeUpdate();
    }

    public List<SearchRequestEntity> search(SearchRequestSearchRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<SearchRequestEntity> cq = cb.createQuery(SearchRequestEntity.class);
        Root<SearchRequestEntity> root = cq.from(SearchRequestEntity.class);

        root.fetch("candidates", JoinType.LEFT);

        cq.select(root).distinct(true);
        applyFilters(cb, cq, root, request);
        applySorting(cb, cq, root, request);

        TypedQuery<SearchRequestEntity> query = em.createQuery(cq);
        applyPagination(query, request);

        return query.getResultList();
    }

    public long count(SearchRequestSearchRequest request) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<SearchRequestEntity> root = cq.from(SearchRequestEntity.class);

        cq.select(cb.count(root));
        applyFilters(cb, cq, root, request);

        return em.createQuery(cq).getSingleResult();
    }

    private void applyFilters(CriteriaBuilder cb, CriteriaQuery<?> cq, Root<SearchRequestEntity> root, SearchRequestSearchRequest request) {
        List<Predicate> predicates = new ArrayList<>();

        if (request.getRequestId() != null) {
            predicates.add(cb.equal(root.get("requestId"), request.getRequestId()));
        }

        if (request.getDateFrom() != null && !request.getDateFrom().isEmpty()) {
            try {
                Date dateFrom = new SimpleDateFormat("dd.MM.yyyy").parse(request.getDateFrom());
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateCreated"), dateFrom));
            } catch (Exception e) {
                LOG.error("Error parsing dateFrom: " + request.getDateFrom(), e);
            }
        }

        if (request.getDateTo() != null && !request.getDateTo().isEmpty()) {
            try {
                Date dateTo = new SimpleDateFormat("dd.MM.yyyy").parse(request.getDateTo());
                predicates.add(cb.lessThanOrEqualTo(root.get("dateModified"), dateTo));
            } catch (Exception e) {
                LOG.error("Error parsing dateTo: " + request.getDateTo(), e);
            }
        }

        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            predicates.add(cb.or(
                    cb.equal(root.get("createdBy"), request.getUsername()),
                    cb.equal(root.get("modifiedBy"), request.getUsername())
            ));
        }

        if (request.getDepartmentId() != null && !request.getDepartmentId().isEmpty()) {
            predicates.add(cb.equal(root.get("dienststelleId"), request.getDepartmentId()));
        }

        if (!predicates.isEmpty()) {
            cq.where(cb.and(predicates.toArray(new Predicate[0])));
        }
    }

    private void applySorting(CriteriaBuilder cb, CriteriaQuery<SearchRequestEntity> cq, Root<SearchRequestEntity> root, SearchRequestSearchRequest request) {
        String sortField = request.getSort();
        Integer sortOrder = request.getOrder();

        if (sortField != null && !sortField.isEmpty()) {
            if (sortOrder != null && sortOrder == -1) {
                cq.orderBy(cb.desc(root.get(sortField)));
            } else {
                cq.orderBy(cb.asc(root.get(sortField)));
            }
        } else {
            cq.orderBy(cb.desc(root.get("dateCreated")));
        }
    }

    private void applyPagination(TypedQuery<?> query, SearchRequestSearchRequest request) {
        if (request.getFirst() != null) {
            query.setFirstResult(request.getFirst());
        }
        if (request.getRows() != null) {
            query.setMaxResults(request.getRows());
        }
    }
}