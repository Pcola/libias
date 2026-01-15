package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class CompareRequest {
    private List<Double> embedding_0;
    private List<Double> embedding_1;
    private String ref_id;

    public CompareRequest() {
    }

    public List<Double> getEmbedding_0() {
        return embedding_0;
    }

    public void setEmbedding_0(List<Double> embedding_0) {
        this.embedding_0 = embedding_0;
    }

    public List<Double> getEmbedding_1() {
        return embedding_1;
    }

    public void setEmbedding_1(List<Double> embedding_1) {
        this.embedding_1 = embedding_1;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }
}