package sk.atos.fri.rest.model;

import java.util.List;

import sk.atos.fri.dao.libias.model.Incident;

public class IncidentResponse {
	
	List<Incident> incidents;
	
	Long totalCountOfIncidents;
	
	public IncidentResponse(List<Incident> incidents, Long totalCountOfIncidents) {
		this.incidents = incidents;
		this.totalCountOfIncidents = totalCountOfIncidents;
	}
	
	public List<Incident> getIncidents() {
		return incidents;
	}
	
	public void setIncidents(List<Incident> incidents) {
		this.incidents = incidents;
	}
	
	public Long getTotalCountOfIncidents() {
		return totalCountOfIncidents;
	}
	
	public void setTotalCountOfIncidents(Long totalCountOfIncidents) {
		this.totalCountOfIncidents = totalCountOfIncidents;
	}	
}
