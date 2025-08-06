package sk.atos.fri.dao.service;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author : A761498, Kamil Macek
 * @since : 14 Nov 2019
 **/
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AbstractDaoTest.class})
@ComponentScan(basePackages = {"sk.atos.fri", "sk.atos.fri.rest.service", "sk.atos.fri.pdf", "sk.atos.fri.dao.libias.service"})
@ActiveProfiles({"test"})
@Ignore
public class AbstractDaoTest {
}
