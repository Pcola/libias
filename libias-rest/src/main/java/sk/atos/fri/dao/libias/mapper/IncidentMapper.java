package sk.atos.fri.dao.libias.mapper;

import sk.atos.fri.dao.libias.domain.Incident;
import sk.atos.fri.dao.libias.domain.IncidentHistory;
import sk.atos.fri.dao.libias.model.IncidentCognitecEntity;
import sk.atos.fri.dao.libias.model.IncidentGesEntity;
import sk.atos.fri.dao.libias.model.IncidentHistoryCognitecEntity;
import sk.atos.fri.dao.libias.model.IncidentHistoryGesEntity;

import java.util.stream.Collectors;


public final class IncidentMapper {
    private IncidentMapper() {
    }

    public static Incident toDomain(IncidentCognitecEntity i) {
        if (i == null) return null;

        Incident d = new Incident();

        d.setCaseId(i.getCaseId());
        d.setProbeId(i.getProbeId());
        d.setGalleryId(i.getGalleryId());
        d.setScore(i.getScore());
        d.setRank(i.getRank());
        d.setJobid(i.getJobid());
        d.setFilter(i.getFilter());

        d.setPriorityId(i.getPriority().getPriorityId());
        d.setPriority(i.getPriority());
        d.setStatus(i.getStatus());
        d.setWorkplace(i.getWorkplace());

        d.setNote(i.getNote());
        d.setWorkplaceNote(i.getWorkplaceNote());
        d.setFileReference(i.getFileReference());
        d.setReferenceType(i.getReferenceType());
        d.setCreatedDate(i.getCreatedDate());

        d.setaApplicantOid(i.getaApplicantOid());
        d.setaPkz(i.getaPkz());
        d.setaFileNumber(i.getaFileNumber());
        d.setaAzrNumber(i.getaAzrNumber());
        d.setaDNumber(i.getaDNumber());
        d.setaENumber(i.getaENumber());
        d.setaEuroDacNumber(i.getaEuroDacNumber());
        d.setaLastName(i.getaLastName());
        d.setaFirstName(i.getaFirstName());
        d.setaBirthDate(i.getaBirthDate());
        d.setaGender(i.getaGender());
        d.setaNationality(i.getaNationality());
        d.setaOriginCountry(i.getaOriginCountry());
        d.setaBirthCountry(i.getaBirthCountry());
        d.setaBirthPlace(i.getaBirthPlace());
        d.setaApplicantType(i.getaApplicantType());
        d.setaWorkplace(i.getaWorkplace());
        d.setaApplicantDate(i.getaApplicantDate());
        d.setaDateModified(i.getaDateModified());

        d.setbApplicantOid(i.getbApplicantOid());
        d.setbPkz(i.getbPkz());
        d.setbFileNumber(i.getbFileNumber());
        d.setbAzrNumber(i.getbAzrNumber());
        d.setbDNumber(i.getbDNumber());
        d.setbENumber(i.getbENumber());
        d.setbEuroDacNumber(i.getbEuroDacNumber());
        d.setbLastName(i.getbLastName());
        d.setbFirstName(i.getbFirstName());
        d.setbBirthDate(i.getbBirthDate());
        d.setbGender(i.getbGender());
        d.setbNationality(i.getbNationality());
        d.setbOriginCountry(i.getbOriginCountry());
        d.setbBirthCountry(i.getbBirthCountry());
        d.setbBirthPlace(i.getbBirthPlace());
        d.setbApplicantType(i.getbApplicantType());
        d.setbWorkplace(i.getbWorkplace());
        d.setbApplicantDate(i.getbApplicantDate());
        d.setbDateModified(i.getbDateModified());

        d.setRowid(i.getRowid());
        d.setBemLastChangedBy(i.getBemLastChangedBy());
        d.setBemLastChangedOn(i.getBemLastChangedOn());
        d.setAusLastChangedBy(i.getAusLastChangedBy());
        d.setAusLastChangedOn(i.getAusLastChangedOn());

        d.setaPersonDeleted(i.getaPersonDeleted());
        d.setbPersonDeleted(i.getbPersonDeleted());
        d.setaAkteDeleted(i.getaAkteDeleted());
        d.setbAkteDeleted(i.getbAkteDeleted());
        d.setaAkteLocked(i.getaAkteLocked());
        d.setbAkteLocked(i.getbAkteLocked());

        if (i.getIncidentHistory() != null) {
            d.setIncidentHistory(
                    i.getIncidentHistory().stream()
                            .map(IncidentMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }

        return d;
    }


    public static Incident toDomain(IncidentGesEntity i) {
        if (i == null) return null;

        Incident d = new Incident();

        d.setCaseId(i.getCaseId());
        d.setProbeId(i.getProbeId());
        d.setGalleryId(i.getGalleryId());
        d.setScore(i.getScore());
        d.setRank(i.getRank());
        d.setJobid(i.getJobid());
        d.setFilter(i.getFilter());

        d.setPriorityId(i.getPriorityId());
        d.setPriority(i.getPriority());
        d.setStatus(i.getStatus());
        d.setWorkplace(i.getWorkplace());

        d.setNote(i.getNote());
        d.setWorkplaceNote(i.getWorkplaceNote());
        d.setFileReference(i.getFileReference());
        d.setReferenceType(i.getReferenceType());
        d.setCreatedDate(i.getCreatedDate());

        d.setaApplicantOid(i.getaApplicantOid());
        d.setaPkz(i.getaPkz());
        d.setaFileNumber(i.getaFileNumber());
        d.setaAzrNumber(i.getaAzrNumber());
        d.setaDNumber(i.getaDNumber());
        d.setaENumber(i.getaENumber());
        d.setaEuroDacNumber(i.getaEuroDacNumber());
        d.setaLastName(i.getaLastName());
        d.setaFirstName(i.getaFirstName());
        d.setaBirthDate(i.getaBirthDate());
        d.setaGender(i.getaGender());
        d.setaNationality(i.getaNationality());
        d.setaOriginCountry(i.getaOriginCountry());
        d.setaBirthCountry(i.getaBirthCountry());
        d.setaBirthPlace(i.getaBirthPlace());
        d.setaApplicantType(i.getaApplicantType());
        d.setaWorkplace(i.getaWorkplace());
        d.setaApplicantDate(i.getaApplicantDate());
        d.setaDateModified(i.getaDateModified());

        d.setbApplicantOid(i.getbApplicantOid());
        d.setbPkz(i.getbPkz());
        d.setbFileNumber(i.getbFileNumber());
        d.setbAzrNumber(i.getbAzrNumber());
        d.setbDNumber(i.getbDNumber());
        d.setbENumber(i.getbENumber());
        d.setbEuroDacNumber(i.getbEuroDacNumber());
        d.setbLastName(i.getbLastName());
        d.setbFirstName(i.getbFirstName());
        d.setbBirthDate(i.getbBirthDate());
        d.setbGender(i.getbGender());
        d.setbNationality(i.getbNationality());
        d.setbOriginCountry(i.getbOriginCountry());
        d.setbBirthCountry(i.getbBirthCountry());
        d.setbBirthPlace(i.getbBirthPlace());
        d.setbApplicantType(i.getbApplicantType());
        d.setbWorkplace(i.getbWorkplace());
        d.setbApplicantDate(i.getbApplicantDate());
        d.setbDateModified(i.getbDateModified());

        d.setRowid(i.getRowid());
        d.setBemLastChangedBy(i.getBemLastChangedBy());
        d.setBemLastChangedOn(i.getBemLastChangedOn());
        d.setAusLastChangedBy(i.getAusLastChangedBy());
        d.setAusLastChangedOn(i.getAusLastChangedOn());

        d.setaPersonDeleted(i.getaPersonDeleted());
        d.setbPersonDeleted(i.getbPersonDeleted());
        d.setaAkteDeleted(i.getaAkteDeleted());
        d.setbAkteDeleted(i.getbAkteDeleted());
        d.setaAkteLocked(i.getaAkteLocked());
        d.setbAkteLocked(i.getbAkteLocked());

        if (i.getGesIncidentHistories() != null) {
            d.setIncidentHistory(
                    i.getGesIncidentHistories().stream()
                            .map(IncidentMapper::toDomain)
                            .collect(Collectors.toList())
            );
        }

        return d;
    }

    public static IncidentHistory toDomain(IncidentHistoryGesEntity i) {
        if (i == null) return null;

        IncidentHistory d = new IncidentHistory();

        d.setHistoryId(i.getHistoryId());
        d.setCaseId(i.getCaseId());
        d.setChangedBy(i.getChangedBy());
        d.setChangedOn(i.getChangedOn());
        d.setType(i.getType());

        return d;
    }

    public static IncidentHistory toDomain(IncidentHistoryCognitecEntity i) {
        if (i == null) return null;

        IncidentHistory d = new IncidentHistory();

        d.setHistoryId(i.getHistoryId());
        d.setCaseId(i.getCaseId());
        d.setChangedBy(i.getChangedBy());
        d.setChangedOn(i.getChangedOn());
        d.setType(i.getType());

        return d;
    }

    public static IncidentCognitecEntity toCognitecEntity(Incident d) {
        if (d == null) return null;

        IncidentCognitecEntity e = new IncidentCognitecEntity();

        e.setCaseId(d.getCaseId());
        e.setProbeId(d.getProbeId());
        e.setGalleryId(d.getGalleryId());
        e.setScore(d.getScore());
        e.setRank(d.getRank());
        e.setJobid(d.getJobid());
        e.setFilter(d.getFilter());

        e.setPriorityId(d.getPriorityId());
        e.setPriority(d.getPriority());
        e.setStatus(d.getStatus());
        e.setWorkplace(d.getWorkplace());

        e.setNote(d.getNote());
        e.setWorkplaceNote(d.getWorkplaceNote());
        e.setFileReference(d.getFileReference());
        e.setReferenceType(d.getReferenceType());
        e.setCreatedDate(d.getCreatedDate());

        e.setaApplicantOid(d.getaApplicantOid());
        e.setaPkz(d.getaPkz());
        e.setaFileNumber(d.getaFileNumber());
        e.setaAzrNumber(d.getaAzrNumber());
        e.setaDNumber(d.getaDNumber());
        e.setaENumber(d.getaENumber());
        e.setaEuroDacNumber(d.getaEuroDacNumber());
        e.setaLastName(d.getaLastName());
        e.setaFirstName(d.getaFirstName());
        e.setaBirthDate(d.getaBirthDate());
        e.setaGender(d.getaGender());
        e.setaNationality(d.getaNationality());
        e.setaOriginCountry(d.getaOriginCountry());
        e.setaBirthCountry(d.getaBirthCountry());
        e.setaBirthPlace(d.getaBirthPlace());
        e.setaApplicantType(d.getaApplicantType());
        e.setaWorkplace(d.getaWorkplace());
        e.setaApplicantDate(d.getaApplicantDate());
        e.setaDateModified(d.getaDateModified());

        e.setbApplicantOid(d.getbApplicantOid());
        e.setbPkz(d.getbPkz());
        e.setbFileNumber(d.getbFileNumber());
        e.setbAzrNumber(d.getbAzrNumber());
        e.setbDNumber(d.getbDNumber());
        e.setbENumber(d.getbENumber());
        e.setbEuroDacNumber(d.getbEuroDacNumber());
        e.setbLastName(d.getbLastName());
        e.setbFirstName(d.getbFirstName());
        e.setbBirthDate(d.getbBirthDate());
        e.setbGender(d.getbGender());
        e.setbNationality(d.getbNationality());
        e.setbOriginCountry(d.getbOriginCountry());
        e.setbBirthCountry(d.getbBirthCountry());
        e.setbBirthPlace(d.getbBirthPlace());
        e.setbApplicantType(d.getbApplicantType());
        e.setbWorkplace(d.getbWorkplace());
        e.setbApplicantDate(d.getbApplicantDate());
        e.setbDateModified(d.getbDateModified());

        e.setRowid(d.getRowid());
        e.setBemLastChangedBy(d.getBemLastChangedBy());
        e.setBemLastChangedOn(d.getBemLastChangedOn());
        e.setAusLastChangedBy(d.getAusLastChangedBy());
        e.setAusLastChangedOn(d.getAusLastChangedOn());

        e.setaPersonDeleted(d.getaPersonDeleted());
        e.setbPersonDeleted(d.getbPersonDeleted());
        e.setaAkteDeleted(d.getaAkteDeleted());
        e.setbAkteDeleted(d.getbAkteDeleted());
        e.setaAkteLocked(d.getaAkteLocked());
        e.setbAkteLocked(d.getbAkteLocked());

        e.setIncidentHistory(d.getIncidentHistory().stream()
                .map(IncidentMapper::toCognitecEntity)
                .collect(Collectors.toList()));

        return e;
    }

    public static IncidentGesEntity toGesEntity(Incident d) {
        if (d == null) return null;

        IncidentGesEntity e = new IncidentGesEntity();

        e.setCaseId(d.getCaseId());
        e.setProbeId(d.getProbeId());
        e.setGalleryId(d.getGalleryId());
        e.setScore(d.getScore());
        e.setRank(d.getRank());
        e.setJobid(d.getJobid());
        e.setFilter(d.getFilter());

        e.setPriorityId(d.getPriorityId());
        e.setPriority(d.getPriority());
        e.setStatus(d.getStatus());
        e.setWorkplace(d.getWorkplace());

        e.setNote(d.getNote());
        e.setWorkplaceNote(d.getWorkplaceNote());
        e.setFileReference(d.getFileReference());
        e.setReferenceType(d.getReferenceType());
        e.setCreatedDate(d.getCreatedDate());

        e.setaApplicantOid(d.getaApplicantOid());
        e.setaPkz(d.getaPkz());
        e.setaFileNumber(d.getaFileNumber());
        e.setaAzrNumber(d.getaAzrNumber());
        e.setaDNumber(d.getaDNumber());
        e.setaENumber(d.getaENumber());
        e.setaEuroDacNumber(d.getaEuroDacNumber());
        e.setaLastName(d.getaLastName());
        e.setaFirstName(d.getaFirstName());
        e.setaBirthDate(d.getaBirthDate());
        e.setaGender(d.getaGender());
        e.setaNationality(d.getaNationality());
        e.setaOriginCountry(d.getaOriginCountry());
        e.setaBirthCountry(d.getaBirthCountry());
        e.setaBirthPlace(d.getaBirthPlace());
        e.setaApplicantType(d.getaApplicantType());
        e.setaWorkplace(d.getaWorkplace());
        e.setaApplicantDate(d.getaApplicantDate());
        e.setaDateModified(d.getaDateModified());

        e.setbApplicantOid(d.getbApplicantOid());
        e.setbPkz(d.getbPkz());
        e.setbFileNumber(d.getbFileNumber());
        e.setbAzrNumber(d.getbAzrNumber());
        e.setbDNumber(d.getbDNumber());
        e.setbENumber(d.getbENumber());
        e.setbEuroDacNumber(d.getbEuroDacNumber());
        e.setbLastName(d.getbLastName());
        e.setbFirstName(d.getbFirstName());
        e.setbBirthDate(d.getbBirthDate());
        e.setbGender(d.getbGender());
        e.setbNationality(d.getbNationality());
        e.setbOriginCountry(d.getbOriginCountry());
        e.setbBirthCountry(d.getbBirthCountry());
        e.setbBirthPlace(d.getbBirthPlace());
        e.setbApplicantType(d.getbApplicantType());
        e.setbWorkplace(d.getbWorkplace());
        e.setbApplicantDate(d.getbApplicantDate());
        e.setbDateModified(d.getbDateModified());

        e.setRowid(d.getRowid());
        e.setBemLastChangedBy(d.getBemLastChangedBy());
        e.setBemLastChangedOn(d.getBemLastChangedOn());
        e.setAusLastChangedBy(d.getAusLastChangedBy());
        e.setAusLastChangedOn(d.getAusLastChangedOn());

        e.setaPersonDeleted(d.getaPersonDeleted());
        e.setbPersonDeleted(d.getbPersonDeleted());
        e.setaAkteDeleted(d.getaAkteDeleted());
        e.setbAkteDeleted(d.getbAkteDeleted());
        e.setaAkteLocked(d.getaAkteLocked());
        e.setbAkteLocked(d.getbAkteLocked());

        e.setGesIncidentHistories(d.getIncidentHistory().stream()
                .map(IncidentMapper::toGesEntity)
                .collect(Collectors.toList()));

        return e;
    }

    public static IncidentHistoryCognitecEntity toCognitecEntity(IncidentHistory d) {
        if (d == null) return null;

        IncidentHistoryCognitecEntity e = new IncidentHistoryCognitecEntity();

        e.setHistoryId(d.getHistoryId());
        e.setCaseId(d.getCaseId());
        e.setChangedBy(d.getChangedBy());
        e.setChangedOn(d.getChangedOn());
        e.setType(d.getType());

        return e;
    }

    public static IncidentHistoryGesEntity toGesEntity(IncidentHistory d) {
        if (d == null) return null;

        IncidentHistoryGesEntity e = new IncidentHistoryGesEntity();

        e.setHistoryId(d.getHistoryId());
        e.setCaseId(d.getCaseId());
        e.setChangedBy(d.getChangedBy());
        e.setChangedOn(d.getChangedOn());
        e.setType(d.getType());

        return e;
    }
}