package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class ReturnSearchHit {
    private Integer automation_advice;
    private Long id;
    private List<Double> landmarks;
    private Double quality;
    private Integer rank;
    private Double score;

    public ReturnSearchHit() {
    }

    public Integer getAutomation_advice() {
        return automation_advice;
    }

    public void setAutomation_advice(Integer automation_advice) {
        this.automation_advice = automation_advice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Double> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(List<Double> landmarks) {
        this.landmarks = landmarks;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}