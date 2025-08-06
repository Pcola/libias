package sk.atos.fri;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.NoSuchAlgorithmException;

/**
 * @author : A761498, Kamil Macek
 * @since : 14 Nov 2019
 **/
public class UtilsTest {

    private BCryptPasswordEncoder passwordEncoder;

    @Before
    public void setup() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    public void testEncodeBCrypt() throws NoSuchAlgorithmException {
        String s = "admin123";
        String encoded = passwordEncoder.encode(s);
        System.out.println("BCrypt: " + encoded);
        Assert.assertTrue(passwordEncoder.matches(s, "$2a$10$cURZ9pp4De82a94FqRAPIud77Injewl7Ptu0wLKP7ztJM.BsZJ9rO"));
    }

}
