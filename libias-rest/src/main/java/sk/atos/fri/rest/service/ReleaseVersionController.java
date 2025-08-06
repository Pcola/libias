package sk.atos.fri.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.rest.ReleaseInfoConfig;

@RestController
@RequestMapping(path = "/version")
public class ReleaseVersionController {

  @Autowired
  private ReleaseInfoConfig releaseInfoConfig;

  /**
   *
   * @return version info
   */
  @RequestMapping(path = "/server",
                  method = RequestMethod.GET,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public String getJobStatus() {
    return "{\"version\":\"" + releaseInfoConfig.getReleaseVersion() + "\"}";
  }
}
