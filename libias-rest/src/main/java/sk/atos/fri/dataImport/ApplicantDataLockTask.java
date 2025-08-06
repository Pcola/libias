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
import sk.atos.fri.ws.maris.service.MarisWSClient;

public class ApplicantDataLockTask implements Callable<String> {

	private final Logger LOG = new Logger();
	private final MarisWSClient marisClient;
	private final IncidentService incidentService;
	private final String aktenzeichen;
	private final Date updDate;

	public ApplicantDataLockTask(String akz, Date d, MarisWSClient mc, IncidentService is) {
		marisClient = mc;
		incidentService = is;
		aktenzeichen = akz;
		updDate = d;
	}

	@Override
	public String call() throws Exception {
		List<Incident> incidents = Collections.emptyList();

		try {
			IncidentFilter f = new IncidentFilter();
			f.setFilter(0);
			// f.setStatus(IncidentStatus.Open);
			f.setAktenzeichen(aktenzeichen);
			incidents = incidentService.getIncident(f);
		}
		catch (Exception ex) {
			throw new Exception(Error.LIST_INCIDENTS.getDescription() + " for Aktenzeichen: " + aktenzeichen, ex);
		}

		List<Long> oids = Stream.concat(
				incidents.stream()
				.filter(incident -> aktenzeichen.equals(incident.getaFileNumber()))
				.map(Incident::getProbeId)
				,
				incidents.stream()
				.filter(incident -> aktenzeichen.equals(incident.getbFileNumber()))
				.map(Incident::getGalleryId)).distinct().collect(Collectors.toList());

		for (int oidIndex = 0; oidIndex < oids.size(); oidIndex++) {
			Long imageOid = oids.get(oidIndex);
			PersonResponse person = marisClient.getPerson(imageOid);
			if (person == null) {
				// handle as empty and locked
				person = new PersonResponse();
			}
			boolean fileLocked = person.handleFileLocked();

			for (int incidentIndex = 0; incidentIndex < incidents.size(); incidentIndex++) {
				Incident inc = incidents.get(incidentIndex);
				LOG.debug(null, "Updating incident " + inc.getCaseId() + ", file A = " + inc.getaFileNumber() + ", file B = " + inc.getbFileNumber());
				try {
					boolean updating = false;
					if (aktenzeichen.equals(inc.getaFileNumber()) && imageOid.equals(inc.getProbeId())) {
						updating = true;
						inc = incidentService.findByCaseId(inc.getCaseId());
						inc.setaApplicantDate(person.getApplicantDate());
						if (person.getApplicantOid() != null) inc.setaApplicantOid(person.getApplicantOid());
						inc.setaApplicantType(person.getApplicantType());
						inc.setaAzrNumber(person.getAzrNumber());
						inc.setaBirthCountry(person.getBirthCountry());
						inc.setaBirthDate(person.getBirthDate());
						inc.setaBirthPlace(person.getBirthPlace());
						if (person.getDateModified() != null) inc.setaDateModified(person.getDateModified());
						inc.setaDNumber(person.getdNumber());
						inc.setaENumber(person.geteNumber());
						inc.setaEuroDacNumber(person.getEuroDacNumber());
						if (person.getFileNumber() != null) inc.setaFileNumber(person.getFileNumber());
						inc.setaFirstName(person.getFirstName());
						inc.setaGender(person.getGender());
						inc.setaLastName(person.getLastName());
						inc.setaNationality(person.getNationality());
						inc.setaOriginCountry(person.getOriginCountry());
						if (person.getPkz() != null) inc.setaPkz(person.getPkz());
						inc.setaWorkplace(person.getWorkplace());
						if (fileLocked && inc.getaAkteLocked() == null) {
							inc.setaAkteLocked(updDate);
						} else if (!fileLocked) {
							inc.setaAkteLocked(null);
						}
						LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side A " + (fileLocked ? "locked" : "unlocked"));
					}
					if (aktenzeichen.equals(inc.getbFileNumber()) && imageOid.equals(inc.getGalleryId())) {
						updating = true;
						inc = incidentService.findByCaseId(inc.getCaseId());
						inc.setbApplicantDate(person.getApplicantDate());
						if (person.getApplicantOid() != null) inc.setbApplicantOid(person.getApplicantOid());
						inc.setbApplicantType(person.getApplicantType());
						inc.setbAzrNumber(person.getAzrNumber());
						inc.setbBirthCountry(person.getBirthCountry());
						inc.setbBirthDate(person.getBirthDate());
						inc.setbBirthPlace(person.getBirthPlace());
						if (person.getDateModified() != null) inc.setbDateModified(person.getDateModified());
						inc.setbDNumber(person.getdNumber());
						inc.setbENumber(person.geteNumber());
						inc.setbEuroDacNumber(person.getEuroDacNumber());
						if (person.getFileNumber() != null) inc.setbFileNumber(person.getFileNumber());
						inc.setbFirstName(person.getFirstName());
						inc.setbGender(person.getGender());
						inc.setbLastName(person.getLastName());
						inc.setbNationality(person.getNationality());
						inc.setbOriginCountry(person.getOriginCountry());
						if (person.getPkz() != null) inc.setbPkz(person.getPkz());
						inc.setbWorkplace(person.getWorkplace());
						if (fileLocked && inc.getbAkteLocked() == null) {
							inc.setbAkteLocked(updDate);
						} else if (!fileLocked) {
							inc.setbAkteLocked(null);
						}
						LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side B " + (fileLocked ? "locked" : "unlocked"));
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
					//throw new Exception(Error.UPDATE_INCIDENT.getDescription() + " for Aktenzeichen: " + aktenzeichen, ex);
					LOG.error(Error.UPDATE_INCIDENT, ex);
				}
			}
		}
		return aktenzeichen;
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
