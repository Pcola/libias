package sk.atos.fri.dao.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.service.ImageService;

/**
 * @author : A761498, Kamil Macek
 * @since : 14 Nov 2019
 **/
@SpringBootTest
public class ImageServiceTest extends AbstractDaoTest {

    @Autowired
    private ImageService imageService;

    @Test
    @Transactional
    public void testGet() {
        imageService.get(1L);
    }

}
