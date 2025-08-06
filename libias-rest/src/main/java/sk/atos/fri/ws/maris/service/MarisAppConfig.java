package sk.atos.fri.ws.maris.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.config.annotation.EnableWs;
import sk.atos.fri.configuration.ServerConfig;

/**
 *
 * @author Jaroslav Kollar
 */
@Configuration
@EnableWs
public class MarisAppConfig {

  @Autowired
  private ServerConfig serverConfig;

  @Bean
  public MarisWSClient marisClient() {
    MarisWSClient client = new MarisWSClient();
    client.setPersonUrl(serverConfig.getMarisPersonUrl());
    client.setAkteUrl(serverConfig.getMarisAkteUrl());
    client.setDeletedPersonsUrl(serverConfig.getMarisDeletedPersonsUrl());
    client.setDeletedFilesUrl(serverConfig.getMarisDeletedFilesUrl());
    client.setLockedFilesUrl(serverConfig.getMarisLockedFilesUrl());
    client.setUpdateUrl(serverConfig.getMarisUpdateUrl());
    client.setTokenUrl(serverConfig.getMarisTokenUrl());
    client.setUsername(serverConfig.getMarisTokenUsername());
    client.setPassword(serverConfig.getMarisTokenPassword());
    client.setMarisImageInfoServiceUrl(serverConfig.getMarisImageInfoServiceUrl());
    client.setMarisDeletedFilesServiceUrl(serverConfig.getMarisDeletedFilesServiceUrl());
    client.setMarisDeletedPersonsServiceUrl(serverConfig.getMarisDeletedPersonsServiceUrl());
    client.setMarisLockedFilesServiceUrl(serverConfig.getMarisLockedFilesServiceUrl());
    client.setMarisOauthServiceUrl(serverConfig.getMarisOauthServiceUrl());
    return client;
  }

}
