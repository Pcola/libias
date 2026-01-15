package sk.atos.mre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="sk.atos.mre")
public class MREApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(MREApplication.class, args);
  }
}
