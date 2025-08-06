package sk.atos.fri.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author kristian
 *
 * Abstract class of all constants using in this project
 */
public abstract class Constants {

  public static final String LANG_EN = "en";
  public static final String LANG_DE = "de";
  public static final String BAMF_LOGO = "images/bamf_logo.png";
  public static final String LIBIAS_LOGO = "images/libias_logo.png";
  public static final String ROLE_USER = "ROLE_USER";
  public static final String ROLE_SUPERUSER = "ROLE_SUPERUSER";
  public static final String ROLE_ADMIN = "ROLE_ADMIN";
  public static final String ROLE_AUSSENSTELLEUSER = "ROLE_AUSSENSTELLEUSER";
  public static final String ROLE_COMPARER = "ROLE_COMPARER";
  public static final String ROLE_SEARCHER = "ROLE_SEARCHER";
  public static final String DEFAULT_VALUE = "--";
  public static final String VALUE_NULL = "NULL";
  public static final String WORKPLACE_NOTE_SYMBOL = "e";
  public static final String NOTE_SYMBOL = "b";
  public static final String HTTP_HEADER_TITLE = "HTTP_TITLE";
  public static final String HTTP_HEADER_LAST_NAME = "HTTP_SN";
  public static final String HTTP_HEADER_FIRST_NAME = "HTTP_GIVENNAME";
  public static final String HTTP_HEADER_IDM_ROLES = "HTTP_GRP";
  public static final String HTTP_HEADER_USERNAME = "HTTP_CN";
  public static final String HTTP_HEADER_DEPARTMENT = "HTTP_DEPARTMENTNUMBER";
  public static final String HEADER_LOGOUT_LINK = "LOGOUT-LINK";
  public static final String IDM_ROLE_AUSSENSTELLENUTZER = "IDMS_LiBiAs_ASNutzer";
  public static final String TEMPLATE_PATH_COMPARER = "SearchReportTemplate.docx";  
  public static final String STANDARD_DATE_FORMAT = "dd.MM.yyyy HH:mm";
  public static final String STANDARD_TIMEZONE = "Europe/Vienna";
  public static final String STANDARD_DATE_FORMAT_COMMA = "dd.MM.yyyy, HH:mm";
  public static final String APPLICATION_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

  public static final String IMAGE_DELETED = "Bild gelöscht";

  public static final Map<String, Integer> REFERENCE_DESCRIPTION = Collections.unmodifiableMap(new HashMap<String, Integer>() {{
    put("Akte mit gleicher D-Nummer", 1);
    put("gleiche Person", 2);
    put("siehe auch", 3);
    put("Erstantrag zu verdecktem Folgeantrag", 4);
    put("Stamm(Ur-)verfahren MFI", 5);
    put("Stamm(Folge-)verfahren MFI", 6);
    put("Mehrfachverfahren", 7);
    put("Verdeckter Folgeantrag", 8);
    put("Posteingang", 9);
    put("Akte", 10);
  }});

  public static enum JobStatus {
    None,
    RunningMaris2Libias,
    RunningLibias2Cognitec,
    RunningDeleted2Libias,
    DeleteCasesFromCognitec,
    RunningDeleted2Cognitec,
    RunningCognitec2Libias,
    RunningDbEnrollment,
    RunningFilterBeforeDataFetch,
    RunningFilterAfterDataFetch,
    RunningResetFilterAfterFetch,
    RunningUpdateLockedFiles,
    RunningUpdateDeletedFiles,
    RunningUpdateDeletedPersons,
    RunningUpdateIncidentApplicantData,
    RunningFetchNewIncidentApplicantData,
    Finished
  };

}
