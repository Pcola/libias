package sk.atos.fri.ws.ges.service;


import sk.atos.fri.ws.ges.service.model.AnalyseRequest;
import sk.atos.fri.ws.ges.service.model.AnalyseResponse;
import sk.atos.fri.ws.ges.service.model.BaseIndexAuth;
import sk.atos.fri.ws.ges.service.model.CompareRequest;
import sk.atos.fri.ws.ges.service.model.CompareResponse;
import sk.atos.fri.ws.ges.service.model.DeleteRequest;
import sk.atos.fri.ws.ges.service.model.EnrollRequest;
import sk.atos.fri.ws.ges.service.model.EnrollResponse;
import sk.atos.fri.ws.ges.service.model.ReturnElasticsearch;
import sk.atos.fri.ws.ges.service.model.SearchResponse;
import sk.atos.fri.ws.ges.service.model.SearchWithEmbeddingRequest;
import sk.atos.fri.ws.ges.service.model.SearchWithImageRequest;

import java.util.List;
import java.util.Map;

public interface IGesClient {

    EnrollResponse enroll(EnrollRequest req);

    Map<String, Object> deleteDocument(DeleteRequest req);

    SearchResponse searchWithImage(SearchWithImageRequest req);

    SearchResponse searchWithEmbedding(SearchWithEmbeddingRequest req);

    AnalyseResponse analyseImage(AnalyseRequest req);

    CompareResponse compare(CompareRequest req);

    List<ReturnElasticsearch> getFailureCases(BaseIndexAuth req);

    List<ReturnElasticsearch> getAllWithAutomaticDecision(BaseIndexAuth req);

    List<ReturnElasticsearch> getAllWithManualDecision(BaseIndexAuth req);

    Map<String, Object> countOfDocs(BaseIndexAuth req);
}