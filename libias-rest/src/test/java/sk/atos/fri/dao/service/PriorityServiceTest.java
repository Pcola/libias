package sk.atos.fri.dao.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import sk.atos.fri.dao.libias.service.PriorityService;

@SpringBootTest
public class PriorityServiceTest extends AbstractDaoTest {

    @Autowired
    private PriorityService priorityService;

    @Test
    @Transactional
    public void testGetAllRecords() {
        Assert.assertNotNull(priorityService.getAllRecords());
    }

    @Test
    @Transactional
    public void testGet() {
        Assert.assertNotNull(priorityService.get(1L));
    }

}
