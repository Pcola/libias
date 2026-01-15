package sk.atos.fri.ws.ges.service;

import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.ges.service.model.*;

import javax.annotation.PostConstruct;
import java.util.*;

public class GesClientDummy implements IGesClient {

    private static final Logger LOGGER = new Logger();
    private final Random random = new Random();

    private Map<String, double[]> embeddingCache = new HashMap<>();

    @PostConstruct
    public void init() {
        LOGGER.info("Dummy GES client (v2) in use - localhost profil");
    }

    @Override
    public AnalyseResponse analyseImage(AnalyseRequest req) {
        LOGGER.info("Dummy: Analysing image - GES v2");

        AnalyseResponse response = new AnalyseResponse();

        String imageHash = calculateImageHash(req.getImg());

        if (embeddingCache.containsKey(imageHash)) {
            LOGGER.info("Dummy: Using cached embedding for hash: " + imageHash);
            response.setEmbedding(embeddingCache.get(imageHash));
        } else {
            response.setEmbedding(new double[1024]);
            for (int i = 0; i < 1024; i++) {
                response.getEmbedding()[i] = (Math.random() - 0.5) * 2.0;
            }
            embeddingCache.put(imageHash, response.getEmbedding());
            LOGGER.info("Dummy: Cached new embedding for hash: " + imageHash + ", cache size: " + embeddingCache.size());
        }

        response.setLandmarks(new ArrayList<>());
        response.getLandmarks().add(110.0 + (Math.random() * 20.0));
        response.getLandmarks().add(170.0 + (Math.random() * 20.0));
        response.getLandmarks().add(160.0 + (Math.random() * 20.0));
        response.getLandmarks().add(170.0 + (Math.random() * 20.0));
        response.getLandmarks().add(135.0 + (Math.random() * 10.0));
        response.getLandmarks().add(195.0 + (Math.random() * 10.0));
        response.getLandmarks().add(115.0 + (Math.random() * 15.0));
        response.getLandmarks().add(230.0 + (Math.random() * 15.0));
        response.getLandmarks().add(155.0 + (Math.random() * 15.0));
        response.getLandmarks().add(230.0 + (Math.random() * 15.0));

        response.setBbox(new ArrayList<>());
        response.getBbox().add(80.0);
        response.getBbox().add(140.0);
        response.getBbox().add(280.0);
        response.getBbox().add(340.0);
        response.getBbox().add(0.04);

        response.setQuality(0.73 + (Math.random() * 0.05));
        response.setRoll(Math.random() * 360.0);

        return response;
    }

    @Override
    public CompareResponse compare(CompareRequest req) {
        LOGGER.info("Dummy: Comparing embeddings - GES v2");

        CompareResponse response = new CompareResponse();

        if (req.getEmbedding_0() != null && req.getEmbedding_1() != null &&
                req.getEmbedding_0().size() > 0 && req.getEmbedding_1().size() > 0) {

            double distance = 0;
            int minLen = Math.min(req.getEmbedding_0().size(), req.getEmbedding_1().size());
            for (int i = 0; i < minLen; i++) {
                double diff = req.getEmbedding_0().get(i) - req.getEmbedding_1().get(i);
                distance += diff * diff;
            }
            distance = Math.sqrt(distance);

            double rawScore = 1.0 / (1.0 + distance);

            double gesScore = applyUmrechnungsformel(rawScore);

            response.setDistance_l2(distance);
            response.setScore(gesScore);
            response.setAutomation_advice((gesScore > 50) ? 1.0 : 0.0);

            LOGGER.info("Dummy Compare:");
            LOGGER.info("  - embedding_0 length: " + req.getEmbedding_0().size());
            LOGGER.info("  - embedding_1 length: " + req.getEmbedding_1().size());
            LOGGER.info("  - distance (L2): " + String.format("%.4f", distance));
            LOGGER.info("  - rawScore: " + String.format("%.4f", rawScore));
            LOGGER.info("  - gesScore (after formula): " + gesScore);
//            LOGGER.info("  - automation_advice: " + response.automation_advice);
        } else {
            response.setDistance_l2(0.0);
            response.setScore(0.0);
            response.setAutomation_advice(0.0);
            LOGGER.error(Error.valueOf("Dummy Compare: embeddingy sú NULL alebo prázdne!"));
        }

        return response;
    }

    private double applyUmrechnungsformel(double rawScore) {
        double result = Math.random() * 100;
        return Math.round(result * 100.0) / 100.0;
    }

    private String calculateImageHash(byte[] img) {
        if (img == null || img.length == 0) return "null";

        long hash = 0;
        int len = Math.min(img.length, 10240);
        for (int i = 0; i < len; i++) {
            hash = hash * 31 + img[i];
        }
        return String.valueOf(hash);
    }

    @Override
    public EnrollResponse enroll(EnrollRequest req) {
        EnrollResponse response = new EnrollResponse();
        response.analysis_result = "embedding created";
        response.id = System.currentTimeMillis();
        return response;
    }

    @Override
    public Map<String, Object> deleteDocument(DeleteRequest req) {
        return new HashMap<>();
    }

    @Override
    public SearchResponse searchWithImage(SearchWithImageRequest req) {
        return new SearchResponse();
    }

    @Override
    public SearchResponse searchWithEmbedding(SearchWithEmbeddingRequest req) {
        LOGGER.info("Dummy: Searching with embedding");

        SearchResponse response = new SearchResponse();

        if (req.getEmbedding() != null && !req.getEmbedding().isEmpty()) {
            long[] fakeIds = {971622L, 971521L, 971606L, 971495L, 971559L};
            double[] fakeScores = {31.0, 29.88, 28.72, 28.31, 27.89};

            int randomIndex = random.nextInt(fakeIds.length);

            ReturnSearchHit hit = new ReturnSearchHit();
            hit.setId(fakeIds[randomIndex]);
            hit.setRank(1);
            hit.setScore(fakeScores[randomIndex]);
            hit.setQuality(0.75 + (Math.random() * 0.05));
            hit.setAutomation_advice(hit.getScore() > 50 ? 1 : 0);

            hit.setLandmarks(new ArrayList<>());
            hit.getLandmarks().add(382.0 + (Math.random() * 20.0));
            hit.getLandmarks().add(478.0 + (Math.random() * 20.0));

            response.hits.add(hit);
            LOGGER.info("Dummy: SearchWithEmbedding returning match with ID: " + hit.getId() + ", Score: " + hit.getScore());
        }

        return response;
    }

    @Override
    public List<ReturnElasticsearch> getFailureCases(BaseIndexAuth req) {
        return new ArrayList<>();
    }

    @Override
    public List<ReturnElasticsearch> getAllWithAutomaticDecision(BaseIndexAuth req) {
        return new ArrayList<>();
    }

    @Override
    public List<ReturnElasticsearch> getAllWithManualDecision(BaseIndexAuth req) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> countOfDocs(BaseIndexAuth req) {
        return new HashMap<>();
    }
}