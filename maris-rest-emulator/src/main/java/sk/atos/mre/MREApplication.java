package sk.atos.mre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableEurekaClient
//@EnableDiscoveryClient
@ComponentScan(basePackages="sk.atos.mre")
public class MREApplication extends SpringBootServletInitializer {
  public static void main(String[] args) {
    SpringApplication.run(MREApplication.class, args);
  }
}
