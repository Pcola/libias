package sk.atos.fri.dao.libias.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.libias.model.IncidentGesEntity;
import sk.atos.fri.dao.libias.model.Priority;
import sk.atos.fri.dao.libias.model.Status;
import sk.atos.fri.dao.libias.repository.IncidentGesRepository;
import sk.atos.fri.ws.ges.service.IGesClient;
import sk.atos.fri.ws.ges.service.model.EnrollResponse;
import sk.atos.fri.ws.ges.service.model.ReturnSearchHit;
import sk.atos.fri.ws.ges.service.model.SearchResponse;
import sk.atos.fri.ws.ges.service.model.SearchWithEmbeddingRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

@Service
public class GesIncidentService {

    private static final Logger LOG = LoggerFactory.getLogger(GesIncidentService.class);

    @PersistenceContext(unitName = "libias-pu")
    private EntityManager entityManager;

    private final IGesClient gesClient;
    private final IncidentGesRepository incidentGesRepository;

    private final String gesElasticUser;
    private final String gesElasticPwd;
    private final String gesIndexName;
    private final int searchSize;

    public GesIncidentService(IGesClient gesClient,
                              IncidentGesRepository incidentGesRepository,
                              ServerConfig serverConfig) {
        this.gesClient = gesClient;
        this.incidentGesRepository = incidentGesRepository;
        this.gesElasticUser = serverConfig.getGesElasticUser();
        this.gesElasticPwd = serverConfig.getGesElasticPwd();
        this.gesIndexName = serverConfig.getGesIndexName();
        this.searchSize = serverConfig.getSearchSize();
    }

    @Transactional
    public void createIncidentsFromEnrollResult(Long probeId,
                                                EnrollResponse enrollResponse,
                                                String jobId,
                                                Date pDate) {

        if (enrollResponse == null) {
            LOG.warn("EnrollResponse or id is null, cannot run search_with_embedding.");
            return;
        }

        SearchWithEmbeddingRequest searchReq = getSearchWithEmbeddingRequest(enrollResponse);

        SearchResponse searchResponse = gesClient.searchWithEmbedding(searchReq);
        List<ReturnSearchHit> hits = searchResponse != null ? searchResponse.getHits() : null;

        if (hits == null || hits.isEmpty()) {
            LOG.info("No hits returned from search_with_embedding for probeId={}", probeId);
            return;
        }

        LOG.info("search_with_embedding returned {} hits for probeId={}",
                hits.size(), probeId);

        short defaultRank = 1;
        for (ReturnSearchHit hit : hits) {
            IncidentGesEntity entity = new IncidentGesEntity();
            entity.setProbeId(probeId);
            entity.setGalleryId(hit.getId());
            entity.setScore(hit.getScore());
            entity.setCreatedDate(pDate);
            entity.setFilter((short) 0);
            entity.setPriority(entityManager.getReference(Priority.class, (short) 0));
            entity.setStatus(entityManager.getReference(Status.class, 1));
            Short rank = hit.getRank() != null
                    ? hit.getRank().shortValue()
                    : defaultRank++;
            entity.setRank(rank);
            entity.setJobid(jobId);

            incidentGesRepository.saveOrMerge(entity);
        }
    }

    private SearchWithEmbeddingRequest getSearchWithEmbeddingRequest(EnrollResponse enrollResponse) {
        SearchWithEmbeddingRequest searchReq = new SearchWithEmbeddingRequest();
        searchReq.setElastic_user(gesElasticUser);
        searchReq.setElastic_pwd(gesElasticPwd);
        searchReq.setIndex_name(gesIndexName);
        searchReq.setSize(searchSize);

        searchReq.setDam_kbt_id(enrollResponse.getId());
        return searchReq;
    }
}
