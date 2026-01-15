package sk.atos.fri.ws.ges.service.model;

public abstract class SearchBaseRequest extends BaseIndexAuth {
    private Integer size = 50;
    private Long dam_kbt_id;
    private Boolean only_identical;
    private String bk_identifier;

    public SearchBaseRequest() {
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getDam_kbt_id() {
        return dam_kbt_id;
    }

    public void setDam_kbt_id(Long dam_kbt_id) {
        this.dam_kbt_id = dam_kbt_id;
    }

    public Boolean getOnly_identical() {
        return only_identical;
    }

    public void setOnly_identical(Boolean only_identical) {
        this.only_identical = only_identical;
    }

    public String getBk_identifier() {
        return bk_identifier;
    }

    public void setBk_identifier(String bk_identifier) {
        this.bk_identifier = bk_identifier;
    }
}