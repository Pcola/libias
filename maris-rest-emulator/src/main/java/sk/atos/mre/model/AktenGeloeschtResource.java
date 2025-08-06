package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

public class AktenGeloeschtResource extends ResourceSupport {

    @JsonProperty("aktenzeichen")
    private String aktenzeichen;

    public String getAktenzeichen() {
        return aktenzeichen;
    }

    public void setAktenzeichen(String aktenzeichen) {
        this.aktenzeichen = aktenzeichen;
    }
}
