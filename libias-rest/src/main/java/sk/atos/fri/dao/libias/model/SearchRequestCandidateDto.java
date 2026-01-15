package sk.atos.fri.dao.libias.model;

public class SearchRequestCandidateDto {

    private Long bildOid;
    private Integer rank;
    private Double score;

    public Long getBildOid() {
        return bildOid;
    }

    public void setBildOid(Long bildOid) {
        this.bildOid = bildOid;
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