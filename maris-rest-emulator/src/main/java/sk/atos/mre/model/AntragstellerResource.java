package sk.atos.mre.model;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AntragstellerResource extends ResourceSupport {
	
	private Long antragstellerOid;
	
	private Long pkz;
	
	private String aktenzeichen;
		
	private String azrNummer;
	
	private String dnummer;
	
	private String enummer;
	
	private String euroDacNr;
	
	private String familienname;
	
	private String vorname;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date geburtsdatum;
	
	private String geburtsland;
	
	private String geburtsort;
	
	private String herkunftsland;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date antragsDatum;
		
	private String antragsTyp;
	
	private String aussenstelle;
	
	private String geschlecht;
	
	private String staatsangehoerigkeit;
	
	private Short changed;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date dateModified;

	private String akteGesperrt;

	public Long getAntragstellerOid() {
		return antragstellerOid;
	}

	public void setAntragstellerOid(Long antragstelleOid) {
		this.antragstellerOid = antragstelleOid;
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

	public String getAzrNummer() {
		return azrNummer;
	}

	public void setAzrNummer(String azrNummer) {
		this.azrNummer = azrNummer;
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

	public String getEuroDacNr() {
		return euroDacNr;
	}

	public void setEuroDacNr(String euroDacNr) {
		this.euroDacNr = euroDacNr;
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

	public Date getAntragsDatum() {
		return antragsDatum;
	}

	public void setAntragsDatum(Date antragsDatum) {
		this.antragsDatum = antragsDatum;
	}

	public String getAntragsTyp() {
		return antragsTyp;
	}

	public void setAntragsTyp(String antragsTyp) {
		this.antragsTyp = antragsTyp;
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
