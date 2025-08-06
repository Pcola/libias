package sk.atos.fri.dao.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.service.IncidentHistoryService;

/**
 * @author : A761498, Kamil Macek
 * @since : 14 Nov 2019
 **/
@SpringBootTest
public class IncidentHistoryServiceTest extends AbstractDaoTest {

    @Autowired
    private IncidentHistoryService incidentHistoryService;

    @Test
    @Transactional
    public void testGetNewID() {
        Assert.assertNotNull(incidentHistoryService.getNewID());
    }

}
