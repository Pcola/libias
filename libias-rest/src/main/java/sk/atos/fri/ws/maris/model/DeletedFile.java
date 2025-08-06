package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedFile {

    private String fileNumber;

    @JsonProperty("fileNumber")
    public String getFileNumber() {
        return fileNumber;
    }

    @JsonProperty("aktenzeichen")
    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    @Override
    public String toString() {
        return "sk.atos.fri.ws.maris.model.DeletedFile[ " +
                "fileNumber=" + fileNumber + " ]";
    }
}
