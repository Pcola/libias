package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class AktenGesperrtResources extends ResourceSupport {

    private List<AktenGesperrtResource> resources = new ArrayList<>();

    @JsonInclude
    @JsonProperty("list")
    public List<AktenGesperrtResource> getResources() {
        return resources;
    }

    public void addResource(AktenGesperrtResource resource) {
        resources.add(resource);
    }

    /*
    private AktenGesperrtResourceList embedded;

    @JsonProperty("_embedded")
    public AktenGesperrtResourceList getEmbedded() {
        return embedded;
    }

    public void setEmbedded(AktenGesperrtResourceList embedded) {
        this.embedded = embedded;
    }
    */
}
