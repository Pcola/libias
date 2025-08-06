package sk.atos.fri.dao.libias.service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.text.SimpleDateFormat;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.config.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import sk.atos.fri.common.Constants;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.dao.IncidentStatus;
import sk.atos.fri.dao.libias.model.*;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.FinishCaseRequest;
import sk.atos.fri.rest.model.IncidentCountResponse;
import sk.atos.fri.rest.model.IncidentSearchRequest;
import sk.atos.fri.rest.model.IncidentUpdateRequest;
import sk.atos.fri.rest.model.RelatedCase;

@Repository
public class IncidentService {

  @Autowired
  private Logger LOG; 

  @PersistenceContext(unitName = "libias-pu")
  private EntityManager entityManager;

  @Autowired
  private ServerConfig serverConfig;

  @Autowired
  private IncidentHistoryService incidentHistoryService;

  @Autowired
  private PriorityService priorityService;

  @Autowired
  private HeaderParserService headerParserService;

  private String incidentQueryHint;

  public Incident findByCaseId(Long caseId) {
    return entityManager.find(Incident.class, caseId);
  }

  public int countAll() {
    Query query = entityManager.createNamedQuery("Incident.countAll");
    return ((Long) query.getSingleResult()).intValue();
  }

  /**
   *
   * @param userProbeAndGallery - boolean value, if user is searching both side
   * @param request - IncidentSearchRequest sent from client
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return list of related incident suitable for criteria
   *
   * Creating CriteriaQuery based on sort flag (-1,1) sort by worid or default sort by caseId
   * Then, based on value of userProbeAndGallery, call another methods and create query
   */
  public List<Incident> searchIncident(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Incident> q = cb.createQuery(Incident.class);
    Root<Incident> c = q.from(Incident.class);
    
    q.select(c);
        
    if (request.getSort() != null && (request.getOrder().equals(-1) || request.getOrder().equals(1))) {
    	if (request.getOrder().equals(1)) {
    		q.orderBy(cb.asc(c.get(request.getSort())), cb.desc(c.get("rowid")));
    	} else if (request.getOrder().equals(-1)) {
       		q.orderBy(cb.desc(c.get(request.getSort())), cb.desc(c.get("rowid")));
    	}
    }
    else {
    	// default sort
   		q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
    }
    
    if (userProbeAndGallery != null && userProbeAndGallery) {
        createIncidentCriteria(cb, q, c, request, httpServletRequest);
    } else {
        createIncidentProbeIdCriteria(cb, q, c, request, httpServletRequest);
    }
        
    TypedQuery<Incident> query = entityManager.createQuery(q)
    	.setFirstResult(request.getFirst())
        .setMaxResults(request.getRows());
    
    addQueryHint(query);
    return query.getResultList();
  }

  /**
   *
   * @param userProbeAndGallery - boolean value, if user is searching both side
   * @param request - IncidentSearchRequest sent from client
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return count of incidents based on criteria
   *
   * Create criteria query and return count of found incidents
   */
  public Long incidentsCount(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<Integer> c = q.from((Class) Incident.class);
    q.select(cb.count(c));
    
    if (userProbeAndGallery != null && userProbeAndGallery) {
      createIncidentCriteria(cb, q, c, request, httpServletRequest);
    } else {
      createIncidentProbeIdCriteria(cb, q, c, request, httpServletRequest);
    }

    TypedQuery<Long> query = entityManager.createQuery(q);
    if (serverConfig.getUseHintForCount()) {
      addQueryHint(query);
    }
    return query.getSingleResult();
  }

  /**
   *
   * @param cb - CriteriaBuilder
   * @param q - CriteriaBuilder
   * @param c - Root
   * @param request - IncidentSearchRequest sent from client
   * @param httpServletRequest - HttpServletRequest sent from client for role checking
   *
   * Creating criteria query for incident based on criteria. Also including aAzrNumber.
   */
  private void createIncidentProbeIdCriteria(CriteriaBuilder cb, CriteriaQuery q, Root c, IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
    List<Predicate> predicates = new ArrayList<>();
    // We want to show cases only with filter on 0.
    predicates.add(cb.equal(c.get("filter"), 0));
    if (isDemo(httpServletRequest)) {
      predicates.add(cb.equal(c.get("caseId"), 0L));
    }
    if (request.getCaseId() != null) {
      predicates.add(cb.equal(c.get("caseId"), request.getCaseId()));
    }
    if (request.getPriorityId() != null) {
      predicates.add(cb.equal(c.get("priority"), new Priority(request.getPriorityId())));
    }
    if (StringUtils.isNotBlank(request.getCreatedDate())) {
      Date createdDate = null;
      try {
        createdDate = (new SimpleDateFormat("dd.MM.yyyy")).parse(request.getCreatedDate());
        predicates.add(cb.equal(c.get("createdDate").as(Date.class), createdDate));
      }
      catch (Exception e) {
        LOG.warn("Could not parse date '" + request.getCreatedDate() + "': " + e.getMessage());
      }
    }
    if (request.getPkz() != null) {
      predicates.add(cb.equal(c.get("aPkz"), request.getPkz()));
    }
    if (StringUtils.isNotBlank(request.getAzrNumber())) {
      predicates.add(cb.equal(c.get("aAzrNumber"), request.getAzrNumber()));
    }
    if (StringUtils.isNotBlank(request.getdNumber())) {
      predicates.add(cb.equal(c.get("aDNumber"), request.getdNumber()));
    }
    if (StringUtils.isNotBlank(request.getFirstName())) {
      predicates.add(cb.like(cb.lower(c.get("aFirstName")), handleWildchars(request.getFirstName().toLowerCase())));
    }
    if (StringUtils.isNotBlank(request.getLastName())) {
      predicates.add(cb.like(cb.lower(c.get("aLastName")), handleWildchars(request.getLastName().toLowerCase())));
    }
    if (StringUtils.isNotBlank(request.getNationality())) {
      predicates.add(cb.equal(cb.lower(c.get("aNationality")), request.getNationality().toLowerCase()));
    }
    if (StringUtils.isNotBlank(request.getWorkplaceId())) {
      predicates.add(cb.equal(c.get("workplace"), new Workplace(request.getWorkplaceId())));
    }
    if (request.getStatusId() != null) {
      predicates.add(cb.equal(c.get("status"), new Status(request.getStatusId())));
    } else {
      if (httpServletRequest.isUserInRole(Constants.ROLE_USER) && !httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER)) {
        List<Status> statuses = Arrays.asList(new Status[]{
          new Status(new Long(IncidentStatus.Open.id)),
          new Status(new Long(IncidentStatus.FilesDoublet.id)),
          new Status(new Long(IncidentStatus.FilesNoDoublet.id)),
          new Status(new Long(IncidentStatus.NotClear.id)),
          new Status(new Long(IncidentStatus.NoProcessing.id)),
          new Status(new Long(IncidentStatus.FilesNoLink.id)),
          new Status(new Long(IncidentStatus.Adjusted.id)),
          new Status(new Long(IncidentStatus.DNumberDiff.id)),
          new Status(new Long(IncidentStatus.AutoAdjusted.id))
        });
        predicates.add(c.get("status").in(statuses));
      }
    }
    if (StringUtils.isNotBlank(request.getReferenceType())) {
      if (!Constants.DEFAULT_VALUE.equals(request.getReferenceType())) {
        predicates.add(cb.equal(c.get("referenceType"), request.getReferenceType()));
      } else {
        predicates.add(cb.isNull(c.get("referenceType")));
      }
    }
    if (StringUtils.isNotBlank(request.getFileNumber())) {
      predicates.add(cb.equal(c.get("aFileNumber"), request.getFileNumber()));
    }
    if (!predicates.isEmpty()) {
      q.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }
  }

  /**
   *
   * @param cb - CriteriaBuilder
   * @param q - CriteriaBuilder
   * @param c - Root
   * @param request - IncidentSearchRequest sent from client
   * @param httpServletRequest - HttpServletRequest sent from client for role checking
   *
   * Creating criteria query for incident based on criteria.
   */
  private void createIncidentCriteria(CriteriaBuilder cb, CriteriaQuery q, Root c, IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
    List<Predicate> predicates = new ArrayList<>(); 
    // We want to show cases only with filter on 0.
    predicates.add(cb.equal(c.get("filter"), 0));
    if (isDemo(httpServletRequest)) {
      predicates.add(cb.equal(c.get("caseId"), 0L));
    }
    if (request.getCaseId() != null) {
      predicates.add(cb.equal(c.get("caseId"), request.getCaseId()));
    }
    if (request.getPriorityId() != null) {
      predicates.add(cb.equal(c.get("priority"), new Priority(request.getPriorityId())));
    }
    if (StringUtils.isNotBlank(request.getCreatedDate())) {
      Date createdDate = null;
      try {
        createdDate = (new SimpleDateFormat("dd.MM.yyyy")).parse(request.getCreatedDate());
        predicates.add(cb.equal(c.get("createdDate").as(Date.class), createdDate));
      }
      catch (Exception e) {
        LOG.warn("Could not parse date '" + request.getCreatedDate() + "': " + e.getMessage());
      }
    }
    if (request.getPkz() != null) {
      predicates.add(cb.or(cb.equal(c.get("aPkz"), request.getPkz()), cb.equal(c.get("bPkz"), request.getPkz())));
    }
    if (StringUtils.isNotBlank(request.getdNumber())) {
      predicates.add(cb.or(cb.equal(c.get("aDNumber"), request.getdNumber()), cb.equal(c.get("bDNumber"), request.getdNumber())));
    }
    if (StringUtils.isNotBlank(request.getFirstName())) {
      predicates.add(cb.or(cb.like(cb.lower(c.get("aFirstName")), handleWildchars(request.getFirstName().toLowerCase())), cb.like(cb.lower(c.get("bFirstName")), handleWildchars(request.getFirstName().toLowerCase()))));
    }
    if (StringUtils.isNotBlank(request.getLastName())) {
      predicates.add(cb.or(cb.like(cb.lower(c.get("aLastName")), handleWildchars(request.getLastName().toLowerCase())), cb.like(cb.lower(c.get("bLastName")), handleWildchars(request.getLastName().toLowerCase()))));
    }
    if (StringUtils.isNotBlank(request.getNationality())) {
      predicates.add(cb.or(cb.equal(cb.lower(c.get("aNationality")), request.getNationality().toLowerCase()), cb.equal(cb.lower(c.get("bNationality")), request.getNationality().toLowerCase())));
    }
    if (StringUtils.isNotBlank(request.getAzrNumber())) {
      predicates.add(cb.or(cb.equal(c.get("aAzrNumber"), request.getAzrNumber()), cb.equal(c.get("bAzrNumber"), request.getAzrNumber())));
    }
    if (StringUtils.isNotBlank(request.getWorkplaceId())) {
      predicates.add(cb.equal(c.get("workplace"), new Workplace(request.getWorkplaceId())));
    }
    if (request.getStatusId() != null) {
      predicates.add(cb.equal(c.get("status"), new Status(request.getStatusId())));
    } else {
      if (httpServletRequest.isUserInRole(Constants.ROLE_USER) && !httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER)) {
        List<Status> statuses = Arrays.asList(new Status[]{
          new Status(new Long(IncidentStatus.Open.id)),
          new Status(new Long(IncidentStatus.FilesDoublet.id)),
          new Status(new Long(IncidentStatus.FilesNoDoublet.id)),
          new Status(new Long(IncidentStatus.NotClear.id)),
          new Status(new Long(IncidentStatus.NoProcessing.id)),
          new Status(new Long(IncidentStatus.FilesNoLink.id)),
          new Status(new Long(IncidentStatus.Adjusted.id)),
          new Status(new Long(IncidentStatus.DNumberDiff.id)),
          new Status(new Long(IncidentStatus.AutoAdjusted.id))
        });
        predicates.add(c.get("status").in(statuses));
      }
    }
    if (StringUtils.isNotBlank(request.getReferenceType())) {
      if (Constants.VALUE_NULL.equals(request.getReferenceType())) {
        predicates.add(cb.isNull(c.get("referenceType")));
      } else if (!Constants.DEFAULT_VALUE.equals(request.getReferenceType())) {
        predicates.add(cb.equal(c.get("referenceType"), request.getReferenceType()));
      }
    }
    if (StringUtils.isNotBlank(request.getFileNumber())) {
      predicates.add(cb.or(cb.equal(c.get("aFileNumber"), request.getFileNumber()), cb.equal(c.get("bFileNumber"), request.getFileNumber())));
    }
    if (!predicates.isEmpty()) {
      q.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }
  }

  /**
   *
   * @param httpServletRequest - HttpServletRequest sent from client for role checking
   *
   * Check if the demo user is logged in
   */
  private boolean isDemo(HttpServletRequest httpServletRequest) {
    return "demo".equalsIgnoreCase(httpServletRequest.getUserPrincipal().getName());
  }

  /**
   *
   * @param input - string input
   * @return changed string with replaced * for %
   *
   * Using for creating query to find also substring in names
   */
  private String handleWildchars(String input) {
    return input.replace("*", "%") + "%";
  }

  private void addQueryHint(TypedQuery<?> query) {
    if (incidentQueryHint == null) {
      String hint = serverConfig.getIncidentQueryHint();
      if (hint != null && hint.length() > 0) {
        incidentQueryHint = "/*+ " + hint + " */";
      } else {
        incidentQueryHint = "";
      }
      LOG.info(null, "incidentQueryHint set to " + incidentQueryHint);
    }

    if (incidentQueryHint != null && incidentQueryHint.length() > 0) {
      query.setHint(QueryHints.HINT, incidentQueryHint);
    }
  }

  /**
   *
   * @param inc - Incident to update
   * @param request - IncidentUpdateRequest - holding infos about changes
   * @param httpServletRequest - HttpServletRequest sent from client
   * @return int value - if 1 is changed, otherwise not for role checking
   *
   * Change Incident values and then persist. Checking for permissions (user roles), if user is approved to do specific operations
   */
  public int updateCase(Incident inc, IncidentUpdateRequest request, HttpServletRequest httpServletRequest) {
    String username = httpServletRequest.getUserPrincipal().getName();

    int result = 0;
    if (inc != null) {      
      boolean changed = false;
      if (httpServletRequest.isUserInRole(Constants.ROLE_ADMIN) || httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER) || httpServletRequest.isUserInRole(Constants.ROLE_USER)) {
        if (request.getPriorityId()!= null) {                             
          inc.setPriority(entityManager.find(Priority.class, request.getPriorityId()));
          changed = true;
        }
      }
      if (((inc.getStatus().getStatusId() != IncidentStatus.ReadyToQA.id &&
              inc.getStatus().getStatusId() != IncidentStatus.Adjusted.id &&
              inc.getStatus().getStatusId() != IncidentStatus.AutoAdjusted.id) &&
              !httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER))
          || httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER)) {
        if (request.getStatusId() != null) {          
          inc.setStatus(entityManager.find(Status.class, request.getStatusId()));
        }
        changeNoteHistory(inc,request.getNote(), headerParserService.getUserFullName(httpServletRequest));
        inc.setNote(request.getNote());
        inc.setWorkplace(entityManager.find(Workplace.class, request.getWorkplaceId()));
        changed = true;
      } 
      
      if (changed) {
        entityManager.merge(inc);
        result = 1;
        entityManager.flush();
        LOG.info(username, "Case with caseId " + inc.getCaseId() + " updated");
      }
    }  
    
    return result;
  }

  /**
   *
   * @return list of all reference types of incident
   */
  public List<String> findAllReferenceType() {
    List<String> res = entityManager.createQuery("select distinct i.referenceType from Incident i where i.filter = 0 order by i.referenceType ASC").getResultList();
    res.removeAll(Collections.singleton(null));
    return res;
  }

  /**
   *
   * @param filter - filters from user input
   * @param username - username of logged user
   * @return list of incidents for logged user - only incidents created by logged user
   */
  public List<Incident> findAussenstellerCases(IncidentSearchRequest filter, String username) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Incident> q = cb.createQuery(Incident.class);
    Root<Incident> c = q.from(Incident.class);
    q.select(c);
    
    if ((filter.getSort() != null) && (filter.getOrder().equals(1) || filter.getOrder().equals(-1))) {
    	if (filter.getOrder().equals(1)) {
    		q.orderBy(cb.asc(c.get(filter.getSort())), cb.desc(c.get("rowid")));
    	} else if (filter.getOrder().equals(-1)) {
    		q.orderBy(cb.desc(c.get(filter.getSort())), cb.desc(c.get("rowid")));
    	}
    }
    else {
    	// default sort
   		q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
    }
    
    createAussenstellerCriteria(cb, q, c, filter, username);
    
    TypedQuery<Incident> query = entityManager.createQuery(q)
    	.setFirstResult(filter.getFirst())
    	.setMaxResults(filter.getRows());
    
    addQueryHint(query);
    return query.getResultList();
  }

  /**
   *
   * @param filter - filters from user input
   * @param username - username of logged user
   * @return count of logged user created incidents
   */
  public Long aussenstellerCasesCount(IncidentSearchRequest filter, String username) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<Integer> c = q.from((Class) Incident.class);
    q.select(cb.count(c));

    createAussenstellerCriteria(cb, q, c, filter, username);

    TypedQuery<Long> query = entityManager.createQuery(q);
    if (serverConfig.getUseHintForCount()) {
      addQueryHint(query);
    }
    return query.getSingleResult();
  }

  /**
   *
   * @param cb - CriteriaBuilder
   * @param q - CriteriaQuery
   * @param c - Root
   * @param request - IncidentSearchRequest from client
   * @param username - username of logged user
   *
   * It should return CriteriaQuery with specific filters
   */
  private void createAussenstellerCriteria(CriteriaBuilder cb, CriteriaQuery q, Root c, IncidentSearchRequest request, String username) {
    List<Predicate> predicates = new ArrayList<>();
    // We want to show cases only with filter on 0.
    predicates.add(cb.equal(c.get("filter"), 0));
    predicates.add(cb.equal(c.get("workplace"), new Workplace(request.getWorkplaceId())));
    if (request.getCaseId()!= null) {
      predicates.add(cb.equal(c.get("caseId"), request.getCaseId()));
    }    
    if (request.getPriorityId()!= null) {
      predicates.add(cb.equal(c.get("priority"), new Priority(request.getPriorityId())));
    }    
    if (StringUtils.isNotBlank(request.getCreatedDate())) {
      Date createdDate = null;
      try {
        createdDate = (new SimpleDateFormat("dd.MM.yyyy")).parse(request.getCreatedDate());
        predicates.add(cb.equal(c.get("createdDate").as(Date.class), createdDate));
      }
      catch (Exception e) {
        LOG.warn("Could not parse date '" + request.getCreatedDate() + "': " + e.getMessage());
      }
    }
    if (request.getPkz() != null) {
      predicates.add(cb.or(cb.equal(c.get("aPkz"), request.getPkz()), cb.equal(c.get("bPkz"), request.getPkz())));
    }
    if (request.getdNumber() != null) {
      predicates.add(cb.or(cb.equal(c.get("aDNumber"), request.getdNumber()), cb.equal(c.get("bDNumber"), request.getdNumber())));
    }
    if (StringUtils.isNotBlank(request.getFirstName())) {
      predicates.add(cb.or(cb.like(cb.lower(c.get("aFirstName")), handleWildchars(request.getFirstName().toLowerCase())), cb.like(cb.lower(c.get("bFirstName")), handleWildchars(request.getFirstName().toLowerCase()))));
    }
    if (StringUtils.isNotBlank(request.getLastName())) {
      predicates.add(cb.or(cb.like(cb.lower(c.get("aLastName")), handleWildchars(request.getLastName().toLowerCase())), cb.like(cb.lower(c.get("bLastName")), handleWildchars(request.getLastName().toLowerCase()))));
    }
    if (StringUtils.isNotBlank(request.getNationality())) {
      predicates.add(cb.or(cb.equal(cb.lower(c.get("aNationality")), request.getNationality().toLowerCase()), cb.equal(cb.lower(c.get("bNationality")), request.getNationality().toLowerCase())));
    }
    if (request.getAzrNumber() != null) {
      predicates.add(cb.or(cb.equal(c.get("aAzrNumber"), request.getAzrNumber()), cb.equal(c.get("bAzrNumber"), request.getAzrNumber())));
    }
    if (request.getStatusId() != null) {
      predicates.add(cb.equal(c.get("status"), new Status(request.getStatusId())));
    } else {
      List<Status> statuses = Arrays.asList(new Status[]{
        new Status(new Long(IncidentStatus.FilesDoublet.id)),
        new Status(new Long(IncidentStatus.FilesNoDoublet.id)),
        new Status(new Long(IncidentStatus.FilesNoLink.id)),
        new Status(new Long(IncidentStatus.ReadyToQA.id))
      });
      predicates.add(c.get("status").in(statuses));
    }
    if (StringUtils.isNotBlank(request.getReferenceType())) {
      if (Constants.VALUE_NULL.equals(request.getReferenceType())) {
        predicates.add(cb.isNull(c.get("referenceType")));
      } else if (!Constants.DEFAULT_VALUE.equals(request.getReferenceType())) {
        predicates.add(cb.equal(c.get("referenceType"), request.getReferenceType()));
      }
    }
    if (request.getFileNumber() != null) {
      predicates.add(cb.or(cb.equal(c.get("aFileNumber"), request.getFileNumber()), cb.equal(c.get("bFileNumber"), request.getFileNumber())));
    }
    if (!predicates.isEmpty()) {
      q.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }
  }

  /**
   *
   * @param request -FinishCaseRequest from client
   * @param httpServletRequest - HttpServletRequest from client
   * @return Incident, which is marked as finished - status is ReadyToQA
   */
  public Incident finishCase(FinishCaseRequest request, HttpServletRequest httpServletRequest) {
    Incident inc = findByCaseId(request.getCaseId());
    changeWorkPlaceNoteHistory(inc,request.getWorkplaceNote(), headerParserService.getUserFullName(httpServletRequest));
    inc.setWorkplaceNote(request.getWorkplaceNote());
    inc.setStatus(entityManager.find(Status.class, new Long(IncidentStatus.ReadyToQA.id)));
    return entityManager.merge(inc);
  }

  /**
   *
   * @return list of incident counts for each status (splitted among priorities)
   */
  public List<IncidentCountResponse> countCasesByStatus() {
    List<Long> prioList = priorityService.getAllRecords().stream().map(Priority::getPriorityId).collect(Collectors.toList());

    Query q = entityManager.createQuery("select i.status.statusId, i.priority.priorityId, count(i.caseId) "
        + " from Incident i where i.filter = 0 "
        + " group by i.status.statusId, i.priority.priorityId order by i.status.statusId, i.priority.priorityId");
    List<Object[]> results = q.getResultList();

    List<IncidentCountResponse> stats = new LinkedList<>();
    for (Object[] result : results) {
      Long statusId = (Long) result[0];
      Long priorityId = (Long) result[1];
      Long count = (Long) result[2];

      IncidentCountResponse stat = null;
      for (IncidentCountResponse s : stats) {
        if (s.getStatusId() == statusId) {
          stat = s;
          break;
        }
      }
      if (stat == null) {
        stat = new IncidentCountResponse(statusId, prioList);
        stats.add(stat);
      }

      stat.setCount(stat.getCount() + count);
      stat.getCountPrioMap().put(priorityId, count);
    }

    if (serverConfig.getStatisticsDisplayZeroLines()) {
      // find all other statuses without assigned incidents
      List<Status> allStatuses = entityManager.createQuery("select s from Status s order by s.statusId").getResultList();
      allStatuses.forEach(status -> {
        boolean found = false;
        for (IncidentCountResponse s : stats) {
          if (s.getStatusId() == status.getStatusId()) {
            found = true;
            break;
          }
        }
        if (!found) {
          stats.add(new IncidentCountResponse(status.getStatusId(), prioList));
        }
      });
    }

    return stats;
  }

  /**
   *
   * @param caseId - Incident ID
   * @return list of related cases for one specific case
   */
  public List<RelatedCase> getRelatedCases(Long caseId) {
    Incident inc = findByCaseId(caseId);

    Query q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from Incident i "        
        + " where (i.probeId = :probeId or i.galleryId = :probeId or i.probeId = :galleryId or i.galleryId = :galleryId) "
        + " and i.filter=0 "
        + " order by case when i.caseId = :caseId then 1 else i.caseId end");

    q.setParameter("caseId", caseId);
    q.setParameter("probeId", inc.getProbeId());
    q.setParameter("galleryId", inc.getGalleryId());
    List<Object[]> resultList = q.getResultList();

    List<RelatedCase> relatedCases = new LinkedList<>();
    if (resultList != null) {
      resultList.forEach((result) -> {
        relatedCases.add(new RelatedCase((Long) result[0], (Long) result[1], (Long) result[2]));
      });
    }

    return relatedCases;
  }

  /**
   *
   * @param caseId - Incident ID
   * @param username
   * @return - list of related cases for caseId
   */
  public List<RelatedCase> getSiteRelatedCases(Long caseId, String username) {
    Incident inc = findByCaseId(caseId);

    Query q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from Incident i "
        + " where (i.probeId = :probeId or i.galleryId = :probeId or i.probeId = :galleryId or i.galleryId = :galleryId) "
        + " and i.workplace.id = :workplaceId "
        + " and i.status.statusId in (2,3,6,8)  "
        + " and i.filter=0 "
        + " order by case when i.caseId = :caseId then 1 else i.caseId end");

    q.setParameter("caseId", caseId);
    q.setParameter("probeId", inc.getProbeId());
    q.setParameter("galleryId", inc.getGalleryId());
    q.setParameter("workplaceId", inc.getWorkplace().getId());
    List<Object[]> resultList = q.getResultList();


    List<RelatedCase> relatedCases = new LinkedList<>();
    if (resultList != null) {
      resultList.forEach((result) -> {
        relatedCases.add(new RelatedCase((Long) result[0], (Long) result[1], (Long) result[2]));
      });
    }

    return relatedCases;
  }

  /**
   *
   * @return all nationalities from dB
   */
  public List<String> findAllNationalities() {
    List<String> res = entityManager.createQuery("select i.aNationality from Incident i where i.filter = 0 union select i.bNationality from Incident i where i.filter = 0").getResultList();
    res.removeAll(Collections.singleton(null));
    return res;    
  }

  /**
   *
   * @return IDs list of images with missing person data
   */
  @Transactional
  public List<Long> getImageOidsWhereMissingPersonData() {
	  List<Long> ares = entityManager.createQuery("SELECT i.probeId from Incident i WHERE i.filter = 0 " + /* "AND i.status.statusId = 1 " +*/ "AND i.aApplicantOid IS NULL").getResultList();
	  List<Long> bres = entityManager.createQuery("SELECT i.galleryId from Incident i WHERE i.filter = 0 " + /*"AND i.status.statusId = 1 " +*/ "AND i.bApplicantOid IS NULL").getResultList();
	  return Stream.concat(ares.stream(), bres.stream()).distinct().collect(Collectors.toList());
  }

  /**
   *
   * @param filter - filters from client based on user input
   * @return list of suitable incident
   */
  @Transactional
  public List<Incident> getIncident(IncidentFilter filter) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Incident> q = cb.createQuery(Incident.class);
    Root<Incident> c = q.from(Incident.class);
    q.select(c);
    
    List<Predicate> predicates = new ArrayList<>(); 
    if (filter.getFilter() != null) {
        predicates.add(cb.equal(c.get("filter"), filter.getFilter()));
    }
    
    if (filter.getStatus() != null) {
        predicates.add(cb.equal(c.get("status"), new Status(new Long(filter.getStatus().id))));
    }
    
    if (filter.getAktenzeichen() != null) {
        String aktenzeichen = filter.getAktenzeichen();
        predicates.add(cb.or(cb.equal(c.get("aFileNumber"), aktenzeichen), cb.equal(c.get("bFileNumber"), aktenzeichen)));
    }
    
    if (filter.getAntragstellerOid() != null) {
    	Long angtragstellerOid = filter.getAntragstellerOid();
        predicates.add(cb.or(cb.equal(c.get("aApplicantOid"), angtragstellerOid), cb.equal(c.get("bApplicantOid"), angtragstellerOid)));
    }
    
    if (filter.getImageOid() != null) {
    	Long imageOid = filter.getImageOid();
        predicates.add(cb.or(cb.equal(c.get("probeId"), imageOid), cb.equal(c.get("galleryId"), imageOid)));
    }
    
    if (!predicates.isEmpty()) {
        q.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
    }
    
    TypedQuery<Incident> query = entityManager.createQuery(q);
    return query.getResultList();
  }

  /**
   *
   * @param incident Incident for uodate
   * @return if incident was updated
   */
  @Transactional
  public int updateIncident(Incident incident) {
    int result = 0;
    if (incident != null) {
        entityManager.merge(incident);
        result = 1;
        entityManager.flush();
        LOG.info(null, "Case with caseId " + incident.getCaseId() + " updated");
    }
    return result;
  }

  /**
   *
   * @param incident - Incident, which note was changed
   * @param note - new note
   * @param changedBy - username changer
   *
   * Add new IncidentHistory record for specific incident
   */
  private void changeNoteHistory(Incident incident, String note, String changedBy) {
    if (!Objects.equals(incident.getNote(), note)) {
      Date currentDate = new Date();

      incident.setBemLastChangedBy(changedBy);
      incident.setBemLastChangedOn(currentDate);

      IncidentHistory newRecord = new IncidentHistory();
      newRecord.setHistoryId(incidentHistoryService.getNewID());
      newRecord.setCaseId(incident.getCaseId());
      newRecord.setChangedBy(changedBy);
      newRecord.setChangedOn(currentDate);
      newRecord.setType(Constants.NOTE_SYMBOL);

      incident.getIncidentHistory().add(newRecord);
    }
  }

  /**
   *
   * @param incident - Incident, which note was changed
   * @param workPlaceNote - new workplace note
   * @param changedBy - username changer
   *
   * Add new IncidentHistory record for specific incident
   */
  private void changeWorkPlaceNoteHistory(Incident incident, String workPlaceNote, String changedBy) {
    if (!Objects.equals(incident.getWorkplaceNote(), workPlaceNote)) {
      Date currentDate = new Date();

      incident.setAusLastChangedBy(changedBy);
      incident.setAusLastChangedOn(currentDate);

      IncidentHistory newRecord = new IncidentHistory();
      newRecord.setHistoryId(incidentHistoryService.getNewID());
      newRecord.setCaseId(incident.getCaseId());
      newRecord.setChangedBy(changedBy);
      newRecord.setChangedOn(currentDate);
      newRecord.setType(Constants.WORKPLACE_NOTE_SYMBOL);

      incident.getIncidentHistory().add(newRecord);
    }
  }

  /**
   *
   * @return all nationalities from DB for searcher
   */
  public List<String> getNationalitiesSearcher() {
    return entityManager.createQuery("select n.nationality from Nationality n").getResultList();
  }

}
