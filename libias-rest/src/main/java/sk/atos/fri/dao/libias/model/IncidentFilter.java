package sk.atos.fri.dao.libias.model;

import sk.atos.fri.dao.IncidentStatus;

public class IncidentFilter {

	private String aktenzeichen;
	private Long antragstellerOid;
	private Long imageOid;
	private Integer filter;
	private IncidentStatus status;

	public String getAktenzeichen() {
		return aktenzeichen;
	}

	public void setAktenzeichen(String aktenzeichen) {
		this.aktenzeichen = aktenzeichen;
	}

	public Long getAntragstellerOid() {
		return antragstellerOid;
	}

	public void setAntragstellerOid(Long antragstellerOid) {
		this.antragstellerOid = antragstellerOid;
	}

	public Long getImageOid() {
		return imageOid;
	}

	public void setImageOid(Long imageOid) {
		this.imageOid = imageOid;
	}

	public void setFilter(Integer filter) {
		this.filter = filter;
	}

	public Integer getFilter() {
		return this.filter;
	}

	public IncidentStatus getStatus() {
		return status;
	}

	public void setStatus(IncidentStatus status) {
		this.status = status;
	}

}
