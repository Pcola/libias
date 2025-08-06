package sk.atos.mre.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="ANTRAGSTELLER")
public class Antragsteller {
	
	@Id
	@NotNull
	@Column(name="ANTRAGSTELLER_OID")
	@SequenceGenerator(name = "ANTRAGSTELLER_SEQ", sequenceName = "ANTRAGSTELLER_SEQ", allocationSize = 1, initialValue = 1)
	private Long antragstellerOid;
	
	@Column(name="PKZ")
	private Long pkz;
	
	@Column(name="AKTENZEICHEN")
	private String aktenzeichen;
		
	@Column(name="AZRNUMMER")
	private String azrnummer;
	
	@Column(name="DNUMMER")
	private String dnummer;
	
	@Column(name="ENUMMER")
	private String enummer;
	
	@Column(name="EURODACNR")
	private String eurodacnr;
	
	@Column(name="FAMILIENNAME")
	private String familienname;
	
	@Column(name="VORNAME")
	private String vorname;
	
	@Column(name="GEBURTSDATUM")
	private Date geburtsdatum;
	
	@Column(name="GEBURTSLAND")
	private String geburtsland;
	
	@Column(name="GEBURTSORT")
	private String geburtsort;
	
	@Column(name="HERKUNFTSLAND")
	private String herkunftsland;
	
	@Column(name="ANTRAGSDATUM")
	private Date antragsdatum;
		
	@Column(name="ANTRAGSTYP")
	private String antragstyp;
	
	@Column(name="AUSSENSTELLE")
	private String aussenstelle;
	
	@Column(name="GESCHLECHT")
	private String geschlecht;
	
	@Column(name="STAATSANGEHOERIGKEIT")
	private String staatsangehoerigkeit;
	
	@Column(name="DATE_MODIFIED")
	private Date dateModified;
	
	@Column(name="CHANGED")
	private Short changed;

	@Column(name="AKTE_GESPERRT")
	private String akteGesperrt;

	public Antragsteller() {
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

	public String getDnummer() {
		return dnummer;
	}

	public void setDnummer(String dnummer) {
		this.dnummer = dnummer;
	}

	public String getEnummer() {
		return enummer;
	}

	public void setEnummer(String enummer) {
		this.enummer = enummer;
	}

	public String getEurodacnr() {
		return eurodacnr;
	}

	public void setEurodacnr(String eurodacnr) {
		this.eurodacnr = eurodacnr;
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

	public String getGeburtsland() {
		return geburtsland;
	}

	public void setGeburtsland(String geburtsland) {
		this.geburtsland = geburtsland;
	}

	public String getGeburtsort() {
		return geburtsort;
	}

	public void setGeburtsort(String geburtsort) {
		this.geburtsort = geburtsort;
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

	public String getAntragsTyp() {
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
	
	public Short getChanged() {
		return changed;
	}

	public void setChanged(Short changed) {
		this.changed = changed;
	}	

	public String getAkteGesperrt() {
		return akteGesperrt;
	}

	public void setAkteGesperrt(String akteGesperrt) {
		this.akteGesperrt = akteGesperrt;
	}

}
