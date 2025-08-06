package sk.atos.fri.rest.service;

import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.maris.model.PersonResponse;
import sk.atos.fri.ws.maris.service.MarisWSClient;

/**
 *
 * @author Jaroslav Kollar
 */
public class MarisCallBildPerTask implements Runnable {
  private final Logger LOG = new Logger();
  
  private final int identificator;
  private final MarisWSClient marisClient;
  private final Long bildId;
  
  public MarisCallBildPerTask(int identificator, MarisWSClient marisClient, Long bildId) {
    this.identificator = identificator;
    this.marisClient = marisClient;
    this.bildId = bildId;
  }  
  
  @Override
  public void run() {        
    PersonResponse response = marisClient.getPerson(bildId);
    if (identificator == 99) {
      LOG.debug("Bild performance completed");
    } else if (identificator % 10 == 0) {
      LOG.debug("Partial bild performance status: " + identificator);
      if(response != null) LOG.debug("Partial response: " + response.getPkz());
    }
  }  
}
