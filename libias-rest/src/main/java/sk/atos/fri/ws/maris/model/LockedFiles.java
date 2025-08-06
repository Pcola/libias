package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LockedFiles {

    @JsonProperty("list")
    List<LockedFile> lockedFiles;

    public List<LockedFile> getLockedFiles() {
        return lockedFiles;
    }

    public void setLockedFiles(List<LockedFile> lockedFiles) {
        this.lockedFiles = lockedFiles;
    }

    /*
    @JsonProperty("_embedded")
    LockedFileList lockedFileList;

    public LockedFileList getLockedFileList() {
        return lockedFileList;
    }

    public void setLockedFileList(LockedFileList lockedFileList) {
        this.lockedFileList = lockedFileList;
    }
    */
}
