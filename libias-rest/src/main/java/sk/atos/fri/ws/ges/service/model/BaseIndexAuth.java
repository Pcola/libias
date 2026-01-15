package sk.atos.fri.ws.ges.service.model;

public class BaseIndexAuth extends BaseAuth {
    private String index_name;

    public BaseIndexAuth() {
    }

    public BaseIndexAuth(String elastic_user,
                         String elastic_pwd,
                         String ref_id,
                         String index_name) {
        super(elastic_user, elastic_pwd, ref_id);
        this.index_name = index_name;
    }

    public String getIndex_name() {
        return index_name;
    }

    public void setIndex_name(String index_name) {
        this.index_name = index_name;
    }
}