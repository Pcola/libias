package sk.atos.fri.ws.cognitec.service;

import com.cognitec.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.log.Logger;

import javax.annotation.PostConstruct;
import java.util.Random;

@Profile("localhost")
@Component
public class CognitecWSClientDummy extends WebServiceGatewaySupport implements ICognitecWSClient {

    private static final Logger LOGGER = new Logger();
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    private final Random random = new Random();

    @Autowired
    private ServerConfig serverConfig;

    @PostConstruct
    public void init() {
        LOGGER.info("Dummy Cognitec client in use");
    }

    @Override
    public StartDBEnrollmentResponse startEnrollment(String jobId) {
        LOGGER.info("Starting enrollment with jobId: " + jobId);
        return new StartDBEnrollmentResponse();
    }

    @Override
    public StartDBIdentificationResponse startDbIdentification(String jobId) {
        LOGGER.info("Starting identification with jobId: " + jobId);
        return new StartDBIdentificationResponse();
    }

    @Override
    public WaitForDBEnrollmentResponse waitForDBEnrollment() {
        LOGGER.info("Waiting for enrollment");
        return new WaitForDBEnrollmentResponse();
    }

    @Override
    public WaitForSyncResponse waitForSync() {
        LOGGER.info("Waiting for synchronization");
        return new WaitForSyncResponse();
    }

    @Override
    public WaitForDBIdentificationResponse waitForDBIdentification() {
        LOGGER.info("Waiting for identification");
        return new WaitForDBIdentificationResponse();
    }

    @Override
    public DeleteCaseResponse deleteCase(String caseId) {
        LOGGER.info("Deleting case with id: " + caseId);
        return new DeleteCaseResponse();
    }

    @Override
    public AnalyzePortraitResponse analyzePortrait(byte[] imageBytes) {
        LOGGER.info("Analyzing portrait");

        AnalyzePortraitResponse response = new AnalyzePortraitResponse();
        AnalyzePortraitResult result = new AnalyzePortraitResult();

        PortraitCharacteristics characteristics = new PortraitCharacteristics();
        characteristics.setNumberOfFaces(1);
        characteristics.setEyeDistance(100 + random.nextInt(100));

        Position eyeRight = new Position();
        eyeRight.setX(50 + random.nextInt(50));
        eyeRight.setY(200 + random.nextInt(50));
        characteristics.setRightEye(eyeRight);

        Position eyeLeft = new Position();
        eyeLeft.setX(200 + random.nextInt(50));
        eyeLeft.setY(200 + random.nextInt(50));
        characteristics.setLeftEye(eyeLeft);

        result.setFoundFace(true);
        result.setSuccessfulProcessed(true);
        result.setPortraitCharacteristics(characteristics);

        response.setVal(result);
        return response;
    }

    @Override
    public VerificationPortraitsResponse verificationPortraits(byte[] imageABytes, byte[] imageBBytes, String authName) {
        LOGGER.info("Verifying portraits");

        VerificationPortraitsResponse verificationPortraitsResponse = new VerificationPortraitsResponse();
        VerificationPortraitsResult verificationResult = new VerificationPortraitsResult();

        verificationResult.setScore(random.nextFloat());

        ImageProcessingInfo imageAInfo = new ImageProcessingInfo();
        imageAInfo.setFoundFace(true);

        ImageProcessingInfo imageBInfo = new ImageProcessingInfo();
        imageBInfo.setFoundFace(true);

        verificationResult.setProcessedImageA(imageAInfo);
        verificationResult.setProcessedImageB(imageBInfo);

        verificationPortraitsResponse.setVal(verificationResult);
        return verificationPortraitsResponse;
    }

    @Override
    public IdentBinningResponse identificationBinning(byte[] imageBytes, String authName, int maxMatches, int minScore) {
        LOGGER.info("Identifying portrait");

        IdentBinningResponse identBinningResponse = new IdentBinningResponse();
        IdentificationResult identificationResult = new IdentificationResult();

        identificationResult.setTransactionId("FakeId");

        ImageProcessingInfo processedImage = new ImageProcessingInfo();
        processedImage.setFoundFace(true);

        FaceLocation faceLocation = new FaceLocation();
        processedImage.setFaceLocation(faceLocation);

        Position eyeRight = new Position();
        eyeRight.setX(50 + random.nextInt(50));
        eyeRight.setY(200 + random.nextInt(50));
        faceLocation.setRightEye(OBJECT_FACTORY.createFaceLocationRightEye(eyeRight));

        Position eyeLeft = new Position();
        eyeLeft.setX(200 + random.nextInt(50));
        eyeLeft.setY(200 + random.nextInt(50));
        faceLocation.setLeftEye(OBJECT_FACTORY.createFaceLocationLeftEye(eyeLeft));

        identificationResult.setProcessedImage(processedImage);
        identificationResult.setMatches(getMatchSet(maxMatches));

        identBinningResponse.setVal(identificationResult);
        return identBinningResponse;
    }

    private static MatchSet getMatchSet(int maxMatches) {
        MatchSet matchSet = new MatchSet();
        for (int i = 1; i <= maxMatches; i++) {
            Match match = new Match();
            match.setCaseID((i % 2 == 1) ? "1" : "2");
            match.setRank(i);
            match.setScore((100 - i) / (float) 100);
            matchSet.getM().add(match);
        }
        return matchSet;
    }

}
