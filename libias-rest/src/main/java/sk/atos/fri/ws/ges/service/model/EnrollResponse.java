package sk.atos.fri.ws.ges.service.model;

public class EnrollResponse {
    public String analysis_result;
    public long id;

    public EnrollResponse() {
    }

    public String getAnalysis_result() {
        return analysis_result;
    }

    public void setAnalysis_result(String analysis_result) {
        this.analysis_result = analysis_result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}