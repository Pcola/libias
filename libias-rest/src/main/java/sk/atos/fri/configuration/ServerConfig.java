package sk.atos.fri.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Server configuration variables mapped to config file
 */
@Configuration
public class ServerConfig {

    @Value("${cognitec.soap.url}")
    private String cognitecSoapUrl;

    @Value("${cognitec.facility}")
    private String cognitecFacility;

    @Value("${cognitec.identificationBinning.priority}")
    private String identificationBinningPriority;

    @Value("${maris.person.url}")
    private String marisPersonUrl;

    @Value("${maris.akte.url}")
    private String marisAkteUrl;

    @Value("${maris.deleted.persons.url}")
    private String marisDeletedPersonsUrl;

    @Value("${maris.deleted.files.url}")
    private String marisDeletedFilesUrl;

    @Value("${maris.locked.files.url}")
    private String marisLockedFilesUrl;

    @Value("${maris.update.url}")
    private String marisUpdateUrl;

    @Value("${maris.token.url}")
    private String marisTokenUrl;

    @Value("${maris.token.username}")
    private String marisTokenUsername;

    @Value("${maris.token.password}")
    private String marisTokenPassword;

    @Value("${maris.bis.service.url:}")
    private String marisImageInfoServiceUrl;

    @Value("${maris.agl.service.url:}")
    private String marisDeletedFilesServiceUrl;

    @Value("${maris.gal.service.url:}")
    private String marisLockedFilesServiceUrl;

    @Value("${maris.pgl.service.url:}")
    private String marisDeletedPersonsServiceUrl;

    @Value("${maris.oauth.service.url:}")
    private String marisOauthServiceUrl;

    @Value("${number.of.threads}")
    private int numberOfThreads;

    @Value("${scheduler.import.runAt}")
    private String schedulerImportRunAt;

    @Value("${scheduler.import.skipDeleteOverWs:false}")
    private boolean schedulerImportSkipDeleteOverWs;

    @Value("${statistics.displayZeroLines:false}")
    private boolean statisticsDisplayZeroLines;

    @Value("${logout.redirect.url}")
    private String logoutRedirectUrl;

    @Value("${logout.redirect.url.intern}")
    private String logoutRedirectUrlIntern;

    @Value("${incident.query.hint:INDEX (INCIDENT INCIDENT_FILTER)}")
    private String incidentQueryHint;

    @Value("${incident.query.useHintForCount:false}")
    private boolean useHintForCount;

    public String getCognitecSoapUrl() {
        return cognitecSoapUrl;
    }

    public String getCognitecFacility() {
        return cognitecFacility;
    }

    public String getIdentificationBinningPriority() {
        return identificationBinningPriority;
    }

    public String getMarisPersonUrl() {
        return marisPersonUrl;
    }

    public String getMarisAkteUrl() {
        return marisAkteUrl;
    }

    public String getMarisDeletedPersonsUrl() {
        return marisDeletedPersonsUrl;
    }

    public String getMarisDeletedFilesUrl() {
        return marisDeletedFilesUrl;
    }

    public String getMarisLockedFilesUrl() {
        return marisLockedFilesUrl;
    }

    public String getMarisUpdateUrl() {
        return marisUpdateUrl;
    }

    public String getMarisTokenUrl() {
        return marisTokenUrl;
    }

    public String getMarisTokenUsername() {
        return marisTokenUsername;
    }

    public String getMarisTokenPassword() {
        return marisTokenPassword;
    }

    public String getMarisImageInfoServiceUrl() {
        return marisImageInfoServiceUrl;
    }

    public String getMarisDeletedFilesServiceUrl() {
        return marisDeletedFilesServiceUrl;
    }

    public String getMarisLockedFilesServiceUrl() {
        return marisLockedFilesServiceUrl;
    }

    public String getMarisDeletedPersonsServiceUrl() {
        return marisDeletedPersonsServiceUrl;
    }

    public String getMarisOauthServiceUrl() {
        return marisOauthServiceUrl;
    }    

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public String getSchedulerImportRunAt() {
        return schedulerImportRunAt;
    }

    public boolean getSchedulerImportSkipDeleteOverWs() {
        return schedulerImportSkipDeleteOverWs;
    }

    public boolean getStatisticsDisplayZeroLines() {
        return statisticsDisplayZeroLines;
    }

    public String getLogoutRedirectUrl() {
        return logoutRedirectUrl;
    }

    public String getLogoutRedirectUrlIntern() {
        return logoutRedirectUrlIntern;
    }

    public String getIncidentQueryHint() {
        return incidentQueryHint;
    }

    public boolean getUseHintForCount() {
        return useHintForCount;
    }

}
