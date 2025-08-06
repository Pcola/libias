package sk.atos.fri.dao.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.model.UserRole;
import sk.atos.fri.dao.libias.service.UserRole2BamUserService;
import sk.atos.fri.dao.libias.service.UserRoleService;
import sk.atos.fri.dao.libias.service.UserService;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author : A761498, Kamil Macek
 * @since : 14 Nov 2019
 **/
@SpringBootTest
public class UserRole2BamUserServiceTest extends AbstractDaoTest {

    @Autowired
    private UserRole2BamUserService userRole2BamUserService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testRemoveUserRoleRecords() {
        userRole2BamUserService.removeUserRoleRecords(1L);
    }

    @Test
    @Transactional
    public void testSaveRoles() {
        BamUser bamUser = new BamUser();
        bamUser.setUsername("skuska");
        bamUser.setFirstName("Skuska");
        bamUser.setLastName("Skuska");

        bamUser = userService.persist(bamUser);

        UserRole userRole = userRoleService.get(1L);
        Collection<UserRole> userRoles = new ArrayList<>();
        userRoles.add(userRole);


        bamUser.setUserRoleCollection(userRoles);

        userRole2BamUserService.saveRoles(bamUser);
    }

}
