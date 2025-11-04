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

    private static final boolean GENERATE_MORE_FACES = true;

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

    @Override
    public FindFacesResponse findFaces(byte[] imageBytes) {
        double randomNr = Math.random();
        if (randomNr < 0.1) {
            throw new RuntimeException("error processing portrait image: Unable to find enough landmarks.");
        } else if (randomNr < 0.2) {
            FindFacesResponse response = new FindFacesResponse();
            response.setVal(new FaceLocations());
            return response;
        }

        Producer producer = new Producer();
        producer.setId("BAMF");
        producer.setDomain(Domain.ALGO);

        // Central position for returned faces (example)
        Position positionCenter = new Position();
        positionCenter.setX(135f);
        positionCenter.setY(195f);

        // Bounding box
        BoundingBox boundingBox = new BoundingBox();
        boundingBox.setCenter(positionCenter);
        boundingBox.setWidth(220f);
        boundingBox.setHeight(220f);
        boundingBox.setAlpha(0.04f);

        // Create landmark positions (example values close to the bbox centre)
        Position leftEye = new Position();
        leftEye.setX(110f + (float)(Math.random() * 20f));   // small random jitter
        leftEye.setY(170f + (float)(Math.random() * 20f));

        Position rightEye = new Position();
        rightEye.setX(160f + (float)(Math.random() * 20f));
        rightEye.setY(170f + (float)(Math.random() * 20f));

        Position noseTip = new Position();
        noseTip.setX(135f + (float)(Math.random() * 10f));
        noseTip.setY(195f + (float)(Math.random() * 10f));

        Position leftMouthCorner = new Position();
        leftMouthCorner.setX(115f + (float)(Math.random() * 15f));
        leftMouthCorner.setY(230f + (float)(Math.random() * 15f));

        Position rightMouthCorner = new Position();
        rightMouthCorner.setX(155f + (float)(Math.random() * 15f));
        rightMouthCorner.setY(230f + (float)(Math.random() * 15f));

        // Fill FaceLocation and set landmarks using OBJECT_FACTORY (JAXB elements)
        FaceLocation faceLocation = new FaceLocation();
        faceLocation.setProducer(producer);
        faceLocation.setBoundingBox(boundingBox);
        faceLocation.setConfidence(2.5f);

        // IMPORTANT: use OBJECT_FACTORY.createFaceLocationX(...) so the JAXB element wrappers are present
        faceLocation.setLeftEye(OBJECT_FACTORY.createFaceLocationLeftEye(leftEye));
        faceLocation.setRightEye(OBJECT_FACTORY.createFaceLocationRightEye(rightEye));
        faceLocation.setNoseTip(OBJECT_FACTORY.createFaceLocationNoseTip(noseTip));
        faceLocation.setLeftMouthCorner(OBJECT_FACTORY.createFaceLocationLeftMouthCorner(leftMouthCorner));
        faceLocation.setRightMouthCorner(OBJECT_FACTORY.createFaceLocationRightMouthCorner(rightMouthCorner));

        FaceLocations faceLocations = new FaceLocations();
        faceLocations.getFaces().add(faceLocation);

        // Optionally generate a second face (keeps behaviour from original dummy)
        if (GENERATE_MORE_FACES) {
            BoundingBox boundingBox2 = new BoundingBox();
            boundingBox2.setCenter(positionCenter);
            boundingBox2.setWidth(230f);
            boundingBox2.setHeight(230f);
            boundingBox2.setAlpha(0.04f);

            // second face landmarks (slightly different)
            Position leftEye2 = new Position();
            leftEye2.setX(105f + (float)(Math.random() * 25f));
            leftEye2.setY(165f + (float)(Math.random() * 25f));

            Position rightEye2 = new Position();
            rightEye2.setX(165f + (float)(Math.random() * 25f));
            rightEye2.setY(165f + (float)(Math.random() * 25f));

            Position noseTip2 = new Position();
            noseTip2.setX(135f + (float)(Math.random() * 15f));
            noseTip2.setY(190f + (float)(Math.random() * 15f));

            Position leftMouthCorner2 = new Position();
            leftMouthCorner2.setX(105f + (float)(Math.random() * 20f));
            leftMouthCorner2.setY(225f + (float)(Math.random() * 20f));

            Position rightMouthCorner2 = new Position();
            rightMouthCorner2.setX(165f + (float)(Math.random() * 20f));
            rightMouthCorner2.setY(225f + (float)(Math.random() * 20f));

            FaceLocation faceLocation2 = new FaceLocation();
            faceLocation2.setProducer(producer);
            faceLocation2.setBoundingBox(boundingBox2);
            faceLocation2.setConfidence(1.5f);
            faceLocation2.setLeftEye(OBJECT_FACTORY.createFaceLocationLeftEye(leftEye2));
            faceLocation2.setRightEye(OBJECT_FACTORY.createFaceLocationRightEye(rightEye2));
            faceLocation2.setNoseTip(OBJECT_FACTORY.createFaceLocationNoseTip(noseTip2));
            faceLocation2.setLeftMouthCorner(OBJECT_FACTORY.createFaceLocationLeftMouthCorner(leftMouthCorner2));
            faceLocation2.setRightMouthCorner(OBJECT_FACTORY.createFaceLocationRightMouthCorner(rightMouthCorner2));

            faceLocations.getFaces().add(faceLocation2);
        }

        FindFacesResponse response = new FindFacesResponse();
        response.setVal(faceLocations);
        return response;
    }

}
