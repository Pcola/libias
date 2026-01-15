package sk.atos.fri.ws.ges.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
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

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


public class GesClientRest implements IGesClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public GesClientRest(RestTemplate restTemplate,
                         @Value("${ges.base-url}")
                         String baseUrl,
                         ObjectMapper objectMapper) {
        this.restTemplate = Objects.requireNonNull(restTemplate);
        this.baseUrl = normalizeBaseUrl(Objects.requireNonNull(baseUrl));
        this.objectMapper = objectMapper;
    }

    @Override
    public EnrollResponse enroll(EnrollRequest req) {
        UriComponentsBuilder builder = createBuilder("/enroll", req);

        Map<String, String> body = createImageBody(req.getImg());

        return post(builder, body, EnrollResponse.class);
    }

    @Override
    public Map<String, Object> deleteDocument(DeleteRequest req) {
        UriComponentsBuilder builder = createBuilder("/delete_document", req);
        return post(builder, null, Map.class);
    }

    @Override
    public SearchResponse searchWithImage(SearchWithImageRequest req) {
        UriComponentsBuilder builder = createBuilder("/search", req);
        Map<String, String> body = createImageBody(req.getImg());

        return post(builder, body, SearchResponse.class);
    }

    @Override
    public SearchResponse searchWithEmbedding(SearchWithEmbeddingRequest req) {
        UriComponentsBuilder builder = createBuilder("/search_with_embedding", req);
        Map<String, Object> body = Collections.singletonMap("embedding", req.getEmbedding());

        return post(builder, body, SearchResponse.class);
    }

    @Override
    public AnalyseResponse analyseImage(AnalyseRequest req) {
        UriComponentsBuilder builder = createBuilder("/v2/analyse_image", req);
        Map<String, String> body = createImageBody(req.getImg());

        return post(builder, body, AnalyseResponse.class);
    }

    @Override
    public CompareResponse compare(CompareRequest req) {
        UriComponentsBuilder builder = createBuilder("/v2/compare", req)
                .replaceQueryParam("embedding_0", join(req.getEmbedding_0()))
                .replaceQueryParam("embedding_1", join(req.getEmbedding_1()));

        return post(builder, null, CompareResponse.class);
    }

    @Override
    public List<ReturnElasticsearch> getFailureCases(BaseIndexAuth req) {
        return getList("/get_failure_cases", req);
    }

    @Override
    public List<ReturnElasticsearch> getAllWithAutomaticDecision(BaseIndexAuth req) {
        return getList("/get_all_with_automatic_decision", req);
    }

    @Override
    public List<ReturnElasticsearch> getAllWithManualDecision(BaseIndexAuth req) {
        return getList("/get_all_manual_decision", req);
    }

    @Override
    public Map<String, Object> countOfDocs(BaseIndexAuth req) {
        UriComponentsBuilder builder = createBuilder("/count_of_docs", req);
        return post(builder, null, Map.class);
    }

    // --- HELPER METHODS  ---

    private List<ReturnElasticsearch> getList(String path, Object requestParams) {
        UriComponentsBuilder builder = createBuilder(path, requestParams);
        ReturnElasticsearch[] response = post(builder, null, ReturnElasticsearch[].class);
        return response != null ? Arrays.asList(response) : Collections.emptyList();
    }

    private UriComponentsBuilder createBuilder(String path, Object requestDto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + path);

        if (requestDto == null) {
            return builder;
        }

        Map<String, Object> map = objectMapper.convertValue(requestDto, new TypeReference<Map<String, Object>>() {
        });

        map.forEach((k, v) -> {
            if (v != null && !k.equals("img") && !k.equals("embedding")) {
                if (v instanceof List) {
                    builder.queryParam(k, joinList((List<?>) v));
                } else {
                    builder.queryParam(k, v);
                }
            }
        });

        return builder;
    }

    private <T> T post(UriComponentsBuilder builder, Object body, Class<T> responseType) {
        URI uri = builder.build().encode().toUri();
        HttpEntity<?> entity = new HttpEntity<>(body, defaultJsonHeaders());

        ResponseEntity<T> response = restTemplate.exchange(uri, HttpMethod.POST, entity, responseType);
        return response.getBody();
    }

    private Map<String, String> createImageBody(byte[] imgData) {
        if (imgData == null) return Collections.singletonMap("img", null);
        return Collections.singletonMap("img", Base64Utils.encodeToString(imgData));
    }

    private String joinList(List<?> list) {
        return list.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private String join(List<Double> nums) {
        if (nums == null) return null;
        return joinList(nums);
    }

    private HttpHeaders defaultJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String normalizeBaseUrl(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}