package sk.atos.fri.dao.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.UserRole;
import sk.atos.fri.dao.libias.service.UserRoleService;

import java.util.Collection;
import java.util.List;

/**
 * @author : A761498, Kamil Macek
 * @since : 13 Nov 2019
 **/
@SpringBootTest
public class UserRoleServiceTest extends AbstractDaoTest {

    @Autowired
    private UserRoleService userRoleService;

    @Test
    @Transactional
    public void testGetAll() {
        List<UserRole> userRoles = userRoleService.getAllRecords();

        Assert.assertFalse(userRoles.isEmpty());
    }

    @Test
    @Transactional
    public void testGetUserRole() {
        for (int i = 1; i < 7; i++) {
            UserRole userRole = userRoleService.get((long) i);

            Assert.assertNotNull(userRole);
        }
    }

    @Test
    @Transactional
    public void testGetRoles() {
        String[] roles = {"Administrator", "Aussenstellerbenutzer", "Sucher"};
        Collection<UserRole> userRoles = userRoleService.getRoles(roles, false);

        Assert.assertNotNull(userRoles);
    }

    @Test
    @Transactional
    public void testGetAllUserRoles() {
        Collection<UserRole> userRoles = userRoleService.getAllUserRoles(1L);

        Assert.assertNotNull(userRoles);
    }
}
