package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class PersonenGeloeschtResourceList extends ResourceSupport {

    private List<PersonenGeloeschtResource> resources = new ArrayList<>();

    @JsonInclude
    @JsonProperty("pglList")
    public List<PersonenGeloeschtResource> getResources() {
        return resources;
    }

    public void addResource(PersonenGeloeschtResource resource) {
        resources.add(resource);
    }
}
