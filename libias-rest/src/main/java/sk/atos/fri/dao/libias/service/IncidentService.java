package sk.atos.fri.dao.libias.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sk.atos.fri.common.Constants;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.HeaderParserService;
import sk.atos.fri.dao.IncidentStatus;
import sk.atos.fri.dao.libias.domain.Incident;
import sk.atos.fri.dao.libias.domain.IncidentHistory;
import sk.atos.fri.dao.libias.enums.SourceSystem;
import sk.atos.fri.dao.libias.mapper.IncidentMapper;
import sk.atos.fri.dao.libias.model.IncidentCognitecEntity;
import sk.atos.fri.dao.libias.model.IncidentFilter;
import sk.atos.fri.dao.libias.model.IncidentGesEntity;
import sk.atos.fri.dao.libias.model.Priority;
import sk.atos.fri.dao.libias.model.Status;
import sk.atos.fri.dao.libias.model.Workplace;
import sk.atos.fri.dao.libias.repository.IncidentCognitecRepository;
import sk.atos.fri.dao.libias.repository.IncidentGesRepository;
import sk.atos.fri.dao.libias.repository.IncidentHistoryCognitecRepository;
import sk.atos.fri.dao.libias.repository.IncidentHistoryGesRepository;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.model.FinishCaseRequest;
import sk.atos.fri.rest.model.IncidentCountResponse;
import sk.atos.fri.rest.model.IncidentSearchRequest;
import sk.atos.fri.rest.model.IncidentUpdateRequest;
import sk.atos.fri.rest.model.RelatedCase;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class IncidentService implements IIncidentService {

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

    @Autowired
    private IncidentCognitecRepository incidentCognitecRepository;

    @Autowired
    private IncidentGesRepository incidentGesRepository;

    @Autowired
    private IncidentHistoryCognitecRepository incidentHistoryCognitecRepository;

    @Autowired
    private IncidentHistoryGesRepository incidentHistoryGesRepository;

    @Transactional(readOnly = true)
    @Override
    public Incident findByCaseId(Long caseId) {
        IncidentCognitecEntity entity = incidentCognitecRepository.findByCaseId(caseId);
        return IncidentMapper.toDomain(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public Incident findByCaseId(Long caseId, SourceSystem system) {
        if (system == SourceSystem.GES) {
            IncidentGesEntity entity = incidentGesRepository.findByCaseId(caseId);
            return IncidentMapper.toDomain(entity);
        } else {
            IncidentCognitecEntity entity = incidentCognitecRepository.findByCaseId(caseId);
            return IncidentMapper.toDomain(entity);
        }
    }

    @Override
    public int countAll() {
        return incidentCognitecRepository.countAll();
    }


    @Override
    public int countAll(SourceSystem system) {
        if (system == SourceSystem.GES) {
            return incidentGesRepository.countAll();
        } else {
            return incidentCognitecRepository.countAll();
        }
    }

    /**
     * @param userProbeAndGallery - boolean value, if user is searching both side
     * @param request             - IncidentSearchRequest sent from client
     * @param httpServletRequest  - HttpServletRequest sent from client
     * @return list of related incident suitable for criteria
     * <p>
     * Creating CriteriaQuery based on sort flag (-1,1) sort by worid or default sort by caseId
     * Then, based on value of userProbeAndGallery, call another methods and create query
     */
    @Transactional
    public List<Incident> searchIncident(Boolean userProbeAndGallery,
                                         IncidentSearchRequest request,
                                         HttpServletRequest httpServletRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentCognitecEntity> q = cb.createQuery(IncidentCognitecEntity.class);
        Root<IncidentCognitecEntity> c = q.from(IncidentCognitecEntity.class);

        q.select(c);

        if (request.getSort() != null && (Objects.equals(request.getOrder(), -1) || Objects.equals(request.getOrder(), 1))) {
            if (Objects.equals(request.getOrder(), 1)) {
                q.orderBy(cb.asc(c.get(request.getSort())), safeDesc(cb, c, "rowid"));
            } else {
                q.orderBy(cb.desc(c.get(request.getSort())), safeDesc(cb, c, "rowid"));
            }
        } else {
            q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
        }

        if (Boolean.TRUE.equals(userProbeAndGallery)) {
            createIncidentCriteria(cb, q, c, request, httpServletRequest);
        } else {
            createIncidentProbeIdCriteria(cb, q, c, request, httpServletRequest);
        }

        TypedQuery<IncidentCognitecEntity> query = entityManager.createQuery(q)
                .setFirstResult(request.getFirst())
                .setMaxResults(request.getRows());

        addQueryHint(query);

        return query.getResultList()
                .stream()
                .map(IncidentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> searchIncident(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

        if (system == SourceSystem.GES) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<IncidentGesEntity> q = cb.createQuery(IncidentGesEntity.class);
            Root<IncidentGesEntity> c = q.from(IncidentGesEntity.class);

            q.select(c);

            if (request.getSort() != null && (Objects.equals(request.getOrder(), -1) || Objects.equals(request.getOrder(), 1))) {
                if (Objects.equals(request.getOrder(), 1)) {
                    q.orderBy(cb.asc(c.get(request.getSort())), safeDescGes(cb, c, "rowid"));
                } else {
                    q.orderBy(cb.desc(c.get(request.getSort())), safeDescGes(cb, c, "rowid"));
                }
            } else {
                q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
            }

            if (Boolean.TRUE.equals(userProbeAndGallery)) {
                createIncidentCriteria(cb, q, c, request, httpServletRequest);
            } else {
                createIncidentProbeIdCriteria(cb, q, c, request, httpServletRequest);
            }

            TypedQuery<IncidentGesEntity> query = entityManager.createQuery(q)
                    .setFirstResult(request.getFirst())
                    .setMaxResults(request.getRows());

            addQueryHint(query);

            return query.getResultList()
                    .stream()
                    .map(IncidentMapper::toDomain)
                    .collect(Collectors.toList());
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentCognitecEntity> q = cb.createQuery(IncidentCognitecEntity.class);
        Root<IncidentCognitecEntity> c = q.from(IncidentCognitecEntity.class);

        q.select(c);

        if (request.getSort() != null && (Objects.equals(request.getOrder(), -1) || Objects.equals(request.getOrder(), 1))) {
            if (Objects.equals(request.getOrder(), 1)) {
                q.orderBy(cb.asc(c.get(request.getSort())), safeDesc(cb, c, "rowid"));
            } else {
                q.orderBy(cb.desc(c.get(request.getSort())), safeDesc(cb, c, "rowid"));
            }
        } else {
            q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
        }

        if (Boolean.TRUE.equals(userProbeAndGallery)) {
            createIncidentCriteria(cb, q, c, request, httpServletRequest);
        } else {
            createIncidentProbeIdCriteria(cb, q, c, request, httpServletRequest);
        }

        TypedQuery<IncidentCognitecEntity> query = entityManager.createQuery(q)
                .setFirstResult(request.getFirst())
                .setMaxResults(request.getRows());

        addQueryHint(query);

        return query.getResultList()
                .stream()
                .map(IncidentMapper::toDomain)
                .collect(Collectors.toList());
    }


    private Order safeDesc(CriteriaBuilder cb, Root<IncidentCognitecEntity> c, String field) {
        try {
            c.get(field);
            return cb.desc(c.get(field));
        } catch (IllegalArgumentException ex) {
            return cb.desc(c.get("caseId"));
        }
    }

    private Order safeDescGes(CriteriaBuilder cb, Root<IncidentGesEntity> c, String field) {
        try {
            c.get(field);
            return cb.desc(c.get(field));
        } catch (IllegalArgumentException ex) {
            return cb.desc(c.get("caseId"));
        }
    }

    /**
     * @param userProbeAndGallery - boolean value, if user is searching both side
     * @param request             - IncidentSearchRequest sent from client
     * @param httpServletRequest  - HttpServletRequest sent from client
     * @return count of incidents based on criteria
     * <p>
     * Create criteria query and return count of found incidents
     */
    public Long incidentsCount(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Integer> c = q.from((Class) IncidentCognitecEntity.class);
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

    @Override
    public Long incidentsCount(Boolean userProbeAndGallery, IncidentSearchRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

        if (system == SourceSystem.GES) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> q = cb.createQuery(Long.class);
            Root<Integer> c = q.from((Class) IncidentGesEntity.class);
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
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Integer> c = q.from((Class) IncidentCognitecEntity.class);
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
     * @param cb                 - CriteriaBuilder
     * @param q                  - CriteriaBuilder
     * @param c                  - Root
     * @param request            - IncidentSearchRequest sent from client
     * @param httpServletRequest - HttpServletRequest sent from client for role checking
     *                           <p>
     *                           Creating criteria query for incident based on criteria. Also including aAzrNumber.
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
            } catch (Exception e) {
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
     * @param cb                 - CriteriaBuilder
     * @param q                  - CriteriaBuilder
     * @param c                  - Root
     * @param request            - IncidentSearchRequest sent from client
     * @param httpServletRequest - HttpServletRequest sent from client for role checking
     *                           <p>
     *                           Creating criteria query for incident based on criteria.
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
            } catch (Exception e) {
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
     * @param httpServletRequest - HttpServletRequest sent from client for role checking
     *                           <p>
     *                           Check if the demo user is logged in
     */
    private boolean isDemo(HttpServletRequest httpServletRequest) {
        return "demo".equalsIgnoreCase(httpServletRequest.getUserPrincipal().getName());
    }

    /**
     * @param input - string input
     * @return changed string with replaced * for %
     * <p>
     * Using for creating query to find also substring in names
     */
    private String handleWildchars(String input) {
        return input.replace("*", "%") + "%";
    }

    private void addQueryHint(TypedQuery<?> query) {
        incidentCognitecRepository.addQueryHint(query);
    }

    /**
     * @param inc                - Incident to update
     * @param request            - IncidentUpdateRequest - holding infos about changes
     * @param httpServletRequest - HttpServletRequest sent from client
     * @return int value - if 1 is changed, otherwise not for role checking
     * <p>
     * Change Incident values and then persist. Checking for permissions (user roles), if user is approved to do specific operations
     */
    public int updateCase(Incident inc, IncidentUpdateRequest request, HttpServletRequest httpServletRequest) {
        String username = httpServletRequest.getUserPrincipal().getName();

        int result = 0;
        if (inc != null) {
            boolean changed = false;
            if (httpServletRequest.isUserInRole(Constants.ROLE_ADMIN) || httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER) || httpServletRequest.isUserInRole(Constants.ROLE_USER)) {
                if (request.getPriorityId() != null) {
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
                changeNoteHistory(inc, request.getNote(), headerParserService.getUserFullName(httpServletRequest));
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

    @Override
    public int updateCase(Incident inc, IncidentUpdateRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem) {
        String username = httpServletRequest.getUserPrincipal().getName();

        int result = 0;
        if (inc != null) {
            boolean changed = false;
            if (httpServletRequest.isUserInRole(Constants.ROLE_ADMIN) || httpServletRequest.isUserInRole(Constants.ROLE_SUPERUSER) || httpServletRequest.isUserInRole(Constants.ROLE_USER)) {
                if (request.getPriorityId() != null) {
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
                changeNoteHistory(inc, request.getNote(), headerParserService.getUserFullName(httpServletRequest), sourceSystem);
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
     * @return list of all reference types of incident
     */
    public List<String> findAllReferenceType() {
        return incidentCognitecRepository.findAllReferenceType();
    }

    @Override
    public List<String> findAllReferenceType(SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;
        if (system == SourceSystem.GES) {
            return incidentGesRepository.findAllReferenceType();
        }
        return incidentCognitecRepository.findAllReferenceType();
    }

    /**
     * @param filter   - filters from user input
     * @param username - username of logged user
     * @return list of incidents for logged user - only incidents created by logged user
     */
    @Transactional
    public List<Incident> findAussenstellerCases(IncidentSearchRequest filter, String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentCognitecEntity> q = cb.createQuery(IncidentCognitecEntity.class);
        Root<IncidentCognitecEntity> c = q.from(IncidentCognitecEntity.class);
        q.select(c);

        if (filter.getSort() != null && (Integer.valueOf(1).equals(filter.getOrder()) || Integer.valueOf(-1).equals(filter.getOrder()))) {
            if (Integer.valueOf(1).equals(filter.getOrder())) {
                q.orderBy(cb.asc(c.get(filter.getSort())), safeDesc(cb, c, "rowid"));
            } else {
                q.orderBy(cb.desc(c.get(filter.getSort())), safeDesc(cb, c, "rowid"));
            }
        } else {
            // default sort
            q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
        }

        createAussenstellerCriteria(cb, q, c, filter, username);

        TypedQuery<IncidentCognitecEntity> query = entityManager.createQuery(q)
                .setFirstResult(filter.getFirst())
                .setMaxResults(filter.getRows());

        addQueryHint(query);

        return query.getResultList()
                .stream()
                .map(IncidentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Incident> findAussenstellerCases(IncidentSearchRequest filter, String username, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;
        if (system == SourceSystem.GES) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<IncidentGesEntity> q = cb.createQuery(IncidentGesEntity.class);
            Root<IncidentGesEntity> c = q.from(IncidentGesEntity.class);
            q.select(c);

            if (filter.getSort() != null && (Integer.valueOf(1).equals(filter.getOrder()) || Integer.valueOf(-1).equals(filter.getOrder()))) {
                if (Integer.valueOf(1).equals(filter.getOrder())) {
                    q.orderBy(cb.asc(c.get(filter.getSort())), safeDescGes(cb, c, "rowid"));
                } else {
                    q.orderBy(cb.desc(c.get(filter.getSort())), safeDescGes(cb, c, "rowid"));
                }
            } else {
                // default sort
                q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
            }

            createAussenstellerCriteria(cb, q, c, filter, username);

            TypedQuery<IncidentGesEntity> query = entityManager.createQuery(q)
                    .setFirstResult(filter.getFirst())
                    .setMaxResults(filter.getRows());

            addQueryHint(query);

            return query.getResultList()
                    .stream()
                    .map(IncidentMapper::toDomain)
                    .collect(Collectors.toList());

        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentCognitecEntity> q = cb.createQuery(IncidentCognitecEntity.class);
        Root<IncidentCognitecEntity> c = q.from(IncidentCognitecEntity.class);
        q.select(c);

        if (filter.getSort() != null && (Integer.valueOf(1).equals(filter.getOrder()) || Integer.valueOf(-1).equals(filter.getOrder()))) {
            if (Integer.valueOf(1).equals(filter.getOrder())) {
                q.orderBy(cb.asc(c.get(filter.getSort())), safeDesc(cb, c, "rowid"));
            } else {
                q.orderBy(cb.desc(c.get(filter.getSort())), safeDesc(cb, c, "rowid"));
            }
        } else {
            // default sort
            q.orderBy(cb.asc(c.get("priorityId")), cb.desc(c.get("caseId")));
        }

        createAussenstellerCriteria(cb, q, c, filter, username);

        TypedQuery<IncidentCognitecEntity> query = entityManager.createQuery(q)
                .setFirstResult(filter.getFirst())
                .setMaxResults(filter.getRows());

        addQueryHint(query);

        return query.getResultList()
                .stream()
                .map(IncidentMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * @param filter   - filters from user input
     * @param username - username of logged user
     * @return count of logged user created incidents
     */
    public Long aussenstellerCasesCount(IncidentSearchRequest filter, String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Integer> c = q.from((Class) IncidentCognitecEntity.class);
        q.select(cb.count(c));

        createAussenstellerCriteria(cb, q, c, filter, username);

        TypedQuery<Long> query = entityManager.createQuery(q);
        if (serverConfig.getUseHintForCount()) {
            addQueryHint(query);
        }
        return query.getSingleResult();
    }

    public Long aussenstellerCasesCount(IncidentSearchRequest filter, String username, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;
        if (system == SourceSystem.GES) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Long> q = cb.createQuery(Long.class);
            Root<Integer> c = q.from((Class) IncidentGesEntity.class);
            q.select(cb.count(c));

            createAussenstellerCriteria(cb, q, c, filter, username);

            TypedQuery<Long> query = entityManager.createQuery(q);
            if (serverConfig.getUseHintForCount()) {
                addQueryHint(query);
            }
            return query.getSingleResult();
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> q = cb.createQuery(Long.class);
        Root<Integer> c = q.from((Class) IncidentCognitecEntity.class);
        q.select(cb.count(c));

        createAussenstellerCriteria(cb, q, c, filter, username);

        TypedQuery<Long> query = entityManager.createQuery(q);
        if (serverConfig.getUseHintForCount()) {
            addQueryHint(query);
        }
        return query.getSingleResult();
    }

    /**
     * @param cb       - CriteriaBuilder
     * @param q        - CriteriaQuery
     * @param c        - Root
     * @param request  - IncidentSearchRequest from client
     * @param username - username of logged user
     *                 <p>
     *                 It should return CriteriaQuery with specific filters
     */
    private void createAussenstellerCriteria(CriteriaBuilder cb, CriteriaQuery q, Root c, IncidentSearchRequest request, String username) {
        List<Predicate> predicates = new ArrayList<>();
        // We want to show cases only with filter on 0.
        predicates.add(cb.equal(c.get("filter"), 0));
        predicates.add(cb.equal(c.get("workplace"), new Workplace(request.getWorkplaceId())));
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
            } catch (Exception e) {
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
     * @param request            -FinishCaseRequest from client
     * @param httpServletRequest - HttpServletRequest from client
     * @return Incident, which is marked as finished - status is ReadyToQA
     */
    public Incident finishCase(FinishCaseRequest request, HttpServletRequest httpServletRequest) {
        Incident inc = findByCaseId(request.getCaseId());
        changeWorkPlaceNoteHistory(inc, request.getWorkplaceNote(), headerParserService.getUserFullName(httpServletRequest));
        inc.setWorkplaceNote(request.getWorkplaceNote());
        inc.setStatus(entityManager.find(Status.class, new Long(IncidentStatus.ReadyToQA.id)));
        return entityManager.merge(inc);
    }

    @Override
    public Incident finishCase(FinishCaseRequest request, HttpServletRequest httpServletRequest, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;
        Incident inc = findByCaseId(request.getCaseId(), system);
        changeWorkPlaceNoteHistory(inc, request.getWorkplaceNote(), headerParserService.getUserFullName(httpServletRequest), sourceSystem);
        inc.setWorkplaceNote(request.getWorkplaceNote());
        inc.setStatus(entityManager.find(Status.class, new Long(IncidentStatus.ReadyToQA.id)));
        return entityManager.merge(inc);
    }

    /**
     * @return list of incident counts for each status (splitted among priorities)
     */
    public List<IncidentCountResponse> countCasesByStatus() {
        List<Long> prioList = priorityService.getAllRecords().stream().map(Priority::getPriorityId).collect(Collectors.toList());

        Query q = entityManager.createQuery("select i.status.statusId, i.priority.priorityId, count(i.caseId) "
                + " from IncidentCognitecEntity i where i.filter = 0 "
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

    @Override
    public List<IncidentCountResponse> countCasesByStatus(SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

        List<Long> prioList = priorityService.getAllRecords().stream().map(Priority::getPriorityId).collect(Collectors.toList());
        Query q;
        if (system == SourceSystem.GES) {
            q = entityManager.createQuery("select i.status.statusId, i.priority.priorityId, count(i.caseId) "
                    + " from IncidentGesEntity i where i.filter = 0 "
                    + " group by i.status.statusId, i.priority.priorityId order by i.status.statusId, i.priority.priorityId");
        } else {
            q = entityManager.createQuery("select i.status.statusId, i.priority.priorityId, count(i.caseId) "
                    + " from IncidentCognitecEntity i where i.filter = 0 "
                    + " group by i.status.statusId, i.priority.priorityId order by i.status.statusId, i.priority.priorityId");
        }

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
     * @param caseId - Incident ID
     * @return list of related cases for one specific case
     */
    public List<RelatedCase> getRelatedCases(Long caseId) {
        Incident inc = findByCaseId(caseId);

        Query q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from IncidentCognitecEntity i "
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

    @Override
    public List<RelatedCase> getRelatedCases(Long caseId, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

        Incident inc = findByCaseId(caseId, system);
        Query q;
        if (system == SourceSystem.GES) {
            q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from IncidentGesEntity i "
                    + " where (i.probeId = :probeId or i.galleryId = :probeId or i.probeId = :galleryId or i.galleryId = :galleryId) "
                    + " and i.filter=0 "
                    + " order by case when i.caseId = :caseId then 1 else i.caseId end");
        } else {
            q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from IncidentCognitecEntity i "
                    + " where (i.probeId = :probeId or i.galleryId = :probeId or i.probeId = :galleryId or i.galleryId = :galleryId) "
                    + " and i.filter=0 "
                    + " order by case when i.caseId = :caseId then 1 else i.caseId end");
        }

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
     * @param caseId   - Incident ID
     * @param username
     * @return - list of related cases for caseId
     */
    public List<RelatedCase> getSiteRelatedCases(Long caseId, String username) {
        Incident inc = findByCaseId(caseId);

        Query q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from IncidentCognitecEntity i "
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

    @Override
    public List<RelatedCase> getSiteRelatedCases(Long caseId, String username, SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

        Incident inc = findByCaseId(caseId, system);
        Query q;
        if (system == SourceSystem.GES) {
            q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from IncidentGesEntity i "
                    + " where (i.probeId = :probeId or i.galleryId = :probeId or i.probeId = :galleryId or i.galleryId = :galleryId) "
                    + " and i.workplace.id = :workplaceId "
                    + " and i.status.statusId in (2,3,6,8)  "
                    + " and i.filter=0 "
                    + " order by case when i.caseId = :caseId then 1 else i.caseId end");
        } else {
            q = entityManager.createQuery("select i.caseId, i.aPkz, i.bPkz from IncidentCognitecEntity i "
                    + " where (i.probeId = :probeId or i.galleryId = :probeId or i.probeId = :galleryId or i.galleryId = :galleryId) "
                    + " and i.workplace.id = :workplaceId "
                    + " and i.status.statusId in (2,3,6,8)  "
                    + " and i.filter=0 "
                    + " order by case when i.caseId = :caseId then 1 else i.caseId end");

        }

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
     * @return all nationalities from dB
     */
    public List<String> findAllNationalities() {
        return incidentCognitecRepository.findAllNationalities();
    }

    @Override
    public List<String> findAllNationalities(SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;
        if (system == SourceSystem.GES) {
            return incidentGesRepository.findAllNationalities();
        }
        return incidentCognitecRepository.findAllNationalities();
    }

    /**
     * @return IDs list of images with missing person data
     */
    @Transactional
    public List<Long> getImageOidsWhereMissingPersonData() {
        return incidentCognitecRepository.getImageOidsWhereMissingPersonData();
    }

    /**
     * @param filter - filters from client based on user input
     * @return list of suitable incident
     */
    @Transactional
    public List<Incident> getIncident(IncidentFilter filter) {
        List<IncidentCognitecEntity> entities =
                incidentCognitecRepository.getIncidentEntities(filter);

        return entities.stream()
                .map(IncidentMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * @param incident Incident for uodate
     * @return if incident was updated
     */
    @Transactional
    public int updateIncident(Incident incident) {

        IncidentCognitecEntity incidentCognitecEntity = IncidentMapper.toCognitecEntity(incident);
        return incidentCognitecRepository.updateIncident(incidentCognitecEntity);
    }

    /**
     * @param incident  - Incident, which note was changed
     * @param note      - new note
     * @param changedBy - username changer
     *                  <p>
     *                  Add new IncidentHistory record for specific incident
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

    private void changeNoteHistory(Incident incident, String note, String changedBy, SourceSystem sourceSystem) {
        if (!Objects.equals(incident.getNote(), note)) {
            Date currentDate = new Date();
            SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

            incident.setBemLastChangedBy(changedBy);
            incident.setBemLastChangedOn(currentDate);

            IncidentHistory newRecord = new IncidentHistory();
            if (system == SourceSystem.GES) {
                newRecord.setHistoryId(incidentHistoryGesRepository.getNewID());
            } else {
                newRecord.setHistoryId(incidentHistoryService.getNewID());
            }
            newRecord.setCaseId(incident.getCaseId());
            newRecord.setChangedBy(changedBy);
            newRecord.setChangedOn(currentDate);
            newRecord.setType(Constants.NOTE_SYMBOL);

            incident.getIncidentHistory().add(newRecord);
        }
    }

    /**
     * @param incident      - Incident, which note was changed
     * @param workPlaceNote - new workplace note
     * @param changedBy     - username changer
     *                      <p>
     *                      Add new IncidentHistory record for specific incident
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

    private void changeWorkPlaceNoteHistory(Incident incident, String workPlaceNote, String changedBy, SourceSystem sourceSystem) {
        if (!Objects.equals(incident.getWorkplaceNote(), workPlaceNote)) {
            Date currentDate = new Date();
            SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

            incident.setAusLastChangedBy(changedBy);
            incident.setAusLastChangedOn(currentDate);

            IncidentHistory newRecord = new IncidentHistory();
            if (system == SourceSystem.GES) {
                newRecord.setHistoryId(incidentHistoryGesRepository.getNewID());
            } else {
                newRecord.setHistoryId(incidentHistoryService.getNewID());
            }
            newRecord.setCaseId(incident.getCaseId());
            newRecord.setChangedBy(changedBy);
            newRecord.setChangedOn(currentDate);
            newRecord.setType(Constants.WORKPLACE_NOTE_SYMBOL);

            incident.getIncidentHistory().add(newRecord);
        }
    }

    /**
     * @return all nationalities from DB for searcher
     */
    public List<String> getNationalitiesSearcher() {
        return incidentCognitecRepository.getNationalitiesSearcher();
    }

    @Override
    public List<String> getNationalitiesSearcher(SourceSystem sourceSystem) {
        SourceSystem system = (sourceSystem != null) ? sourceSystem : SourceSystem.COGNITEC;

        if (system == SourceSystem.GES) {
            return incidentGesRepository.getNationalitiesSearcher();
        }
        return incidentCognitecRepository.getNationalitiesSearcher();
    }

    @Transactional
    @Override
    public int markFilesDeletedByAkz(Date pDate, String akz) {
        if (akz == null) return 0;

        List<IncidentCognitecEntity> toUpdate = incidentCognitecRepository.findByAkzForDeletion(akz);
        LOG.debug(null, "Updating " + toUpdate.size() + " incidents with deleted AKZ " + akz);

        int updated = 0;
        for (IncidentCognitecEntity e : toUpdate) {
            boolean changed = false;

            if (akz.equals(e.getaFileNumber())) {
                e.setaAkteDeleted(pDate);
                changed = true;
            }
            if (akz.equals(e.getbFileNumber())) {
                e.setbAkteDeleted(pDate);
                changed = true;
            }

            if (changed) {
                incidentCognitecRepository.saveOrMerge(e);
                updated++;
            }
        }
        return updated;
    }

    @Transactional
    @Override
    public int markPersonsDeletedByPkz(Date pDate, Long pkz) {
        if (pkz == null) return 0;

        List<IncidentCognitecEntity> toUpdate = incidentCognitecRepository.findByPkzForDeletion(pkz);
        LOG.debug(null, "Updating " + toUpdate.size() + " incidents with deleted PKZ " + pkz);

        int updated = 0;
        for (IncidentCognitecEntity e : toUpdate) {
            boolean changed = false;

            if (pkz.equals(e.getaPkz())) {
                e.setaPersonDeleted(pDate);
                changed = true;
            }
            if (pkz.equals(e.getbPkz())) {
                e.setbPersonDeleted(pDate);
                changed = true;
            }

            if (changed) {
                incidentCognitecRepository.saveOrMerge(e);
                updated++;
            }
        }
        return updated;
    }

}
