package sk.atos.mre.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="AKTENREFERENZ")
public class Aktenreferenz {

	@Id
	@Column(name="AKTENREFERENZ_OID")
	private Long aktenreferenzOid;
	
	@Column(name="AKTENZEICHEN_A")
	private String aktenzeicherA;
	
	@Column(name="AKTENZEICHEN_B")
	private String aktenzeichenB;
	
	@Column(name="REFERENZBEZEICHNUNG")
	private String referenzbezeichnung;
	
	@Column(name="DATE_MODIFIED")
	private Date dateModified;

	public Long getAktenreferenzOid() {
		return aktenreferenzOid;
	}
		
	public void setAktenreferenzOid(Long aktenreferenz_oid) {
		this.aktenreferenzOid = aktenreferenz_oid;
	}

	public String getAktenzeichenA() {
		return aktenzeicherA;
	}

	public void setAktenzeichenA(String aktenzeichen_a) {
		this.aktenzeicherA = aktenzeichen_a;
	}

	public String getAktenzeichenB() {
		return aktenzeichenB;
	}

	public void setAktenzeichenB(String aktenzeichen_b) {
		this.aktenzeichenB = aktenzeichen_b;
	}

	public String getReferenzbezeichnung() {
		return referenzbezeichnung;
	}

	public void setReferenzbezeichnung(String referenzbezeichnung) {
		this.referenzbezeichnung = referenzbezeichnung;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date date_modified) {
		this.dateModified = date_modified;
	}
}
