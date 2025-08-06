package sk.atos.fri.ws.cognitec.service;

import com.cognitec.*;

public interface ICognitecWSClient {
    StartDBEnrollmentResponse startEnrollment(String jobId);

    StartDBIdentificationResponse startDbIdentification(String jobId);

    WaitForDBEnrollmentResponse waitForDBEnrollment();

    WaitForSyncResponse waitForSync();

    WaitForDBIdentificationResponse waitForDBIdentification();

    DeleteCaseResponse deleteCase(String caseId);

    AnalyzePortraitResponse analyzePortrait(byte[] imageBytes);

    VerificationPortraitsResponse verificationPortraits(byte[] imageABytes, byte[] imageBBytes, String authName);

    IdentBinningResponse identificationBinning(byte[] imageBytes, String authName, int maxMatches, int minScore);
}
