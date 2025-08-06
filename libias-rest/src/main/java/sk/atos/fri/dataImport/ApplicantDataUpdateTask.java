package sk.atos.fri.dataImport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sk.atos.fri.common.Constants;
//import sk.atos.fri.dao.IncidentStatus;
import sk.atos.fri.dao.libias.model.Incident;
import sk.atos.fri.dao.libias.model.IncidentFilter;
import sk.atos.fri.dao.libias.service.IncidentService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.ws.maris.model.AkteResponse;
import sk.atos.fri.ws.maris.model.PersonResponse;
import sk.atos.fri.ws.maris.model.Record;
import sk.atos.fri.ws.maris.model.UpdatedApplicant;
import sk.atos.fri.ws.maris.service.MarisWSClient;

public class ApplicantDataUpdateTask implements Callable<Long> {

	private final Logger LOG = new Logger();
	private final MarisWSClient marisClient;
	private final IncidentService incidentService;
	private final UpdatedApplicant updatedApplicant;
	private final Date updDate;

	public ApplicantDataUpdateTask(UpdatedApplicant ua, Date d, MarisWSClient mc, IncidentService is) {
		marisClient = mc;
		incidentService = is;
		updatedApplicant = ua;
		updDate = d;
	}

	@Override
	public Long call() throws Exception {
		Long antragStellerOid = updatedApplicant.getApplicantOid();
		List<Incident> incidents = Collections.emptyList();

		try {
			IncidentFilter f = new IncidentFilter();
			f.setFilter(0);
			// f.setStatus(IncidentStatus.Open);
			f.setAntragstellerOid(antragStellerOid);
			incidents = incidentService.getIncident(f);
		}
		catch (Exception ex) {
			throw new Exception(Error.LIST_INCIDENTS.getDescription() + " for AntragstellerOid: " + antragStellerOid, ex);
		}

		List<Long> oids = Stream.concat(
				incidents.stream()
				.filter(incident -> antragStellerOid.equals(incident.getaApplicantOid()))
				.map(Incident::getProbeId)
				,
				incidents.stream()
				.filter(incident -> antragStellerOid.equals(incident.getbApplicantOid()))
				.map(Incident::getGalleryId)).distinct().collect(Collectors.toList());

		for (int oidIndex = 0; oidIndex < oids.size(); oidIndex++) {
			Long imageOid = oids.get(oidIndex);
			PersonResponse person = marisClient.getPerson(imageOid);
			if (person == null) {
				//throw new Exception(Error.MARIS_FETCH.getDescription() + " for ImageOid: " + imageOid);
				continue;
			}
			boolean fileLocked = person.handleFileLocked();

			for (int incidentIndex = 0; incidentIndex < incidents.size(); incidentIndex++) {
				Incident inc = incidents.get(incidentIndex);
				LOG.debug(null, "Updating incident " + inc.getCaseId() + ", person A = " + inc.getaApplicantOid() + ", person B = " + inc.getbApplicantOid());
				try {
					boolean updating = false;
					if (antragStellerOid.equals(inc.getaApplicantOid()) && imageOid.equals(inc.getProbeId())) {
						updating = true;
						inc = incidentService.findByCaseId(inc.getCaseId());
						inc.setaApplicantDate(person.getApplicantDate());
						inc.setaApplicantOid(person.getApplicantOid());
						inc.setaApplicantType(person.getApplicantType());
						inc.setaAzrNumber(person.getAzrNumber());
						inc.setaBirthCountry(person.getBirthCountry());
						inc.setaBirthDate(person.getBirthDate());
						inc.setaBirthPlace(person.getBirthPlace());
						inc.setaDateModified(person.getDateModified());
						inc.setaDNumber(person.getdNumber());
						inc.setaENumber(person.geteNumber());
						inc.setaEuroDacNumber(person.getEuroDacNumber());
						inc.setaFileNumber(person.getFileNumber());
						inc.setaFirstName(person.getFirstName());
						inc.setaGender(person.getGender());
						inc.setaLastName(person.getLastName());
						inc.setaNationality(person.getNationality());
						inc.setaOriginCountry(person.getOriginCountry());
						inc.setaPkz(person.getPkz());
						inc.setaWorkplace(person.getWorkplace());
						if (fileLocked && inc.getaAkteLocked() == null) {
							inc.setaAkteLocked(updDate);
						} else if (!fileLocked) {
							inc.setaAkteLocked(null);
						}
						LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side A filled with person " + person.getApplicantOid());
					}
					if (antragStellerOid.equals(inc.getbApplicantOid()) && imageOid.equals(inc.getGalleryId())) {
						updating = true;
						inc = incidentService.findByCaseId(inc.getCaseId());
						inc.setbApplicantDate(person.getApplicantDate());
						inc.setbApplicantOid(person.getApplicantOid());
						inc.setbApplicantType(person.getApplicantType());
						inc.setbAzrNumber(person.getAzrNumber());
						inc.setbBirthCountry(person.getBirthCountry());
						inc.setbBirthDate(person.getBirthDate());
						inc.setbBirthPlace(person.getBirthPlace());
						inc.setbDateModified(person.getDateModified());
						inc.setbDNumber(person.getdNumber());
						inc.setbENumber(person.geteNumber());
						inc.setbEuroDacNumber(person.getEuroDacNumber());
						inc.setbFileNumber(person.getFileNumber());
						inc.setbFirstName(person.getFirstName());
						inc.setbGender(person.getGender());
						inc.setbLastName(person.getLastName());
						inc.setbNationality(person.getNationality());
						inc.setbOriginCountry(person.getOriginCountry());
						inc.setbPkz(person.getPkz());
						inc.setbWorkplace(person.getWorkplace());
						if (fileLocked && inc.getbAkteLocked() == null) {
							inc.setbAkteLocked(updDate);
						} else if (!fileLocked) {
							inc.setbAkteLocked(null);
						}
						LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side B filled with person " + person.getApplicantOid());
					}
					if (updating && inc.getaFileNumber() != null && inc.getbFileNumber() != null) {
						LOG.debug(null, "Updating file reference for incident " + inc.getCaseId() + ", file number A = " + inc.getaFileNumber() + ", file number B = " + inc.getbFileNumber());
						inc.setFileReference(null);
						inc.setReferenceType(null);
						inc = updateFileReference(inc);
					}
					if (updating) {
						incidentService.updateIncident(inc);
					}
				}
				catch (Exception ex) {
					//throw new Exception(Error.UPDATE_INCIDENT.getDescription() + " for AntragstellerOid: " + antragStellerOid, ex);
					LOG.error(Error.UPDATE_INCIDENT, ex);
				}
			}
		}
		return antragStellerOid;
	}

	private Incident updateFileReference(Incident incident) throws Exception {
		try {
			List<Record> references = new ArrayList<Record>();

			AkteResponse respAB = marisClient.getAkte(incident.getaFileNumber(), incident.getbFileNumber());
			//AkteResponse respBA = marisClient.getAkte(incident.getbFileNumber(), incident.getaFileNumber());

			if (respAB != null) {
				references.addAll(respAB.getRecords());
			}

			/*
			if (respBA != null) {
				references.addAll(respBA.getRecords());
			}
			*/

			if (references.size() > 0) {
				Record mostImportantReference = references.get(0);
				for (int c = 0; c < references.size(); c++) {
					Integer priority = Constants.REFERENCE_DESCRIPTION.get(references.get(c).getReferenceType());
					Integer currentLowest = Constants.REFERENCE_DESCRIPTION.get(mostImportantReference.getReferenceType());
					
					if (priority != null && (currentLowest == null || currentLowest > priority)) {
						mostImportantReference = references.get(c);
					}
				}
				incident.setFileReference(mostImportantReference.getFileReference());
				incident.setReferenceType(mostImportantReference.getReferenceType());
				LOG.debug(null, "Updating file reference for incident " + incident.getCaseId() + ", reference set to " + incident.getFileReference());
				return incident;
			}
		}
		catch (Exception ex) {
			throw new Exception(Error.UPDATE_AKTENREFERENZRELATION.getDescription() + " for AktenzeichenA: " + incident.getaFileNumber() + ", AktenzeichenB: " + incident.getaFileNumber(), ex);
		}
		return incident;
	}

}
