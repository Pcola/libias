package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeletedFiles {

    @JsonProperty("list")
    List<DeletedFile> deletedFiles;

    public List<DeletedFile> getDeletedFiles() {
        return deletedFiles;
    }

    public void setDeletedFiles(List<DeletedFile> deletedFiles) {
        this.deletedFiles = deletedFiles;
    }

    /*
    @JsonProperty("_embedded")
    DeletedFileList deletedFileList;

    public DeletedFileList getDeletedFileList() {
        return deletedFileList;
    }

    public void setDeletedFileList(DeletedFileList deletedFileList) {
        this.deletedFileList = deletedFileList;
    }
    */
}
