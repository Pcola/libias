package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class AktenGeloeschtResourceList extends ResourceSupport {

    private List<AktenGeloeschtResource> resources = new ArrayList<>();

    @JsonInclude
    @JsonProperty("aglList")
    public List<AktenGeloeschtResource> getResources() {
        return resources;
    }

    public void addResource(AktenGeloeschtResource resource) {
        resources.add(resource);
    }
}
