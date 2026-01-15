package sk.atos.fri.rest.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.fri.dao.libias.model.SearchRequestCandidateDto;
import sk.atos.fri.dao.libias.model.SearchRequestCandidateEntity;
import sk.atos.fri.dao.libias.model.SearchRequestDetailDto;
import sk.atos.fri.dao.libias.model.SearchRequestEntity;
import sk.atos.fri.dao.libias.model.SearchRequestListItemDto;
import sk.atos.fri.dao.libias.service.ISearchRequestService;
import sk.atos.fri.dao.libias.service.IdentificationBinningService;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.SearchRequestSearchRequest;
import sk.atos.fri.rest.model.SearchRequestSearchResponse;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping(path = "/search-requests")
public class SearchRequestController {

    private static final Logger LOG = new Logger();

    private final IdentificationBinningService identificationBinningService;
    private final ISearchRequestService searchRequestService;

    public SearchRequestController(IdentificationBinningService identificationBinningService, ISearchRequestService searchRequestService) {
        this.identificationBinningService = identificationBinningService;
        this.searchRequestService = searchRequestService;
    }

    @RequestMapping(
            path = "/{requestId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchRequestDetailDto> getById(@PathVariable("requestId") Long requestId,
                                                          HttpServletRequest httpServletRequest) {

        String username = resolveUsername(httpServletRequest);
        LOG.info(username, "Loading search request detail, requestId=" + requestId);

        IdentificationBinningService.SearchRequestData data = identificationBinningService.load(requestId);
        if (data == null || data.getRequest() == null) {
            LOG.info(username, "Search request not found, requestId=" + requestId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        SearchRequestDetailDto dto = toDetailDto(data);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @RequestMapping(
            path = "/search",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchRequestSearchResponse> search(@RequestBody SearchRequestSearchRequest request,
                                                              HttpServletRequest httpServletRequest) {
        String username = resolveUsername(httpServletRequest);
        LOG.info(username, "Searching search requests");

        List<SearchRequestListItemDto> results = searchRequestService.search(request);
        long totalCount = searchRequestService.count(request);

        return new ResponseEntity<>(new SearchRequestSearchResponse(results, totalCount), HttpStatus.OK);
    }

    private SearchRequestDetailDto toDetailDto(IdentificationBinningService.SearchRequestData data) {
        SearchRequestEntity e = data.getRequest();
        List<SearchRequestCandidateEntity> candidates = data.getCandidates();

        SearchRequestDetailDto dto = new SearchRequestDetailDto();
        dto.setRequestId(e.getRequestId());
        dto.setDateCreated(e.getDateCreated());
        dto.setCreatedBy(e.getCreatedBy());
        dto.setDateModified(e.getDateModified());
        dto.setModifiedBy(e.getModifiedBy());
        dto.setMaxCandidates(e.getMaxCandidates());
        dto.setMinScore(e.getMinScore());
        dto.setTransformation(e.getTransformation());

        try {
            Object bd = e.getBilddaten();
            if (bd != null) {
                String base64 = null;
                if (bd instanceof byte[]) {
                    base64 = Base64.getEncoder().encodeToString((byte[]) bd);
                } else if (bd instanceof Blob) {
                    Blob blob = (Blob) bd;
                    try (InputStream is = blob.getBinaryStream();
                         ByteArrayOutputStream os = new ByteArrayOutputStream()) {
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        base64 = Base64.getEncoder().encodeToString(os.toByteArray());
                    }
                } else if (bd instanceof String) {
                    base64 = (String) bd;
                } else {
                    base64 = bd.toString();
                }
                dto.setBilddaten(base64);
            } else {
                dto.setBilddaten(null);
            }
        } catch (Exception ex) {
            dto.setBilddaten(null);
        }

        List<SearchRequestCandidateDto> candidateDtos = new ArrayList<>();
        if (candidates != null) {
            for (SearchRequestCandidateEntity c : candidates) {
                SearchRequestCandidateDto cd = new SearchRequestCandidateDto();
                cd.setBildOid(c.getBildOid());
                cd.setRank(c.getRank());
                cd.setScore(c.getScore());
                candidateDtos.add(cd);
            }
        }
        dto.setCandidates(candidateDtos);

        return dto;
    }

    private String resolveUsername(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal != null ? principal.getName() : null;
    }
}