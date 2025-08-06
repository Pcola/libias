package sk.atos.fri.dao.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.BamUser;
import sk.atos.fri.dao.libias.service.UserService;


/**
 * @author : A761498, Kamil Macek
 * @since : 27 Sep 2019
 **/
@SpringBootTest
public class UserServiceTest extends AbstractDaoTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testFind() {
        Assert.assertNotNull(userService.find("admin"));
    }

    @Test
    @Transactional
    public void testFindAll() {
        Assert.assertNotNull(userService.findAll());
    }

    @Test
    @Transactional
    public void testGet() {
        Assert.assertNotNull(userService.get(1L));
    }

    @Test
    @Transactional
    public void testDeleteUser() {
        BamUser bamUser = new BamUser();
        bamUser.setUsername("skuska");
        bamUser.setFirstName("Skuska");
        bamUser.setLastName("Skuska");

        bamUser = userService.persist(bamUser);
        userService.delete(bamUser);

        Assert.assertNull(userService.find("skuska"));
    }

    @Test
    @Transactional
    public void testPersist() {
        BamUser bamUser = new BamUser();
        bamUser.setUsername("skuska");
        bamUser.setFirstName("Skuska");
        bamUser.setLastName("Skuska");

        bamUser = userService.persist(bamUser);

        Assert.assertNotNull(bamUser);
    }
}
