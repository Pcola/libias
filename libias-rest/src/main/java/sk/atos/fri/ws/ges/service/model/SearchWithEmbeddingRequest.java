package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class SearchWithEmbeddingRequest extends SearchBaseRequest {
    private List<Double> embedding;

    public SearchWithEmbeddingRequest() {
    }

    public List<Double> getEmbedding() {
        return embedding;
    }

    public void setEmbedding(List<Double> embedding) {
        this.embedding = embedding;
    }
}