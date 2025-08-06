package sk.atos.mre.entity;

import javax.persistence.*;

@Entity
@Table(name = "PERSONEN_GELOESCHT")
public class PersonenGeloescht {

    @Id
    @Column(name = "PERSONENNUMMER", nullable = false)
    private Long personennummer;

    @Column(name = "AKTENZEICHEN")
    private String aktenzeichen;

    @Column(name = "AZRNUMMER")
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
