package sk.atos.fri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author a605053
 */
@SpringBootApplication
@ComponentScan(basePackages="sk.atos.fri")
@ServletComponentScan(basePackages = "sk.atos.fri.dataImport")
public class LibiasApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(LibiasApplication.class, args);
  }
}
