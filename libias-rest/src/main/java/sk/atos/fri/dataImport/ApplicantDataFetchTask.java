package sk.atos.fri.dataImport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
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

public class ApplicantDataFetchTask implements Callable<Long>{

	private final Logger LOG = new Logger();
	private final MarisWSClient marisClient;
	private final IncidentService incidentService;
	private final Long imageOid;
	private final Date impDate;

	public ApplicantDataFetchTask(Long imgOid, Date d, MarisWSClient mc, IncidentService is) {
		marisClient = mc;
		incidentService = is;
		imageOid = imgOid;
		impDate = d;
	}

	@Override
	public Long call() throws Exception {
		PersonResponse personData = marisClient.getPerson(imageOid);
		if (personData == null) {
			//throw new Exception(Error.MARIS_FETCH.getDescription() + " for ImageOid: " + imageOid);
			return imageOid;
		}
		boolean fileLocked = personData.handleFileLocked();

		List<Incident> incidents = Collections.emptyList();

		try {
			IncidentFilter f = new IncidentFilter();
			f.setFilter(0);
			//f.setStatus(IncidentStatus.Open);
			f.setImageOid(imageOid);
			incidents = incidentService.getIncident(f);
		}
		catch (Exception ex) {
			throw new Exception(Error.LIST_INCIDENTS.getDescription() + " for ImageOid: " + imageOid, ex);
		}

		for (int incidentIndex=0; incidentIndex < incidents.size(); incidentIndex++) {
			Incident inc = incidents.get(incidentIndex);
			if (inc.getaApplicantOid() != null && inc.getbApplicantOid() != null) {
				// nothing to update
				continue;
			}
			LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side A = " + inc.getaApplicantOid() + ", side B = " + inc.getbApplicantOid());
			try {
				boolean updating = false;
				if (inc.getaApplicantOid() == null && imageOid.equals(inc.getProbeId())) {
					updating = true;
					inc = incidentService.findByCaseId(inc.getCaseId());
					inc.setaApplicantDate(personData.getApplicantDate());
					inc.setaApplicantOid(personData.getApplicantOid());
					inc.setaApplicantType(personData.getApplicantType());
					inc.setaAzrNumber(personData.getAzrNumber());
					inc.setaBirthCountry(personData.getBirthCountry());
					inc.setaBirthDate(personData.getBirthDate());
					inc.setaBirthPlace(personData.getBirthPlace());
					inc.setaDateModified(personData.getDateModified());
					inc.setaDNumber(personData.getdNumber());
					inc.setaENumber(personData.geteNumber());
					inc.setaEuroDacNumber(personData.getEuroDacNumber());
					inc.setaFileNumber(personData.getFileNumber());
					inc.setaFirstName(personData.getFirstName());
					inc.setaGender(personData.getGender());
					inc.setaLastName(personData.getLastName());
					inc.setaNationality(personData.getNationality());
					inc.setaOriginCountry(personData.getOriginCountry());
					inc.setaPkz(personData.getPkz());
					inc.setaWorkplace(personData.getWorkplace());
					inc.setaAkteLocked(fileLocked ? impDate : null);
					LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side A filled with person " + personData.getApplicantOid());
				}
				if (inc.getbApplicantOid() == null && imageOid.equals(inc.getGalleryId())) {
					updating = true;
					inc = incidentService.findByCaseId(inc.getCaseId());
					inc.setbApplicantDate(personData.getApplicantDate());
					inc.setbApplicantOid(personData.getApplicantOid());
					inc.setbApplicantType(personData.getApplicantType());
					inc.setbAzrNumber(personData.getAzrNumber());
					inc.setbBirthCountry(personData.getBirthCountry());
					inc.setbBirthDate(personData.getBirthDate());
					inc.setbBirthPlace(personData.getBirthPlace());
					inc.setbDateModified(personData.getDateModified());
					inc.setbDNumber(personData.getdNumber());
					inc.setbENumber(personData.geteNumber());
					inc.setbEuroDacNumber(personData.getEuroDacNumber());
					inc.setbFileNumber(personData.getFileNumber());
					inc.setbFirstName(personData.getFirstName());
					inc.setbGender(personData.getGender());
					inc.setbLastName(personData.getLastName());
					inc.setbNationality(personData.getNationality());
					inc.setbOriginCountry(personData.getOriginCountry());
					inc.setbPkz(personData.getPkz());
					inc.setbWorkplace(personData.getWorkplace());
					inc.setbAkteLocked(fileLocked ? impDate : null);
					LOG.debug(null, "Updating incident " + inc.getCaseId() + ", side B filled with person " + personData.getApplicantOid());
				}
				if (updating && inc.getaFileNumber() != null && inc.getbFileNumber() != null) {
					LOG.debug(null, "Updating file reference for incident " + inc.getCaseId() + ", file number A = " + inc.getaFileNumber() + ", file number B = " + inc.getbFileNumber());
					inc = updateFileReference(inc);
				}
				if (updating) {
					incidentService.updateIncident(inc);
				}
			}
			catch (Exception ex) {
				//throw new Exception(Error.UPDATE_INCIDENT.getDescription() + " for ImageOid: " + imageOid, ex);
				LOG.error(Error.UPDATE_INCIDENT, ex);
			}
		}
		return imageOid;
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
