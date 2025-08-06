package sk.atos.mre.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.hateoas.ResourceSupport;

import javax.persistence.Column;

public class PersonenGeloeschtResource extends ResourceSupport {

    @JsonProperty("personennummer")
    private Long personennummer;

    @JsonProperty("aktenzeichen")
    private String aktenzeichen;

    @JsonProperty("azrNummer")
    private String azrnummer;

    public Long getPersonennummer() {
        return personennummer;
    }

    public void setPersonennummer(Long personennummer) {
        this.personennummer = personennummer;
    }

    public String getAktenzeichen() {
        return aktenzeichen;
    }

    public void setAktenzeichen(String aktenzeichen) {
        this.aktenzeichen = aktenzeichen;
    }

    public String getAzrnummer() {
        return azrnummer;
    }

    public void setAzrnummer(String azrnummer) {
        this.azrnummer = azrnummer;
    }
}
