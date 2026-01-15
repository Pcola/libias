package sk.atos.fri.dao.libias.service;

import com.cognitec.IdentificationResult;
import com.cognitec.Match;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.dao.libias.model.SearchRequestCandidateEntity;
import sk.atos.fri.dao.libias.model.SearchRequestEntity;
import sk.atos.fri.dao.libias.repository.SearchRequestCandidateRepository;
import sk.atos.fri.dao.libias.repository.SearchRequestRepository;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.IdentBinningRequest;
import sk.atos.fri.rest.model.IdentBinningResponse;
import sk.atos.fri.rest.model.ImageType;
import sk.atos.fri.ws.cognitec.service.ICognitecWSClient;

import java.util.Date;
import java.util.List;

@Service
public class IdentificationBinningService {

    private static final Logger LOG = new Logger();

    private final SearchRequestRepository searchRequestRepository;
    private final SearchRequestCandidateRepository candidateRepository;
    private final ICognitecWSClient cognitecWSClient;

    public IdentificationBinningService(SearchRequestRepository searchRequestRepository,
                                        SearchRequestCandidateRepository candidateRepository,
                                        ICognitecWSClient cognitecWSClient) {
        this.searchRequestRepository = searchRequestRepository;
        this.candidateRepository = candidateRepository;
        this.cognitecWSClient = cognitecWSClient;
    }

    @Transactional
    public IdentBinningResponse process(IdentBinningRequest req, String username) {
        Long requestId = req.getRequestId();
        Date now = new Date();

        SearchRequestEntity searchRequest;

        if (requestId != null) {
            searchRequest = searchRequestRepository.findById(requestId).orElse(null);

            if (searchRequest != null) {
                LOG.info(username, "IdentificationBinning: re-processing existing requestId=" + requestId);

                searchRequest.setDateModified(now);
                searchRequest.setModifiedBy(username);
                searchRequest.setBilddaten(req.getImg());
                searchRequest.setMaxCandidates(req.getMaxMatches());
                searchRequest.setMinScore((double) req.getMinScore());
                searchRequest.setTransformation(req.getImgType() != null ? req.getImgType().name() : null);

                searchRequestRepository.save(searchRequest);

                candidateRepository.deleteByRequestId(requestId);
            } else {
                LOG.info(username, "IdentificationBinning: requestId=" + requestId + " not found, creating new request");
                searchRequest = createAndSaveSearchRequest(req, username, now);
                requestId = searchRequest.getRequestId();
            }
        } else {
            LOG.info(username, "IdentificationBinning: creating new request (no requestId provided)");
            searchRequest = createAndSaveSearchRequest(req, username, now);
            requestId = searchRequest.getRequestId();
        }

            IdentificationResult liveResult =
                    callCognitecAndPersistCandidates(requestId, req, username);

            return buildResponse(liveResult, req.getImgType(), requestId);
    }

    @Transactional(readOnly = true)
    public SearchRequestData load(Long requestId) {
        SearchRequestEntity request = searchRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            return null;
        }
        List<SearchRequestCandidateEntity> candidates = candidateRepository.findByRequestId(requestId);
        SearchRequestData data = new SearchRequestData();
        data.setRequest(request);
        data.setCandidates(candidates);
        return data;
    }

    // -------------------------------------------------------------------------
    //  vytvorenie a uloženie SEARCH_REQUEST
    // -------------------------------------------------------------------------
    private SearchRequestEntity createAndSaveSearchRequest(IdentBinningRequest req,
                                                           String username,
                                                           Date now) {

        SearchRequestEntity sr = new SearchRequestEntity();
        sr.setDateCreated(now);
        sr.setCreatedBy(username);
        sr.setDateModified(now);
        sr.setModifiedBy(username);
        sr.setBilddaten(req.getImg());
        sr.setMaxCandidates(req.getMaxMatches());
        sr.setMinScore((double) req.getMinScore());
        sr.setTransformation(req.getImgType() != null ? req.getImgType().name() : null);

        SearchRequestEntity saved = searchRequestRepository.save(sr);

        return saved;
    }

    // -------------------------------------------------------------------------
    //  volanie Cognitec + persist kandidátov
    // -------------------------------------------------------------------------
    private IdentificationResult callCognitecAndPersistCandidates(Long requestId,
                                                                  IdentBinningRequest req,
                                                                  String username) {
        int maxMatches = req.getMaxMatches() * 3;
        int minScore = req.getMinScore();

        LOG.info(username, "IdentificationBinning: calling Cognitec, requestId=" + requestId
                + ", maxMatches=" + maxMatches + ", minScore=" + minScore);

        IdentificationResult liveResult = cognitecWSClient
                .identificationBinning(req.getImg(), username, maxMatches, minScore)
                .getVal();

        persistCandidatesFromResult(requestId, liveResult, username);

        return liveResult;
    }

    // -------------------------------------------------------------------------
    //  uloženie kandidátov do SEARCH_REQUEST_CANDIDATE
    // -------------------------------------------------------------------------
    private void persistCandidatesFromResult(Long requestId,
                                             IdentificationResult result,
                                             String username) {
        if (result == null || result.getMatches() == null || result.getMatches().getM() == null) {
            LOG.info("IdentificationBinning: no matches to persist for requestId=" + requestId);
            return;
        }

        List<Match> matches = result.getMatches().getM();

        for (Match m : matches) {
            SearchRequestCandidateEntity candidate = new SearchRequestCandidateEntity();
            candidate.setRequestId(requestId);
            candidate.setRank(m.getRank());
            candidate.setBildOid(parseLongOrNull(m.getCaseID()));
            candidate.setScore((double) m.getScore());
            candidateRepository.save(candidate);
        }

        searchRequestRepository.touchModified(requestId, new Date(), username);

        LOG.info("IdentificationBinning: persisted " + matches.size() + " candidates for requestId=" + requestId);
    }


    // -------------------------------------------------------------------------
    //  build response object
    // -------------------------------------------------------------------------
    private IdentBinningResponse buildResponse(IdentificationResult result, ImageType imgType, Long requestId) {
        IdentBinningResponse resp = new IdentBinningResponse();
        resp.setVal(result);
        resp.setImgType(imgType);
        resp.setRequestId(requestId);
        return resp;
    }

    private Long parseLongOrNull(String s) {
        if (s == null) {
            return null;
        }
        try {
            return Long.valueOf(s.trim());
        } catch (NumberFormatException e) {
            LOG.warn("IdentificationBinning: unable to parse '{}' as Long for bildOid", s);
            return null;
        }
    }

    public static class SearchRequestData {
        private SearchRequestEntity request;
        private List<SearchRequestCandidateEntity> candidates;

        public SearchRequestEntity getRequest() {
            return request;
        }

        public void setRequest(SearchRequestEntity request) {
            this.request = request;
        }

        public List<SearchRequestCandidateEntity> getCandidates() {
            return candidates;
        }

        public void setCandidates(List<SearchRequestCandidateEntity> candidates) {
            this.candidates = candidates;
        }
    }
}