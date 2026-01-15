package sk.atos.fri.dao.libias.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name = "TRAFFIC_LIGHT")
public class TrafficLight {

    @Id
    @Column(name = "ROLE_ID", nullable = false)
    private Long roleId;

    @Column(name = "SCORE_FROM", columnDefinition = "BINARY_DOUBLE")
    private Double scoreFrom;

    @Column(name = "SCORE_TO", columnDefinition = "BINARY_DOUBLE")
    private Double scoreTo;

    @Size(max = 100)
    @Column(name = "COMMENT_RED", length = 100)
    private String commentRed;

    @Size(max = 100)
    @Column(name = "COMMENT_YELLOW", length = 100)
    private String commentYellow;

    @Size(max = 100)
    @Column(name = "COMMENT_GREEN", length = 100)
    private String commentGreen;


    public TrafficLight() {
    }

    public TrafficLight(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Double getScoreFrom() {
        return scoreFrom;
    }

    public void setScoreFrom(Double scoreFrom) {
        this.scoreFrom = scoreFrom;
    }

    public Double getScoreTo() {
        return scoreTo;
    }

    public void setScoreTo(Double scoreTo) {
        this.scoreTo = scoreTo;
    }

    public String getCommentRed() {
        return commentRed;
    }

    public void setCommentRed(String commentRed) {
        this.commentRed = commentRed;
    }

    public String getCommentYellow() {
        return commentYellow;
    }

    public void setCommentYellow(String commentYellow) {
        this.commentYellow = commentYellow;
    }

    public String getCommentGreen() {
        return commentGreen;
    }

    public void setCommentGreen(String commentGreen) {
        this.commentGreen = commentGreen;
    }
}