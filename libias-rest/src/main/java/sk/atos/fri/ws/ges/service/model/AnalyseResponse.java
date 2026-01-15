package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class AnalyseResponse {
    private double[] embedding;
    private List<Double> landmarks;
    private List<Double> bbox;
    private Double quality;
    private Double roll;

    public AnalyseResponse() {
    }

    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }

    public List<Double> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(List<Double> landmarks) {
        this.landmarks = landmarks;
    }

    public List<Double> getBbox() {
        return bbox;
    }

    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public Double getRoll() {
        return roll;
    }

    public void setRoll(Double roll) {
        this.roll = roll;
    }
}