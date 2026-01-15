package sk.atos.fri.ws.ges.service.model;

public class DeleteRequest extends BaseIndexAuth {
    private long id;

    public DeleteRequest() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}