package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedPersonList {

    @JsonProperty("pglList")
    List<DeletedPerson> deletedPersons;

    public List<DeletedPerson> getDeletedPersons() {
        return deletedPersons;
    }

    public void setDeletedPersons(List<DeletedPerson> deletedPersons) {
        this.deletedPersons = deletedPersons;
    }
}
