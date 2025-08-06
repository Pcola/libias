package sk.atos.fri.dao.libias.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @author : A761498, Kamil Macek
 * @since : 8/1/2019
 **/

@Entity
@Table(name = "INCIDENT_HISTORY")
@XmlRootElement
@NamedQueries({
        @NamedQuery(name = "IncidentHistory.findAll", query = "SELECT i FROM IncidentHistory i")
})
public class IncidentHistory implements Serializable {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID", unique = true, nullable = false)
    private Long historyId;

    @NotNull
    @Column(name = "CASE_ID")
    private Long caseId;

    @Size(max = 100)
    @Column(name = "CHANGED_BY")
    private String changedBy;

    @Column(name = "CHANGED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date changedOn;

    @Column(name = "TYPE")
    @Size(max = 1)
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
