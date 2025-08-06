package sk.atos.fri.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:release.properties")
public class ReleaseInfoConfig {

  @Autowired
  private Environment env;

  public String getReleaseVersion() {
    return env.getProperty("release.version");
  }

}
