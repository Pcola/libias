package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedPersons {

    @JsonProperty("list")
    List<DeletedPerson> deletedPersons;

    public List<DeletedPerson> getDeletedPersons() {
        return deletedPersons;
    }

    public void setDeletedPersons(List<DeletedPerson> deletedPersons) {
        this.deletedPersons = deletedPersons;
    }

    /*
    @JsonProperty("_embedded")
    DeletedPersonList deletedPersonList;

    public DeletedPersonList getDeletedPersonList() {
        return deletedPersonList;
    }

    public void setDeletedPersonList(DeletedPersonList deletedPersonList) {
        this.deletedPersonList = deletedPersonList;
    }
    */
}
