package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedPerson {

    private Long personNumber;
    private String fileNumber;
    private String registrationNumber;

    @JsonProperty("personNumber")
    public Long getPersonNumber() {
        return personNumber;
    }

    @JsonProperty("personennummer")
    public void setPersonNumber(Long personNumber) {
        this.personNumber = personNumber;
    }

    @JsonProperty("fileNumber")
    public String getFileNumber() {
        return fileNumber;
    }

    @JsonProperty("aktenzeichen")
    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    @JsonProperty("registrationNumber")
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    @JsonProperty("azrNummer")
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    @Override
    public String toString() {
        return "sk.atos.fri.ws.maris.model.DeletedPerson[ " +
                "personNumber=" + personNumber +
                ", fileNumber=" + fileNumber +
                ", registrationNumber=" + registrationNumber + " ]";
    }
}
