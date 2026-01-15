package sk.atos.fri.dao.libias.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import sk.atos.fri.dao.libias.domain.Incident;
import sk.atos.fri.dao.libias.enums.SourceSystem;
import sk.atos.fri.dao.libias.model.IncidentFilter;
import sk.atos.fri.rest.model.FinishCaseRequest;
import sk.atos.fri.rest.model.IncidentCountResponse;
import sk.atos.fri.rest.model.IncidentSearchRequest;
import sk.atos.fri.rest.model.IncidentUpdateRequest;
import sk.atos.fri.rest.model.RelatedCase;
import java.util.Date;

/**
 * Interface defining operations for managing incidents in the system.
 * Provides methods for searching, counting, updating, and accessing incident data.
 */
public interface IIncidentService {

    /**
     * Finds an incident by its case ID.
     * 
     * @param caseId The ID of the case to find
     * @return The found incident, or null if not found
     */
    Incident findByCaseId(Long caseId);
    Incident findByCaseId(Long caseId, SourceSystem sourceSystem);

    /**
     * Counts all incidents in the system.
     * 
     * @return The total number of incidents
     */
    int countAll();
    int countAll(SourceSystem sourceSystem);


    /**
     * Searches for incidents based on provided search criteria.
     * 
     * @param userProbeAndGallery Boolean value indicating if user is searching both sides
     * @param request The search request containing filter criteria
     * @param httpServletRequest HTTP request for role checking
     * @return List of incidents matching the criteria
     */
    List<Incident> searchIncident(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest);
    List<Incident> searchIncident(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem);

    /**
     * Counts incidents matching the provided search criteria.
     * 
     * @param userProbeAndGallery Boolean value indicating if user is searching both sides
     * @param request The search request containing filter criteria
     * @param httpServletRequest HTTP request for role checking
     * @return Count of incidents matching the criteria
     */
    Long incidentsCount(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest);
    Long incidentsCount(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem);

    /**
     * Updates an incident case with provided information.
     * 
     * @param incident The incident to update
     * @param request The update request containing changes
     * @param httpServletRequest HTTP request for role checking
     * @return 1 if updated successfully, 0 otherwise
     */
    int updateCase(Incident incident, IncidentUpdateRequest request, HttpServletRequest httpServletRequest);
    int updateCase(Incident incident, IncidentUpdateRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem);

    /**
     * Finds all distinct reference types for incidents.
     * 
     * @return List of all reference types
     */
    List<String> findAllReferenceType();
    List<String> findAllReferenceType(SourceSystem sourceSystem);

    /**
     * Finds cases created by a specific user based on workplace.
     * 
     * @param filter Filters from user input
     * @param username Username of logged user
     * @return List of incidents created by the logged user
     */
    List<Incident> findAussenstellerCases(IncidentSearchRequest filter, String username);
    List<Incident> findAussenstellerCases(IncidentSearchRequest filter, String username, SourceSystem sourceSystem);

    /**
     * Counts cases created by a specific user based on workplace.
     * 
     * @param filter Filters from user input
     * @param username Username of logged user
     * @return Count of incidents created by the logged user
     */
    Long aussenstellerCasesCount(IncidentSearchRequest filter, String username);
    Long aussenstellerCasesCount(IncidentSearchRequest filter, String username, SourceSystem sourceSystem);

    /**
     * Marks a case as finished by changing its status to ReadyToQA.
     * 
     * @param request The finish case request
     * @param httpServletRequest HTTP request for user information
     * @return The updated incident
     */
    Incident finishCase(FinishCaseRequest request, HttpServletRequest httpServletRequest);
    Incident finishCase(FinishCaseRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem);

    /**
     * Counts cases by status and priority.
     * 
     * @return List of incident counts for each status (split by priorities)
     */
    List<IncidentCountResponse> countCasesByStatus();
    List<IncidentCountResponse> countCasesByStatus(SourceSystem sourceSystem);

    /**
     * Gets related cases for a specific incident.
     * 
     * @param caseId The case ID to find related cases for
     * @return List of related cases
     */
    List<RelatedCase> getRelatedCases(Long caseId);
    List<RelatedCase> getRelatedCases(Long caseId, SourceSystem sourceSystem);

    /**
     * Gets site-specific related cases for a specific incident.
     * 
     * @param caseId The case ID to find related cases for
     * @param username Username of the logged user
     * @return List of related cases for the site
     */
    List<RelatedCase> getSiteRelatedCases(Long caseId, String username);
    List<RelatedCase> getSiteRelatedCases(Long caseId, String username, SourceSystem sourceSystem);

    /**
     * Finds all nationalities from the database.
     * 
     * @return List of all nationalities
     */
    List<String> findAllNationalities();
    List<String> findAllNationalities(SourceSystem sourceSystem);

    /**
     * Gets image OIDs where person data is missing.
     * 
     * @return List of image OIDs with missing person data
     */
    List<Long> getImageOidsWhereMissingPersonData();

    /**
     * Gets incidents matching the provided filter.
     * 
     * @param filter The filter criteria
     * @return List of incidents matching the filter
     */
    List<Incident> getIncident(IncidentFilter filter);

    /**
     * Updates an incident in the database.
     * 
     * @param incident The incident to update
     * @return 1 if updated successfully, 0 otherwise
     */
    int updateIncident(Incident incident);

    /**
     * Gets all nationalities available for the searcher.
     * 
     * @return List of nationalities for the searcher
     */
    List<String> getNationalitiesSearcher();
    List<String> getNationalitiesSearcher(SourceSystem sourceSystem);

    /**
     * Označí súbory (AKZ) za zmazané pre všetky incidenty, kde sa AKZ nachádza
     * na strane A alebo B. Nastaví dátum zmazania podľa parametra pDate.
     *
     * @param pDate dátum zmazania
     * @param akz   číslo spisu (AKZ)
     * @return počet upravených incidentov
     */
    int markFilesDeletedByAkz(Date pDate, String akz);

    /**
     * Označí osobu (PKZ) ako zmazanú pre všetky incidenty, kde sa nachádza
     * na strane A alebo B. Nastaví dátum zmazania podľa parametra pDate.
     *
     * @param pDate dátum zmazania
     * @param pkz   identifikátor osoby (PKZ)
     * @return počet aktualizovaných incidentov
     */
    int markPersonsDeletedByPkz(Date pDate, Long pkz);
}
