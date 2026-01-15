package sk.atos.fri.ws.ges.service.model;

class BaseAuth {
    private String elastic_user;
    private String elastic_pwd;
    private String ref_id;

    public BaseAuth() {
    }

    public BaseAuth(String elastic_user, String elastic_pwd, String ref_id) {
        this.elastic_user = elastic_user;
        this.elastic_pwd = elastic_pwd;
        this.ref_id = ref_id;
    }

    public String getElastic_user() {
        return elastic_user;
    }

    public void setElastic_user(String elastic_user) {
        this.elastic_user = elastic_user;
    }

    public String getElastic_pwd() {
        return elastic_pwd;
    }

    public void setElastic_pwd(String elastic_pwd) {
        this.elastic_pwd = elastic_pwd;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }
}