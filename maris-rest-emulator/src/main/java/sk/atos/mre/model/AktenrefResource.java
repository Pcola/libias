package sk.atos.mre.model;

import java.util.Date;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AktenrefResource extends ResourceSupport{	
	private Long aktenreferenzOid;
	
	private String aktenzeichenA;
	
	private String aktenzeichenB;
	
	private String referenzBezeichnung;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private Date dateModified;

	public Long getAktenreferenzOid() {
		return aktenreferenzOid;
	}

	public void setAktenreferenzOid(Long aktenreferenzOid) {
		this.aktenreferenzOid = aktenreferenzOid;
	}

	public String getAktenzeichenA() {
		return aktenzeichenA;
	}

	public void setAktenzeichenA(String aktenzeichenA) {
		this.aktenzeichenA = aktenzeichenA;
	}

	public String getAktenzeichenB() {
		return aktenzeichenB;
	}

	public void setAktenzeichenB(String aktenzeichenB) {
		this.aktenzeichenB = aktenzeichenB;
	}

	public String getReferenzBezeichnung() {
		return referenzBezeichnung;
	}

	public void setReferenzBezeichnung(String referenzBezeichnung) {
		this.referenzBezeichnung = referenzBezeichnung;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}	
}
