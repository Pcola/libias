package sk.atos.fri.ws.ges.service.model;

public class CompareResponse {
    private Double distance_l2;
    private String ref_id;
    private Double score;
    private Double automation_advice;

    public CompareResponse() {
    }

    public Double getDistance_l2() {
        return distance_l2;
    }

    public void setDistance_l2(Double distance_l2) {
        this.distance_l2 = distance_l2;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getAutomation_advice() {
        return automation_advice;
    }

    public void setAutomation_advice(Double automation_advice) {
        this.automation_advice = automation_advice;
    }
}