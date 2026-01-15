package sk.atos.fri.ws.ges.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.log.Logger;

@Configuration
public class GesAppConfig {

    private static final Logger LOGGER = new Logger();

    @Autowired
    private ServerConfig serverConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Profile("!localhost")
    @Primary
    public IGesClient gesClientRest(RestTemplate restTemplate) {
        LOGGER.info("GES REST client (v2) configured - production profil");
        String baseUrl = serverConfig.getGesBaseUrl();
        return new GesClientRest(restTemplate, baseUrl,objectMapper);
    }

    @Bean
    @Profile("localhost")
    public IGesClient gesClientDummy() {
        LOGGER.info("Dummy GES client (v2) configured - localhost profil");
        return new GesClientDummy();
    }
}
