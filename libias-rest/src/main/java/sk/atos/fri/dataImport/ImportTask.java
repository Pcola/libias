package sk.atos.fri.dataImport;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.WebServiceClientException;
import sk.atos.fri.common.Constants.JobStatus;
import sk.atos.fri.common.LibiasAppContext;
import sk.atos.fri.configuration.ServerConfig;
import sk.atos.fri.dao.libias.model.AkteLocked;
import sk.atos.fri.dao.libias.model.Image;
import sk.atos.fri.dao.libias.service.AkteLockedService;
import sk.atos.fri.dao.libias.service.DataImportService;
import sk.atos.fri.dao.libias.service.GesIncidentService;
import sk.atos.fri.dao.libias.service.IIncidentService;
import sk.atos.fri.log.Error;
import sk.atos.fri.log.Logger;
import sk.atos.fri.rest.DataImportException;
import sk.atos.fri.rest.model.JobStatusResponse;
import sk.atos.fri.ws.cognitec.service.ICognitecWSClient;
import sk.atos.fri.ws.ges.service.IGesClient;
import sk.atos.fri.ws.ges.service.model.DeleteRequest;
import sk.atos.fri.ws.ges.service.model.EnrollRequest;
import sk.atos.fri.ws.ges.service.model.EnrollResponse;
import sk.atos.fri.ws.ges.service.model.EnrollResultForImage;
import sk.atos.fri.ws.maris.model.DeletedFile;
import sk.atos.fri.ws.maris.model.DeletedFiles;
import sk.atos.fri.ws.maris.model.DeletedPerson;
import sk.atos.fri.ws.maris.model.DeletedPersons;
import sk.atos.fri.ws.maris.model.LockedFile;
import sk.atos.fri.ws.maris.model.LockedFiles;
import sk.atos.fri.ws.maris.model.UpdatedApplicant;
import sk.atos.fri.ws.maris.model.UpdatedApplicants;
import sk.atos.fri.ws.maris.service.MarisWSClient;
import sk.atos.fri.ws.maris.service.TokenStore;

import java.net.SocketTimeoutException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author a605053
 */
@Component
public class ImportTask implements IDataImport {

    private final static SimpleDateFormat jobIdFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private final Logger LOG = new Logger();
    private final ApplicationContext context;
    private boolean skipCogniImport;
    private boolean skipDeleteOverWs;
    private final boolean isGesEnabled;
    private final JobStatusResponse jobStatusResponse;

    private final AkteLockedService akteLockedService;
    private final DataImportService dataImportService;
    private final IIncidentService incidentService;
    private final MarisWSClient marisClient;
    private final ICognitecWSClient cognitecEnrollmentClient;
    private ExecutorService executorService;
    private final IGesClient gesClient;
    private final GesIncidentService  gesIncidentService;

    private final String gesElasticUser;
    private final String gesElasticPwd;
    private final String gesIndexName;

    @Autowired
    public ImportTask(AkteLockedService akteLockedService, DataImportService dataImportService,
                      IIncidentService incidentService, MarisWSClient marisClient, ICognitecWSClient cognitecEnrollmentClient, IGesClient gesClient, GesIncidentService gesIncidentService, ServerConfig serverConfig) {
        this.isGesEnabled = serverConfig.isGesEnabled();
        this.gesClient = gesClient;
        this.gesIncidentService = gesIncidentService;
        this.gesElasticUser = serverConfig.getGesElasticUser();
        this.gesElasticPwd = serverConfig.getGesElasticPwd();
        this.gesIndexName = serverConfig.getGesIndexName();
        this.context = LibiasAppContext.getApplicationContext();
        this.skipCogniImport = false;
        this.skipDeleteOverWs = false;
        this.jobStatusResponse = new JobStatusResponse();
        this.jobStatusResponse.setJobStatus(JobStatus.None.name());
        this.jobStatusResponse.setProgress(0);

        this.akteLockedService = akteLockedService;
        this.dataImportService = dataImportService;
        this.incidentService = incidentService;
        this.marisClient = marisClient;
        this.cognitecEnrollmentClient = cognitecEnrollmentClient;
    }

    @Override
    public void run() {
        if (isRunning()) {
            LOG.info("Import Scheduler: DB Import Job is already running, state: " + jobStatusResponse.getJobStatus());
        } else {
            executeDataImport();
        }
    }

    @Override
    public void willSkipMarisImport(boolean skip) {
        if (isRunning()) {
            return;
        } else {
            skipCogniImport = skip;
        }
    }

    @Override
    public void willSkipDeleteOverWs(boolean skip) {
        if (isRunning()) {
            return;
        } else {
            skipDeleteOverWs = skip;
        }
    }

    @Override
    public JobStatusResponse getStatus() {
        return jobStatusResponse;
    }

    @Override
    public boolean isRunning() {
        if (jobStatusResponse.getJobStatus().equals(JobStatus.None.name().toUpperCase())) {
            return false;
        }
        if (jobStatusResponse.getJobStatus().equals(JobStatus.Finished.name().toUpperCase())) {
            return false;
        }
        return true;
    }

    private void executeDataImport() {
        try {
            jobStatusResponse.reset();

            executorService = Executors.newFixedThreadPool(context.getBean(Integer.class, "threadCount"));

            final String jobId = jobIdFormat.format(new Date());
            LOG.info("Starting new data import for JobId: " + jobId);
            jobStatusResponse.setJobStarted(new Date());

            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            final Date pDate = cal.getTime();

            if (skipCogniImport) {
                LOG.info("skipped jobs: importMaris2Libias, importDeleted2Libias, deleteCasesFromCognitec," +
                        " importLibias2Cognitec, importDeleted2Cognitec, startDbEnrollment," +
                        " importCrossIdentificationResult2Libias, filterIncidentBeforeDataFetch");
            } else {
                importMaris2Libias();
                importDeleted2Libias(pDate);
                if (skipDeleteOverWs) {
                    LOG.info("skipped job: deleteCasesFromCognitec");
                } else {
                    deleteCasesFromCognitec(pDate);
                }
                importLibias2Cognitec(jobId);
                importDeleted2Cognitec(pDate);
                startDbEnrollment(jobId);
                importCrossIdentificationResult2Libias(jobId);
                filterIncidentBeforeDataFetch();
            }

            TokenStore.setToken(marisClient.getToken());
            if (TokenStore.getToken() == null) {
                LOG.info("skipped jobs: resetAfterFetchFilter, updateIncidentPersonData, updateDeletedPersons," +
                        " updateDeletedFiles, updateLockedFiles, getNewIncidentPersonData," +
                        " filterIncidentAfterDataFetch due to missing token");
            } else {
                resetAfterFetchFilter();
                updateIncidentPersonData(pDate);
                updateDeletedPersons(pDate);
                updateDeletedFiles(pDate);
                updateLockedFiles(pDate);
                getNewIncidentPersonData(pDate);
                filterIncidentAfterDataFetch();
            }
            runGesScheduledJob(pDate, jobId);

            LOG.info("Data import finished for JobId: " + jobId);
        } catch (Exception e) {
            LOG.error(Error.DATA_IMPORT, e);
            jobStatusResponse.setErrorMessage(ExceptionUtils.getStackTrace(e));
        } finally {
            jobStatusResponse.setJobFinished(new Date());
            jobStatusResponse.setJobStatus(JobStatus.Finished.name());
            willSkipMarisImport(false);
            executorService.shutdown();
        }
    }

    private void deleteCasesFromCognitec(Date pDate) throws DataImportException {
        LOG.info("Starting job 'deleteFromCognitec.'");
        jobStatusResponse.setJobStatus(JobStatus.DeleteCasesFromCognitec.name());
        try {
            // trying to delete cases by cognitec
            List<Long> caseIds = dataImportService.findCasesToDelete(pDate);
            LOG.info("DELETING " + caseIds.size() + " COGNITEC CASES");

            for (Long caseId : caseIds) {
                cognitecEnrollmentClient.deleteCase(caseId.toString());
            }
        } catch (Exception e) {
            LOG.error(Error.DB_LIBIAS_COGNITEC_JOB, e);
            throw new DataImportException(Error.DB_LIBIAS_COGNITEC_JOB.getDescription(), e);
        }
    }

    public void runGesScheduledJob(Date pDate, String jobId) throws DataImportException {

        if (!isGesEnabled()) {
            LOG.info("GES processing disabled by config – skipping delete/enroll/search.");
            return;
        }

        deleteCasesFromGes(pDate);

        List<EnrollResultForImage> enrollResults = enrollImagesForGes(pDate);

        searchWithEmbeddingAndCreateIncidents(enrollResults, jobId, pDate);
    }

    public List<EnrollResultForImage> enrollImagesForGes(Date pDate) throws DataImportException {
        LOG.info("Starting job 'enrollImagesForGes' for date {}." + pDate);
        jobStatusResponse.setJobStatus(JobStatus.EnrollCasesToGes.name());

        try {
            List<Image> images = dataImportService.findImagesToEnrollForGes(pDate);
            LOG.info("ENROLLING {} GES DOCUMENTS" + images.size());

            List<Long> enrolledIds = new ArrayList<>(images.size());
            List<EnrollResultForImage> results = new ArrayList<>();

            for (Image image : images) {
                try {
                    EnrollRequest request = buildEnrollRequest(image);
                    EnrollResponse response = gesClient.enroll(request);

                    results.add(new EnrollResultForImage(image, response));
                    enrolledIds.add(image.getOid());
                } catch (Exception ex) {
                    LOG.error(Error.valueOf("Failed to enroll image oid={} to GES" + image.getOid()), ex);
                }
            }

            if (!enrolledIds.isEmpty()) {
                Date today = new Date();
                dataImportService.markGesDateCreated(enrolledIds, today);
                LOG.info("Successfully enrolled {} documents to GES and updated dateCreatedGes.", String.valueOf(enrolledIds.size()));
            } else {
                LOG.warn("No images successfully enrolled to GES for date {}.", String.valueOf(pDate));
            }

            return results;

        } catch (Exception e) {
            LOG.error(Error.valueOf("Unexpected error while enrolling images to GES"), e);
            throw new DataImportException("Error while enrolling cases to GES", e);
        }
    }

    public void deleteCasesFromGes(Date pDate) throws DataImportException {
        LOG.info("Starting job 'deleteFromGes'.");
        jobStatusResponse.setJobStatus(JobStatus.DeleteCasesFromGes.name());

        try {
            List<Long> caseIds = dataImportService.findCasesToDeleteForGes(pDate);
            LOG.info("DELETING {} GES DOCUMENTS", String.valueOf(caseIds.size()));

            List<Long> successfullyDeleted = new ArrayList<>();

            for (Long caseId : caseIds) {
                try {
                    DeleteRequest request = buildDeleteRequest(caseId);
                    gesClient.deleteDocument(request);
                    successfullyDeleted.add(caseId);
                } catch (Exception ex) {
                    LOG.error(Error.valueOf("Failed to delete GES document for caseId={}"+ caseId), ex);
                }
            }

            if (!successfullyDeleted.isEmpty()) {
                Date today = new Date();
                dataImportService.markGesDeleted(successfullyDeleted, today);
                LOG.info("Successfully deleted {} documents from GES and updated dateDeletedGes.",
                        String.valueOf(successfullyDeleted.size()));
            } else {
                LOG.warn("No documents successfully deleted from GES for date {}.", String.valueOf(pDate));
            }

        } catch (Exception e) {
            LOG.error(Error.valueOf("Unexpected error while deleting cases from GES"), e);
            throw new DataImportException("Error while deleting cases from GES", e);
        }
    }

    public void searchWithEmbeddingAndCreateIncidents(List<EnrollResultForImage> enrollResults,
                                                      String jobId, Date pDate) {
        LOG.info("Starting job 'searchWithEmbeddingAndCreateIncidents' for {} enroll results.",
                String.valueOf(enrollResults.size()));
        jobStatusResponse.setJobStatus(JobStatus.SearchWithEmbeddingForGes.name());

        for (EnrollResultForImage result : enrollResults) {
            Image image = result.getImage();
            EnrollResponse enrollResponse = result.getEnrollResponse();

            try {
                Long probeId = image.getOid();

                gesIncidentService.createIncidentsFromEnrollResult(
                        probeId,
                        enrollResponse,
                        jobId,
                        pDate
                );
            } catch (Exception ex) {
                LOG.error(Error.valueOf("Failed to create incidents for image oid={}" + image.getOid()), ex);
            }
        }
    }

    private DeleteRequest buildDeleteRequest(Long caseId) {
        DeleteRequest r = new DeleteRequest();
        r.setElastic_user(gesElasticUser);
        r.setElastic_pwd(gesElasticPwd);
        r.setIndex_name(gesIndexName);
        r.setId(caseId);
        return r;
    }

    private EnrollRequest buildEnrollRequest(Image image) {
        EnrollRequest r = new EnrollRequest();

        r.setElastic_user(gesElasticUser);
        r.setElastic_pwd(gesElasticPwd);
        r.setIndex_name(gesIndexName);

        r.setId(image.getOid());
        r.setImg(image.getImageData());

        return r;
    }

    private void updateDeletedPersons(Date pDate) throws DataImportException {
        LOG.info("Starting job 'updateDeletedPersons'");
        jobStatusResponse.setJobStatus(JobStatus.RunningUpdateDeletedPersons.name());

        try {
            DeletedPersons dp = marisClient.getDeletedPersons(pDate);
            if (dp == null) {
                return;
            }

            List<DeletedPerson> deletedPersons = dp.getDeletedPersons();
            if (deletedPersons == null) {
                return;
            }

            LOG.info("Got " + deletedPersons.size() + " deleted person ids");
            if (deletedPersons.size() == 0) {
                return;
            }

            deletedPersons.forEach(person -> {
                dataImportService.updateDeletedPersons(pDate, person.getPersonNumber(), incidentService);
            });
        } catch (Exception ex) {
            LOG.error(Error.MARIS_FETCH, ex);
        }
    }

    private void updateDeletedFiles(Date pDate) throws DataImportException {
        LOG.info("Starting job 'updateDeletedFiles'");
        jobStatusResponse.setJobStatus(JobStatus.RunningUpdateDeletedFiles.name());

        try {
            DeletedFiles df = marisClient.getDeletedFiles(pDate);
            if (df == null) {
                return;
            }

            List<DeletedFile> deletedFiles = df.getDeletedFiles();
            if (deletedFiles == null) {
                return;
            }

            LOG.info("Got " + deletedFiles.size() + " deleted file ids");
            if (deletedFiles.size() == 0) {
                return;
            }

            deletedFiles.forEach(file -> {
                dataImportService.updateDeletedFiles(pDate, file.getFileNumber(), incidentService);
            });
        } catch (Exception ex) {
            LOG.error(Error.MARIS_FETCH, ex);
        }
    }

    private void updateLockedFiles(Date pDate) throws DataImportException {
        LOG.info("Starting job 'updateLockedFiles'");
        jobStatusResponse.setJobStatus(JobStatus.RunningUpdateLockedFiles.name());
        jobStatusResponse.setProgress(0);

        try {
            LockedFiles lf = marisClient.getLockedFiles();
            if (lf == null) {
                return;
            }

            List<LockedFile> lockedFiles = lf.getLockedFiles();
            if (lockedFiles == null) {
                return;
            }

            LOG.info("Got " + lockedFiles.size() + " locked file ids");
            if (lockedFiles.size() == 0) {
                return;
            }

            // compare locked file numbers from last and current runs and process only changes
            List<String> lastLockedFileNumbers = akteLockedService.findAll().stream()
                    .map(AkteLocked::getFileNumber).collect(Collectors.toList());
            List<String> currentLockedFileNumbers = lockedFiles.stream()
                    .map(LockedFile::getFileNumber).collect(Collectors.toList());

            List<String> lockedFileNumbers = new ArrayList<String>(currentLockedFileNumbers);
            lockedFileNumbers.removeAll(lastLockedFileNumbers);

            List<String> unlockedFileNumbers = new ArrayList<String>(lastLockedFileNumbers);
            unlockedFileNumbers.removeAll(currentLockedFileNumbers);

            List<String> changedFileNumbers = new ArrayList<String>(lockedFileNumbers);
            changedFileNumbers.addAll(unlockedFileNumbers);

            LOG.info("Got " + changedFileNumbers.size() + " changes in locked file ids");
            if (changedFileNumbers.size() == 0) {
                return;
            }

            List<Future<String>> toComplete = new ArrayList<Future<String>>();
            List<String> completed = new ArrayList<String>();
            changedFileNumbers.forEach(fileNumber -> {
                toComplete.add(executorService.submit(new ApplicantDataLockTask(fileNumber, pDate, marisClient, incidentService)));
            });

            int lastProgress = 0;
            int currProgress = 0;
            for (Future<String> fileNumber : toComplete) {
                completed.add(fileNumber.get());
                currProgress = 100 * completed.size() / changedFileNumbers.size();
                if (currProgress != lastProgress && currProgress % 10 == 0) {
                    jobStatusResponse.setProgress(currProgress);
                    LOG.info("UpdateLockedFiles status : " + currProgress);
                }
                lastProgress = currProgress;
            }

            // store changes
            lockedFileNumbers.forEach(fileNumber -> {
                akteLockedService.persist(fileNumber, pDate);
            });
            unlockedFileNumbers.forEach(fileNumber -> {
                akteLockedService.delete(fileNumber);
            });
        } catch (Exception ex) {
            LOG.error(Error.MARIS_FETCH, ex);
        }
    }

    private void importDeleted2Libias(Date pDate) throws DataImportException {
        LOG.info("Starting job 'importDeleted2Libias.'");
        jobStatusResponse.setJobStatus(JobStatus.RunningDeleted2Libias.name());
        try {
            dataImportService.importDeleted2Libias(pDate);
        } catch (SQLException e) {
            LOG.error(Error.DB_MARIS_JOB, e);
            throw new DataImportException(Error.DB_MARIS_JOB.getDescription(), e);
        }
    }

    private void importDeleted2Cognitec(Date pDate) throws DataImportException {
        LOG.info("Starting job 'importDeleted2Cognitec.'");
        jobStatusResponse.setJobStatus(JobStatus.RunningDeleted2Cognitec.name());
        try {
            dataImportService.importDeleted2Cognitec(pDate);
        } catch (SQLException e) {
            LOG.error(Error.DB_LIBIAS_COGNITEC_JOB, e);
            throw new DataImportException(Error.DB_LIBIAS_COGNITEC_JOB.getDescription(), e);
        }
    }

    private void importMaris2Libias() throws DataImportException {
        LOG.info("Starting job 'importMaris2Libias.'");
        jobStatusResponse.setJobStatus(JobStatus.RunningMaris2Libias.name());
        try {
            dataImportService.importMaris2Libias();
        } catch (SQLException e) {
            LOG.error(Error.DB_MARIS_JOB, e);
            throw new DataImportException(Error.DB_MARIS_JOB.getDescription(), e);
        }
    }

    private void filterIncidentBeforeDataFetch() throws DataImportException {
        LOG.info("Starting job 'filterIncidentBeforeDataFetch.'");
        jobStatusResponse.setJobStatus(JobStatus.RunningFilterBeforeDataFetch.name());
        try {
            dataImportService.filterIncidentBeforeDataFetch();
        } catch (SQLException e) {
            LOG.error(Error.DB_FILTER_BEFORE_JOB, e);
            throw new DataImportException(Error.DB_FILTER_BEFORE_JOB.getDescription(), e);
        }
    }

    private void filterIncidentAfterDataFetch() throws DataImportException {
        LOG.info("Starting job 'filterIncidentAfterDataFetch.'");
        jobStatusResponse.setJobStatus(JobStatus.RunningFilterAfterDataFetch.name());
        try {
            dataImportService.filterIncidentAfterDataFetch();
        } catch (SQLException e) {
            LOG.error(Error.DB_FILTER_AFTER_JOB, e);
            throw new DataImportException(Error.DB_FILTER_AFTER_JOB.getDescription(), e);
        }
    }

    private void importLibias2Cognitec(String jobId) throws DataImportException {
        LOG.info("Starting job 'importLibias2Cognitec' for JobId: " + jobId);
        jobStatusResponse.setJobStatus(JobStatus.RunningLibias2Cognitec.name());
        try {
            dataImportService.importLibias2Cognitec(jobId);
        } catch (SQLException e) {
            LOG.error(Error.DB_LIBIAS_COGNITEC_JOB, e);
            throw new DataImportException(Error.DB_LIBIAS_COGNITEC_JOB.getDescription(), e);
        }
    }

    private void startDbEnrollment(String jobId) throws DataImportException {
        LOG.info("Starting job 'startDbEnrollment' for jobId: " + jobId);
        jobStatusResponse.setJobStatus(JobStatus.RunningDbEnrollment.name());
        try {
            LOG.info("Calling Cognitec WS 'StartDBEnrollment'.");
            cognitecEnrollmentClient.startEnrollment(jobId);
            waitForDBEnrollment();
            waitForSync();
            LOG.info("Calling Cognitec WS 'StartDBIdentification'.");
            cognitecEnrollmentClient.startDbIdentification(jobId);
            waitForDBIdentification();
        } catch (Exception e) {
            LOG.error(Error.COGNITE_ENROLLMENT, e);
            throw new DataImportException(Error.COGNITE_ENROLLMENT.getDescription(), e);
        }
    }

    private void waitForDBEnrollment() {
        LOG.info("Calling Cognitec WS 'WaitForDBEnrollment'.");
        try {
            cognitecEnrollmentClient.waitForDBEnrollment();
        } catch (WebServiceClientException e) {
            Throwable cause = e.getMostSpecificCause();
            if (cause != null && cause instanceof SocketTimeoutException) {
                LOG.error(Error.COGNITEC_ENROLLMENT_TIMEOUT, e);
                waitForDBEnrollment();
            } else {
                throw e;
            }
        }
    }

    private void waitForSync() {
        LOG.info("Calling Cognitec WS 'WaitForSync'.");
        try {
            cognitecEnrollmentClient.waitForSync();
        } catch (WebServiceClientException e) {
            Throwable cause = e.getMostSpecificCause();
            if (cause != null && cause instanceof SocketTimeoutException) {
                LOG.error(Error.COGNITEC_SYNC_TIMEOUT, e);
                waitForSync();
            } else {
                throw e;
            }
        }
    }

    private void waitForDBIdentification() {
        LOG.info("Calling Cognitec WS 'waitForDBIdentification'.");
        try {
            cognitecEnrollmentClient.waitForDBIdentification();
        } catch (WebServiceClientException e) {
            Throwable cause = e.getMostSpecificCause();
            if (cause != null && cause instanceof SocketTimeoutException) {
                LOG.error(Error.COGNITEC_IDENTIFICATION_TIMEOUT, e);
                waitForDBIdentification();
            } else {
                throw e;
            }
        }
    }

    private void importCrossIdentificationResult2Libias(String jobId) throws DataImportException {
        LOG.info("Starting job 'importCrossIdentificationResult2Libias' for jobId: " + jobId);
        jobStatusResponse.setJobStatus(JobStatus.RunningCognitec2Libias.name());
        try {
            dataImportService.importCrossIdentificationResult2Libias(jobId);
        } catch (SQLException e) {
            LOG.error(Error.DB_COGNITEC_LIBIAS_JOB, e);
            throw new DataImportException(Error.DB_COGNITEC_LIBIAS_JOB.getDescription(), e);
        }
    }

    private void resetAfterFetchFilter() throws DataImportException {
        LOG.info("Starting job 'resetAfterDataFetchFilter.'");
        jobStatusResponse.setJobStatus(JobStatus.RunningResetFilterAfterFetch.name());
        try {
            dataImportService.resetAfterDataFetchFilter();
        } catch (SQLException e) {
            LOG.error(Error.DB_RESET_AFTER_FETCH_FILTER_JOB, e);
            throw new DataImportException(Error.DB_RESET_AFTER_FETCH_FILTER_JOB.getDescription(), e);
        }
    }

    private void updateIncidentPersonData(Date pDate) {
        LOG.info("Starting job 'updateIncidentPersonData'");
        jobStatusResponse.setJobStatus(JobStatus.RunningUpdateIncidentApplicantData.name());
        jobStatusResponse.setProgress(0);

        try {
            UpdatedApplicants ua = marisClient.getUpdatedApplicants();
            if (ua == null) {
                return;
            }

            List<UpdatedApplicant> updatedApplicants = ua.getUpdatedApplicants();
            LOG.info("Got " + updatedApplicants.size() + " updated person ids");
            if (updatedApplicants.size() == 0) {
                return;
            }

            List<Future<Long>> toComplete = new ArrayList<Future<Long>>();
            List<Long> completed = new ArrayList<Long>();
            updatedApplicants.forEach(applicant -> {
                toComplete.add(executorService.submit(new ApplicantDataUpdateTask(applicant, pDate, marisClient, incidentService)));
            });

            int lastProgress = 0;
            int currProgress = 0;
            for (Future<Long> oid : toComplete) {
                completed.add(oid.get());
                currProgress = 100 * completed.size() / updatedApplicants.size();
                if (currProgress != lastProgress && currProgress % 10 == 0) {
                    jobStatusResponse.setProgress(currProgress);
                    LOG.info("UpdateIncidentPersonData status : " + currProgress);
                }
                lastProgress = currProgress;
            }
        } catch (Exception ex) {
            LOG.error(Error.MARIS_FETCH, ex);
        }
    }

    private void getNewIncidentPersonData(Date pDate) {
        LOG.info("Starting job 'getNewIncidentPersonData'");
        jobStatusResponse.setJobStatus(JobStatus.RunningFetchNewIncidentApplicantData.name());
        jobStatusResponse.setProgress(0);

        try {
            List<Long> missingPersonDataOids = incidentService.getImageOidsWhereMissingPersonData();
            LOG.info("Got " + missingPersonDataOids.size() + " image ids with missing person data");
            if (missingPersonDataOids.size() == 0) {
                return;
            }

            List<Future<Long>> toComplete = new ArrayList<Future<Long>>();
            List<Long> completed = new ArrayList<Long>();
            missingPersonDataOids.forEach(oid -> {
                toComplete.add(executorService.submit(new ApplicantDataFetchTask(oid, pDate, marisClient, incidentService)));
            });

            int lastProgress = 0;
            int currProgress = 0;
            for (Future<Long> oid : toComplete) {
                completed.add(oid.get());
                currProgress = 100 * completed.size() / missingPersonDataOids.size();
                if (currProgress != lastProgress && currProgress % 10 == 0) {
                    jobStatusResponse.setProgress(currProgress);
                    LOG.info("GetNewIncidentPersonData status : " + currProgress);
                }
                lastProgress = currProgress;
            }
        } catch (Exception ex) {
            LOG.error(Error.MARIS_FETCH, ex);
        }
    }

    private boolean isGesEnabled() {
        return isGesEnabled;
    }

}
