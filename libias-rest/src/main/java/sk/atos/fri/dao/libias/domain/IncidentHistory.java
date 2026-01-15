package sk.atos.fri.dao.libias.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


public class IncidentHistory {

    private Long historyId;
    private Long caseId;
    private String changedBy;
    private Date changedOn;
    private String type;

    public IncidentHistory() {
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss", timezone = "Europe/Berlin")
    public Date getChangedOn() {
        return changedOn;
    }

    public void setChangedOn(Date changedOn) {
        this.changedOn = changedOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
