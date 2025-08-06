package sk.atos.fri.log;

/**
 *
 * @author Jaroslav Kollar
 */
public enum Error {

  LIST_INCIDENTS(100, "Cannot list incidents."),
  GET_INCIDENT(101, "Cannot get incident by id."),
  UPDATE_INCIDENT(102, "Cannot update incident."),
  GET_CASE_DETAIL(103, "Cannot get case detail."),
  GET_SITE_STATISTICS(104, "Cannot get site statistics."),
  GET_SITE_RELATED_CASES(105, "Cannot get site related cases"),
  GET_STATISTICS(106, "Cannot get statistics."),
  FINISH_CASE(107, "Cannot finish case."),
  GET_AUSSENSTELLER(108, "Cannot get aussensteller cases."),
  LIST_REFERENCE_NUMBER(109, "Cannot list ReferenceNumber."),
  GEN_XIC_FILE(110, "Error generate XIC file."),
  UPDATE_CASE(111, "Cannot update case."),
  CREATE_REPORT(122, "Error creating new report."),
  DIGEST_NOT_MATCHING(112, "Digest are not matching!"),
  CHECKING_HMAC(113, "Error while checking HMAC token."),
  READ_FILE_I18M(114, "Error reading i18n file."),
  GET_COGNITEC_IMAGE(115, "Cannot get cognitec image."),
  LIST_COUNTRIES_ORIGIN(116, "Cannot list countries origin."),
  LIST_COUNTRIES_BIRTH(117, "Cannot list countries birth."),
  DATA_IMPORT(118, "Error data import."),
  DB_MARIS_JOB(119, "Error calling DB job 'maris2libias'."),
  DB_LIBIAS_COGNITEC_JOB(120, "Error calling DB job 'libias2cognitec'."),
  DB_COGNITEC_LIBIAS_JOB(121, "Error calling DB job 'cognitec2libias'."),
  COGNITE_ENROLLMENT(123, "Error calling job 'startDbEnrollment'."),
  COGNITEC_ENROLLMENT_TIMEOUT(124, "Cognitec's call for 'waitForDBEnrollment' has timed out. Retry..."),
  COGNITEC_SYNC_TIMEOUT(125, "Cognitec's call for 'waitForSync' has timed out. Retry..."),
  COGNITEC_IDENTIFICATION_TIMEOUT(126, "Error calling DB job 'cognitec2libias'."),
  GET_IMAGE(127, "Cannot get image for specified oid."),
  GET_IMAGE_INFO(128, "Cannot get image info."),
  GET_ANTRAGSTELLER(129, "Cannot get Antragsteller for pkz."),
  LIST_NATIONALITIES(130, "Cannot list nationalities."),
  CREATING_REPORT(131, "Error creating new report."),
  DOWNLOAD_REPORT(132, "Error downloading report"),
  GET_REPORT(133, "Error getting report id"),
  GET_USER(134, "Cannot get user for username."),
  GET_USER_INFO(135, "Cannot get logged user info."),
  LIST_USERS(136, "Cannot list users."),
  CREATE_NEW_USER(137, "Cannot create new user."),
  UPDATE_USER(138, "Cannot update user"),
  CHANGE_PASSWORD(139, "Cannot change password."),
  LIST_DIENSTELLE(140, "Cannot list dienststelle records."),
  VERIFY_JWT(141, "Error verifying JWT."),
  GENERATE_SECRET(142, "Cannot generate secret. Cannot encode base 64."),
  GENERATE_JWT(143, "Cannot generate JWT."),
  WRITE_DB(144, "Cannot write to DB"),
  GET_MARIS_BILD(145, "Cannot obtain person for bild from maris."),
  GET_MARIS_AKTE(146, "Cannot obtain aktenreferenz from maris."),
  GET_MARIS_UPDATE(147, "Cannot obtain updated applicants from maris."),
  GET_MARIS_TOKEN(148, "Cannot obtain token."),
  DB_FILTER_BEFORE_JOB(149, "Error calling DB job 'filterBeforeDataFetch'."),
  DB_FILTER_AFTER_JOB(150, "Error calling DB job 'filterAfterDataFetch'."),
  DB_RESET_AFTER_FETCH_FILTER_JOB(151, "Error calling DB job 'resetAfterDataFetchFilter'"),
  UPDATE_AKTENREFERENZRELATION(152, "Error updating Akten Referenz Relation."),
  MARIS_FETCH(153, "Cannot obtain data from maris"),
  GET_MARIS_DELETED_PERSONS(155, "Cannot obtain deleted persons from maris"),
  GET_MARIS_DELETED_FILES(156, "Cannot obtain deleted files from maris"),
  EXPORT_CANNOT_READ_TEMPLATE(166, "Error occurred when reading word template"),
  EXPORT_CANNOT_CREATE_FORM(167, "Error occurred when creating word form"),
  EXPORT_CANNOT_READ_IMAGE_SIZE(168, "Cannot read image size when creating export"),
  EXPORT_CANNOT_ADD_IMAGE(169, "Cannot add image to export"),
  GET_NOTIFICATIONS(170, "Cannot read notifications"),
  GET_MARIS_LOCKED_FILES(171, "Cannot obtain locked files from maris"),
  LIST_PRIORITIES(172, "Cannot list priorities"),
  DUMMY_ERROR_CODE(999, "Dummy error code");

  private final int code;
  private final String description;

  private Error(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public String getDescription() {
     return description;
  }

  public int getCode() {
     return code;
  }

  @Override
  public String toString() {
    return code + ": " + description;
  }
}
