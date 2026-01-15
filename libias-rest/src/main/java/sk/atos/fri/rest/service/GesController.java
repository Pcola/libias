package sk.atos.fri.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.ges.service.IGesClient;
import sk.atos.fri.ws.ges.service.IGesClient.*;
import sk.atos.fri.ws.ges.service.model.*;

@RestController
@RequestMapping("/ges")
public class GesController {

    private static final Logger LOGGER = new Logger();

    @Autowired
    private IGesClient gesClient;

    @PostMapping("/v2/analyse_image")
    public AnalyseResponse analyzeImage(@RequestBody AnalyseRequest request) {
        LOGGER.info("GesRestController: analyzeImage requested");
        return gesClient.analyseImage(request);
    }

    @PostMapping("/v2/compare")
    public CompareResponse compare(@RequestBody CompareRequest request) {
        LOGGER.info("GesRestController: compare requested");
        return gesClient.compare(request);
    }

    @PostMapping("/enroll")
    public EnrollResponse enroll(@RequestBody EnrollRequest request) {
        LOGGER.info("GesRestController: enroll requested");
        return gesClient.enroll(request);
    }

    @PostMapping("/search")
    public SearchResponse searchWithImage(@RequestBody SearchWithImageRequest request) {
        LOGGER.info("GesRestController: searchWithImage requested");
        return gesClient.searchWithImage(request);
    }

    @PostMapping("/search_with_embedding")
    public SearchResponse searchWithEmbedding(@RequestBody SearchWithEmbeddingRequest request) {
        LOGGER.info("GesRestController: searchWithEmbedding requested");
        return gesClient.searchWithEmbedding(request);
    }

    @PostMapping("/delete_document")
    public Object deleteDocument(@RequestBody DeleteRequest request) {
        LOGGER.info("GesRestController: deleteDocument requested");
        return gesClient.deleteDocument(request);
    }

    @PostMapping("/get_failure_cases")
    public Object getFailureCases(@RequestBody BaseIndexAuth request) {
        LOGGER.info("GesRestController: getFailureCases requested");
        return gesClient.getFailureCases(request);
    }

    @PostMapping("/get_all_with_automatic_decision")
    public Object getAllWithAutomaticDecision(@RequestBody BaseIndexAuth request) {
        LOGGER.info("GesRestController: getAllWithAutomaticDecision requested");
        return gesClient.getAllWithAutomaticDecision(request);
    }

    @PostMapping("/get_all_manual_decision")
    public Object getAllWithManualDecision(@RequestBody BaseIndexAuth request) {
        LOGGER.info("GesRestController: getAllWithManualDecision requested");
        return gesClient.getAllWithManualDecision(request);
    }

    @PostMapping("/count_of_docs")
    public Object countOfDocs(@RequestBody BaseIndexAuth request) {
        LOGGER.info("GesRestController: countOfDocs requested");
        return gesClient.countOfDocs(request);
    }
}