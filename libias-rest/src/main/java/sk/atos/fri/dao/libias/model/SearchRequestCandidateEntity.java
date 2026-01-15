package sk.atos.fri.dao.libias.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "SEARCH_REQUEST_CANDIDATE")
public class SearchRequestCandidateEntity {


    @Id
    @Column(name = "REQUEST_ID", nullable = false, precision = 10, scale = 0)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", insertable = false, updatable = false)
    private SearchRequestEntity searchRequest;

    @Column(name = "RANK", nullable = false, precision = 3, scale = 0)
    private Integer rank;

    @Column(name = "SCORE")
    private Double score;

    @Column(name = "BILD_OID", precision = 10, scale = 0)
    private Long bildOid;

    @Column(name = "ANTRAGSTELLER_OID", precision = 10, scale = 0)
    private Long antragstellerOid;

    @Column(name = "PKZ", precision = 10, scale = 0)
    private Long pkz;

    @Column(name = "AKTENZEICHEN", length = 32)
    private String aktenzeichen;

    @Column(name = "AZRNUMMER", length = 12)
    private String azrNummer;

    @Column(name = "DNUMMER", length = 128)
    private String dNummer;

    @Column(name = "ENUMMER", length = 128)
    private String eNummer;

    @Column(name = "EURODACNR", length = 128)
    private String eurodacNr;

    @Column(name = "FAMILIENNAME", length = 64)
    private String familienname;

    @Column(name = "VORNAME", length = 64)
    private String vorname;

    @Temporal(TemporalType.DATE)
    @Column(name = "GEBURTSDATUM")
    private Date geburtsdatum;

    @Column(name = "GEBURTSORT", length = 128)
    private String geburtsort;

    @Column(name = "GEBURTSLAND", length = 128)
    private String geburtsland;

    @Column(name = "HERKUNFTSLAND", length = 128)
    private String herkunftsland;

    @Temporal(TemporalType.DATE)
    @Column(name = "ANTRAGSDATUM")
    private Date antragsdatum;

    @Column(name = "ANTRAGSTYP", length = 64)
    private String antragstyp;

    @Column(name = "AUSSENSTELLE", length = 64)
    private String aussenstelle;

    @Column(name = "GESCHLECHT", length = 1)
    private String geschlecht;

    @Column(name = "STAATSANGEHOERIGKEIT", length = 128)
    private String staatsangehoerigkeit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIFIED")
    private Date dateModified;

    @Column(name = "TRANSFORMATION", length = 1000)
    private String transformation;

    public String getAktenzeichen() {
        return aktenzeichen;
    }

    public void setAktenzeichen(String aktenzeichen) {
        this.aktenzeichen = aktenzeichen;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public SearchRequestEntity getSearchRequest() { return searchRequest; }

    public void setSearchRequest(SearchRequestEntity searchRequest) { this.searchRequest = searchRequest; }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Long getBildOid() {
        return bildOid;
    }

    public void setBildOid(Long bildOid) {
        this.bildOid = bildOid;
    }

    public Long getAntragstellerOid() {
        return antragstellerOid;
    }

    public void setAntragstellerOid(Long antragstellerOid) {
        this.antragstellerOid = antragstellerOid;
    }

    public Long getPkz() {
        return pkz;
    }

    public void setPkz(Long pkz) {
        this.pkz = pkz;
    }

    public String getAzrNummer() {
        return azrNummer;
    }

    public void setAzrNummer(String azrNummer) {
        this.azrNummer = azrNummer;
    }

    public String getdNummer() {
        return dNummer;
    }

    public void setdNummer(String dNummer) {
        this.dNummer = dNummer;
    }

    public String geteNummer() {
        return eNummer;
    }

    public void seteNummer(String eNummer) {
        this.eNummer = eNummer;
    }

    public String getEurodacNr() {
        return eurodacNr;
    }

    public void setEurodacNr(String eurodacNr) {
        this.eurodacNr = eurodacNr;
    }

    public String getFamilienname() {
        return familienname;
    }

    public void setFamilienname(String familienname) {
        this.familienname = familienname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public Date getGeburtsdatum() {
        return geburtsdatum;
    }

    public void setGeburtsdatum(Date geburtsdatum) {
        this.geburtsdatum = geburtsdatum;
    }

    public String getGeburtsort() {
        return geburtsort;
    }

    public void setGeburtsort(String geburtsort) {
        this.geburtsort = geburtsort;
    }

    public String getGeburtsland() {
        return geburtsland;
    }

    public void setGeburtsland(String geburtsland) {
        this.geburtsland = geburtsland;
    }

    public String getHerkunftsland() {
        return herkunftsland;
    }

    public void setHerkunftsland(String herkunftsland) {
        this.herkunftsland = herkunftsland;
    }

    public Date getAntragsdatum() {
        return antragsdatum;
    }

    public void setAntragsdatum(Date antragsdatum) {
        this.antragsdatum = antragsdatum;
    }

    public String getAntragstyp() {
        return antragstyp;
    }

    public void setAntragstyp(String antragstyp) {
        this.antragstyp = antragstyp;
    }

    public String getAussenstelle() {
        return aussenstelle;
    }

    public void setAussenstelle(String aussenstelle) {
        this.aussenstelle = aussenstelle;
    }

    public String getGeschlecht() {
        return geschlecht;
    }

    public void setGeschlecht(String geschlecht) {
        this.geschlecht = geschlecht;
    }

    public String getStaatsangehoerigkeit() {
        return staatsangehoerigkeit;
    }

    public void setStaatsangehoerigkeit(String staatsangehoerigkeit) {
        this.staatsangehoerigkeit = staatsangehoerigkeit;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }
}