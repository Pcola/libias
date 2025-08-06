package sk.atos.mre.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AktenrefResources extends ResourceSupport{

	private List<AktenrefResource> resources = new ArrayList<>();
	
	@JsonInclude
    //@JsonProperty("_embedded")
    @JsonProperty("list")
	public List<AktenrefResource> getEmbeddedResources() {
        return resources;
    }

    public void embedResource(AktenrefResource resource) {
        resources.add(resource);
    }	
}
