package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

import java.util.ArrayList;
import java.util.List;

public class AktenGeloeschtResources extends ResourceSupport {

    private List<AktenGeloeschtResource> resources = new ArrayList<>();

    @JsonInclude
    @JsonProperty("list")
    public List<AktenGeloeschtResource> getResources() {
        return resources;
    }

    public void addResource(AktenGeloeschtResource resource) {
        resources.add(resource);
    }

    /*
    private AktenGeloeschtResourceList embedded;

    @JsonProperty("_embedded")
    public AktenGeloeschtResourceList getEmbedded() {
        return embedded;
    }

    public void setEmbedded(AktenGeloeschtResourceList embedded) {
        this.embedded = embedded;
    }
    */
}
