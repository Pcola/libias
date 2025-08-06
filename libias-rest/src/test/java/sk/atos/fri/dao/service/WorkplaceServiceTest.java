package sk.atos.fri.dao.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import sk.atos.fri.dao.libias.service.WorkplaceService;

@SpringBootTest
public class WorkplaceServiceTest extends AbstractDaoTest {

    @Autowired
    private WorkplaceService workplaceService;

    @Test
    @Transactional
    public void testGetAllRecords() {
        Assert.assertNotNull(workplaceService.getAllRecords());
    }

    @Test
    @Transactional
    public void testGet() {
        Assert.assertNotNull(workplaceService.get("01"));
    }

    @Test
    @Transactional
    public void testGetWorkplaces() {
        String[] roles = {"AS Augsburg", "AS Berlin", "AS Dortmund"};
        Assert.assertNotNull(workplaceService.getWorkplaces(roles, false));
    }

}
