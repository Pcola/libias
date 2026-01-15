package sk.atos.fri.ws.ges.service.model;

import java.util.List;

public class ReturnElasticsearch {
    private Long dam_kbt_id;
    private Boolean do_not_enroll;
    private double[] embedding;
    private Long id;
    private String import_time;
    private List<Double> landmarks;
    private String manual_decision;
    private Boolean partition;
    private Double quality;
    private Double roll;

    public ReturnElasticsearch() {
    }

    public Long getDam_kbt_id() {
        return dam_kbt_id;
    }

    public void setDam_kbt_id(Long dam_kbt_id) {
        this.dam_kbt_id = dam_kbt_id;
    }

    public Boolean getDo_not_enroll() {
        return do_not_enroll;
    }

    public void setDo_not_enroll(Boolean do_not_enroll) {
        this.do_not_enroll = do_not_enroll;
    }

    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImport_time() {
        return import_time;
    }

    public void setImport_time(String import_time) {
        this.import_time = import_time;
    }

    public List<Double> getLandmarks() {
        return landmarks;
    }

    public void setLandmarks(List<Double> landmarks) {
        this.landmarks = landmarks;
    }

    public String getManual_decision() {
        return manual_decision;
    }

    public void setManual_decision(String manual_decision) {
        this.manual_decision = manual_decision;
    }

    public Boolean getPartition() {
        return partition;
    }

    public void setPartition(Boolean partition) {
        this.partition = partition;
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
