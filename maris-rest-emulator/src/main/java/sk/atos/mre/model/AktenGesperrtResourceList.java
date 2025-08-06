package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class AktenGesperrtResourceList extends ResourceSupport {

    private List<AktenGesperrtResource> resources = new ArrayList<>();

    @JsonInclude
    @JsonProperty("galList")
    public List<AktenGesperrtResource> getResources() {
        return resources;
    }

    public void addResource(AktenGesperrtResource resource) {
        resources.add(resource);
    }
}
