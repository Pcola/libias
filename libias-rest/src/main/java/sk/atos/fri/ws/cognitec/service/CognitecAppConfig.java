package sk.atos.fri.ws.cognitec.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import sk.atos.fri.configuration.ServerConfig;

@Configuration
@EnableWs
public class CognitecAppConfig {

  @Autowired
  private ServerConfig serverConfig;

  @Bean
  public Jaxb2Marshaller marshaller() {
    Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
    marshaller.setContextPath("com.cognitec");
    return marshaller;
  }

  @Bean
  @Profile("!localhost")
  @Primary
  public ICognitecWSClient cognitecWSClient(Jaxb2Marshaller marshaller) {
    CognitecWSClient client = new CognitecWSClient();
    client.setDefaultUri(serverConfig.getCognitecSoapUrl());
    client.setMarshaller(marshaller);
    client.setUnmarshaller(marshaller);
    return client;
  }

  @Bean
  @Profile("localhost")
  public ICognitecWSClient cognitecWSClientDummy() {
    return new CognitecWSClientDummy();
  }
}
