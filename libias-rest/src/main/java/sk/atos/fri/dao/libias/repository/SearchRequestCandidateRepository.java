package sk.atos.fri.dao.libias.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.libias.model.SearchRequestCandidateEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public class SearchRequestCandidateRepository {

    private static final Logger LOG = LoggerFactory.getLogger(SearchRequestCandidateRepository.class);

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager em;

    public List<SearchRequestCandidateEntity> findByRequestId(Long requestId) {
        return em.createQuery(
                        "select c from SearchRequestCandidateEntity c where c.requestId = :reqId",
                        SearchRequestCandidateEntity.class)
                .setParameter("reqId", requestId)
                .getResultList();
    }

    @Transactional
    public SearchRequestCandidateEntity save(SearchRequestCandidateEntity entity) {
        return em.merge(entity);
    }

    @Transactional
    public void deleteByRequestId(Long requestId) {
        em.createQuery("delete from SearchRequestCandidateEntity c where c.requestId = :reqId")
                .setParameter("reqId", requestId)
                .executeUpdate();
    }

    public long countAll() {
        return em.createQuery("select count(c) from SearchRequestCandidateEntity c", Long.class)
                .getSingleResult();
    }
}