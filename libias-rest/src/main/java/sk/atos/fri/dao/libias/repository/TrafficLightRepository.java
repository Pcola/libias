package sk.atos.fri.dao.libias.repository;

import org.springframework.stereotype.Repository;
import sk.atos.fri.dao.libias.model.TrafficLight;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TrafficLightRepository {

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager em;

    public List<TrafficLight> findAll() {
        return em.createQuery("SELECT t FROM TrafficLight t ORDER BY t.roleId", TrafficLight.class)
                .getResultList();
    }

    public Optional<TrafficLight> findById(Long roleId) {
        return Optional.ofNullable(em.find(TrafficLight.class, roleId));
    }

    /**
     * create – očakáva sa, že ROLE_ID je nastavené a v DB ešte neexistuje
     */
    public TrafficLight create(TrafficLight entity) {
        em.persist(entity);
        return entity;
    }

    /**
     * update – načíta existujúci záznam a zmení polia; vyhadzuje IllegalArgumentException ak neexistuje
     */
    public TrafficLight update(Long roleId, TrafficLight src) {
        TrafficLight target = em.find(TrafficLight.class, roleId);
        if (target == null) {
            throw new IllegalArgumentException("TrafficLight not found: roleId=" + roleId);
        }
        target.setScoreFrom(src.getScoreFrom());
        target.setScoreTo(src.getScoreTo());
        target.setCommentRed(src.getCommentRed());
        target.setCommentYellow(src.getCommentYellow());
        target.setCommentGreen(src.getCommentGreen());
        return target;
    }

    public void delete(Long roleId) {
        TrafficLight found = em.find(TrafficLight.class, roleId);
        if (found == null) {
            throw new IllegalArgumentException("TrafficLight not found: roleId=" + roleId);
        }
        em.remove(found);
    }

    public boolean existsById(Long roleId) {
        Long cnt = em.createQuery(
                        "SELECT COUNT(t) FROM TrafficLight t WHERE t.roleId = :id", Long.class)
                .setParameter("id", roleId)
                .getSingleResult();
        return cnt != 0L;
    }
}