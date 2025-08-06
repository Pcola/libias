package sk.atos.mre.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UpdatedAntragstellerResources extends ResourceSupport{

	private List<UpdatedAntragstellerResource> resources = new ArrayList<>();
	
	@JsonInclude
    //@JsonProperty("_embedded")
    @JsonProperty("list")
	public List<UpdatedAntragstellerResource> getEmbeddedResources() {
        return resources;
    }

    public void embedResource(UpdatedAntragstellerResource resource) {
        resources.add(resource);
    }	
}
