package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedFileList {

    @JsonProperty("aglList")
    List<DeletedFile> deletedFiles;

    public List<DeletedFile> getDeletedFiles() {
        return deletedFiles;
    }

    public void setDeletedFiles(List<DeletedFile> deletedFiles) {
        this.deletedFiles = deletedFiles;
    }
}
