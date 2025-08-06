package sk.atos.fri.rest.service;

import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.maris.service.MarisWSClient;

/**
 *
 * @author Jaroslav Kollar
 */
public class MarisCallAktePerTask implements Runnable {
  private final Logger LOG = new Logger();
  
  private final int identificator;
  private final MarisWSClient marisClient;
  private final String aktenzeichenA;
  private final String aktenzeichenB;
         
  public MarisCallAktePerTask(int identificator, MarisWSClient marisClient, String aktenzeichenA, String aktenzeichenB) {
    this.identificator = identificator;
    this.marisClient = marisClient; 
    this.aktenzeichenA = aktenzeichenA;
    this.aktenzeichenB = aktenzeichenB;
  }  
  
  @Override
  public void run() {        
    marisClient.getAkte(aktenzeichenA, aktenzeichenB);
    if (identificator == 99) {
      LOG.debug("Bild performance completed");
    } else if (identificator % 10 == 0) {
      LOG.debug("Partial bild performance status: " + identificator);
    }
  }  
}
