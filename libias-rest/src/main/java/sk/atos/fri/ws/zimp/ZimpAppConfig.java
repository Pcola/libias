package sk.atos.fri.ws.zimp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.zimp.ApiClient;
import sk.atos.fri.zimp.api.BestandsabfrageApi;

@Configuration
public class ZimpAppConfig {

    @Autowired
    private ServerConfig serverConfig;

    @Bean
    public RestTemplate zimpRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    @Primary
    public ApiClient zimpApiClient(RestTemplate restTemplate) {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(serverConfig.getZimpBaseUrl());
        return apiClient;
    }

    @Bean
    @Primary
    public BestandsabfrageApi bestandsabfrageApi(ApiClient zimpApiClient) {
        return new BestandsabfrageApi(zimpApiClient);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .findAndRegisterModules();
    }
}