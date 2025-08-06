package sk.atos.fri.dao.libias.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.model.UserRole;
import sk.atos.fri.dao.libias.model.UserRole2BamUser;
import sk.atos.fri.log.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Collection;
import java.util.List;

/**
 * @author : A761498, Kamil Macek
 * @since : 23 Sep 2019
 **/
@Repository
@Transactional
public class UserRole2BamUserService {

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager entityManager;

    /**
     *
     * @param userId - BamUser ID
     * @param roleId - UserRole ID
     *
     * Called when inserting logged user in dB, to persist user roles
     */
    private void addNewRecord(Long userId, Long roleId) {
        UserRole2BamUser userRole2BamUser = new UserRole2BamUser();
        userRole2BamUser.getPk().setUserId(userId);
        userRole2BamUser.getPk().setRoleId(roleId);

        entityManager.persist(userRole2BamUser);
        entityManager.flush();
    }

    /**
     *
     * @param userId - BamUser ID
     *
     * Called when logging out. Delete all roles from dB for logging out user
     */
    public void removeUserRoleRecords(Long userId) {
        Query query = entityManager.createQuery("delete from UserRole2BamUser ur where ur.pk.userId = :userId");
        query.setParameter("userId", userId);
        query.executeUpdate();

        entityManager.flush();
    }

    /**
     *
     * @param bamUser - Logged user
     *
     * Go trough user roles and persist them
     */
    public void saveRoles(BamUser bamUser) {
        for(UserRole userRole : bamUser.getUserRoleCollection()) {
            addNewRecord(bamUser.getUserId(), userRole.getRoleId());
        }
    }
}
