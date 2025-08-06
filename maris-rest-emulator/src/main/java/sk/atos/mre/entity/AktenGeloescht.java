package sk.atos.mre.entity;

import javax.persistence.*;

@Entity
@Table(name = "AKTEN_GELOESCHT")
public class AktenGeloescht {

    @Id
    @Column(name = "AKTENZEICHEN", nullable = false)
    private String aktenzeichen;

    public String getAktenzeichen() {
        return aktenzeichen;
    }

    public void setAktenzeichen(String aktenzeichen) {
        this.aktenzeichen = aktenzeichen;
    }
}
