package sk.atos.fri.ws.cognitec.service;

import com.cognitec.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import sk.atos.fri.configuration.ServerConfig;

import javax.xml.bind.JAXBElement;

@Component
@Profile("!localhost")
public class CognitecWSClient extends WebServiceGatewaySupport implements ICognitecWSClient {

  private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

  @Autowired
  private ServerConfig serverConfig;

  @Override
  public StartDBEnrollmentResponse startEnrollment(String jobId) {
    StartDBEnrollment request = new StartDBEnrollment();
    request.setJobId(OBJECT_FACTORY.createStartDBEnrollmentJobId(jobId));
    StartDBEnrollmentResponse response = (StartDBEnrollmentResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    return response;
  }

  @Override
  public StartDBIdentificationResponse startDbIdentification(String jobId) {
    StartDBIdentification request = new StartDBIdentification();
    request.setJobId(OBJECT_FACTORY.createStartDBIdentificationJobId(jobId));
    request.setFacility(OBJECT_FACTORY.createStartDBIdentificationFacility(serverConfig.getCognitecFacility()));
    StartDBIdentificationResponse response = (StartDBIdentificationResponse) getWebServiceTemplate().marshalSendAndReceive(request);
    return response;
  }

  @Override
  public WaitForDBEnrollmentResponse waitForDBEnrollment() {
    WaitForDBEnrollment request = new WaitForDBEnrollment();
    return (WaitForDBEnrollmentResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public WaitForSyncResponse waitForSync() {
    WaitForSync request = new WaitForSync();
    return (WaitForSyncResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public WaitForDBIdentificationResponse waitForDBIdentification() {
    WaitForDBIdentification request = new WaitForDBIdentification();
    request.setFacility(OBJECT_FACTORY.createWaitForDBIdentificationFacility(serverConfig.getCognitecFacility()));
    return (WaitForDBIdentificationResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public DeleteCaseResponse deleteCase(String caseId) {
    FvdbDeleteCase request = new FvdbDeleteCase();
    request.setCaseID(OBJECT_FACTORY.createFvdbDeleteCaseCaseID(caseId));
    return (DeleteCaseResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public AnalyzePortraitResponse analyzePortrait(byte[] imageBytes) {
    Image createImage = OBJECT_FACTORY.createImage();
    createImage.setBinaryImg(imageBytes);

    AnalyzePortrait request = new AnalyzePortrait();
    request.setImage(OBJECT_FACTORY.createAnalyzePortraitImage(createImage));
    return (AnalyzePortraitResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public FindFacesResponse findFaces(byte[] imageBytes) {
    Image createImage = OBJECT_FACTORY.createImage();
    createImage.setBinaryImg(imageBytes);

    FindFaces request = new FindFaces();
    request.setImage(OBJECT_FACTORY.createFindFacesImage(createImage));
    return (FindFacesResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public VerificationPortraitsResponse verificationPortraits(byte[] imageABytes, byte[] imageBBytes, String authName) {
    Image createImageA = OBJECT_FACTORY.createImage();
    createImageA.setBinaryImg(imageABytes);
    JAXBElement<Image> imageA = OBJECT_FACTORY.createVerificationPortraitsImageA(createImageA);

    Image createImageB = OBJECT_FACTORY.createImage();
    createImageB.setBinaryImg(imageBBytes);
    JAXBElement<Image> imageB = OBJECT_FACTORY.createVerificationPortraitsImageB(createImageB);

    VerificationPortraits request = new VerificationPortraits();
    request.setImageA(imageA);
    request.setImageB(imageB);
    //request.setAuthName(OBJECT_FACTORY.createVerificationPortraitsAuthName(authName));
    return (VerificationPortraitsResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }

  @Override
  public IdentBinningResponse identificationBinning(byte[] imageBytes, String authName, int maxMatches, int minScore) {
    Image createImage = OBJECT_FACTORY.createImage();
    createImage.setBinaryImg(imageBytes);

    JAXBElement<Image> createIdentificationBinningImage = OBJECT_FACTORY.createIdentificationBinningImage(createImage);

    IdentificationBinning request = new IdentificationBinning();
    request.setImage(createIdentificationBinningImage);
    request.setAuthName(OBJECT_FACTORY.createIdentificationBinningAuthName(authName));
    request.setMatchListThreshold((float) minScore / 100);
    request.setMaxMatches(maxMatches);
    request.setPriority(Integer.parseInt(serverConfig.getIdentificationBinningPriority()));
    return (IdentBinningResponse) getWebServiceTemplate().marshalSendAndReceive(request);
  }
}
