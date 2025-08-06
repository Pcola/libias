package sk.atos.fri.ws.maris.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LockedFileList {

    @JsonProperty("galList")
    List<LockedFile> lockedFiles;

    public List<LockedFile> getLockedFiles() {
        return lockedFiles;
    }

    public void setLockedFiles(List<LockedFile> lockedFiles) {
        this.lockedFiles = lockedFiles;
    }
}
