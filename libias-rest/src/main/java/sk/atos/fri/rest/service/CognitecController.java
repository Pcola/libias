package sk.atos.fri.rest.service;

import com.cognitec.AnalyzePortraitResponse;
import com.cognitec.FindFacesResponse;
import com.cognitec.VerificationPortraitsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import sk.atos.fri.dao.cognitec.model.CognitecImages;
import sk.atos.fri.dao.cognitec.service.CognitecImagesService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.AnalyzePortraitRequest;
import sk.atos.fri.rest.model.IdentBinningRequest;
import sk.atos.fri.rest.model.IdentBinningResponse;
import sk.atos.fri.rest.model.VerificationPortraitsRequest;
import sk.atos.fri.ws.cognitec.service.ICognitecWSClient;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/cognitec")
public class CognitecController {

  @Autowired
  private Logger LOG;

  @Autowired
  private CognitecImagesService cognitecImagesService;

  @Autowired
  private ICognitecWSClient cognitecWSClient;

  /**
   *
   * @param oid - image ID
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return CognitecImages for specific image ID
   */
  @RequestMapping(path = "/image/{oid}",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public CognitecImages getCognitecImage(@PathVariable String oid, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      return cognitecImagesService.get(oid);
    } catch (Exception e) {
      LOG.error(username, Error.GET_COGNITEC_IMAGE, e);
      throw e;
    }
  }

  @RequestMapping(path = "/image/count",
          method = RequestMethod.GET,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public long getCountAll() {
    return cognitecImagesService.countAll();
  }

  /**
   *
   * @param request - AnalyzePortraitRequest - it contains image which was loaded as user input
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return AnalyzePortraitResponse
   */
  @RequestMapping(path = "/analyzePortrait",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public AnalyzePortraitResponse analyzePortrait(@RequestBody AnalyzePortraitRequest request, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Analyzing image");
      return cognitecWSClient.analyzePortrait(request.getImg());
    } catch (Exception e) {
      LOG.error(username, Error.GET_COGNITEC_IMAGE, e);
      throw e;
    }
  }

  /**
   *
   * @param request - VerificationPortraitsRequest - it contains 2 images ti verify
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return VerificationPortraitsResponse - including processed images and score
   */
  @RequestMapping(path = "/verificationPortraits",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public VerificationPortraitsResponse verificationPortraits(@RequestBody VerificationPortraitsRequest request, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Verificating portraits");
      return cognitecWSClient.verificationPortraits(request.getImg1(), request.getImg2(), username);
    } catch (Exception e) {
      LOG.error(username, Error.GET_COGNITEC_IMAGE, e);
      throw e;
    }
  }

  /**
   *
   * @param request - IdentBinningRequest - contains image as user input
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return IdentBinningResponse - contains related images (Matches) with score and photo
   */
  @RequestMapping(path = "/identBinning",
                  method = RequestMethod.POST,
                  produces = MediaType.APPLICATION_JSON_VALUE)
  public IdentBinningResponse identificationBinning(@RequestBody IdentBinningRequest request, HttpServletRequest httpServletRequest) {
    String username = httpServletRequest.getUserPrincipal().getName();
    int maxMatches = request.getMaxMatches() * 3;
    int minScore = request.getMinScore();

    IdentBinningResponse response = new IdentBinningResponse();
    response.setImgType(request.getImgType());

    try {
      LOG.info(username, "Starting identification, maxMatches = " + maxMatches + ", minScore = " + minScore);
      response.setVal(cognitecWSClient.identificationBinning(request.getImg(), username, maxMatches, minScore).getVal());
      return response;
    } catch (Exception e) {
      LOG.error(username, Error.GET_COGNITEC_IMAGE, e);
      throw e;
    }
  }

  @RequestMapping(path = "/findFaces",
          method = RequestMethod.POST,
          produces = MediaType.APPLICATION_JSON_VALUE)
  public FindFacesResponse findFaces(@RequestBody AnalyzePortraitRequest request, HttpServletRequest httpServletRequest) {
    String username = null;
    try {
      username = httpServletRequest.getUserPrincipal().getName();
      LOG.info(username, "Finding faces in image");
      return cognitecWSClient.findFaces(request.getImg());
    } catch (Exception e) {
      String errMsg = e.getMessage().toLowerCase();
      if (errMsg.contains("error processing portrait image")) {
        LOG.error(username, Error.valueOf("Error processing portrait image"));
        LOG.info(username, "Response = " + e.getMessage());
        return new FindFacesResponse();
      } else {
        LOG.error(username, Error.valueOf("Error getting Cognitec response"), e);
        throw e;
      }
    }
  }
}
