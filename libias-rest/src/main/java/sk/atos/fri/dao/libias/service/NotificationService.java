package sk.atos.fri.dao.libias.service;

import org.springframework.stereotype.Service;

import sk.atos.fri.dao.libias.model.Notification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager entityManager;

    /**
     * Retrieves all notifications that are currently valid.
     *
     * @return a list of valid Notifications
     */
    public List<Notification> getAllValid() {
        TypedQuery<Notification> query = entityManager.createQuery("SELECT n FROM Notification n WHERE n.validFrom <= :now AND n.validTo >= :now", Notification.class);
        query.setParameter("now", new Date());
        return query.getResultList();
    }

    public Notification findById(Long id) {
        return entityManager.find(Notification.class, id);
    }

    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            entityManager.persist(notification);
        } else {
            entityManager.merge(notification);
        }
        entityManager.flush();
        return notification;
    }

    public void delete(Notification notification) {
        entityManager.remove(notification);
    }
}
