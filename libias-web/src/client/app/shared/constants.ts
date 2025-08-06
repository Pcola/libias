export const CONTENT_TYPE_JSON: string = 'application/json';
export const ACCEPT_JSON: string = 'application/json';
export const ACCEPT_XML: string = 'application/xml';
export const ACCEPT_TEXT_PLAIN: string = 'text/plain';
export const APPLICATION_PDF: string = 'application/pdf';
export const APPLICATION_XLSX: string = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';

export const USER_ROLE_ADMIN_ID = 1;
export const USER_ROLE_SUPERUSER_ID = 2;
export const USER_ROLE_USER_ID = 3;

export const STATUS_ID_OPEN = 1;
export const STATUS_ID_FILES_DOUBLET = 2;
export const STATUS_ID_FILES_NO_DOUBLET = 3;
export const STATUS_ID_NOT_CLEAR = 4;
export const STATUS_ID_NO_PROCESSING = 5;
export const STATUS_ID_FILES_NO_LINK = 6;
export const STATUS_ID_ADJUSTED = 7;
export const STATUS_ID_READY_TO_QA = 8;
export const STATUS_ID_DNUMBER_DIFF = 9;
export const STATUS_ID_AUTO_ADJUSTED = 10;

export const GENDER_W: string = 'W';
export const GENDER_M: string = 'M';
export const GENDER_U: string = 'U';
export const GENDER_X: string = 'X';
export const GENDER_D: string = 'D';
export const GENDER_EMPTY: string = '\xa0';
export const NATIONALITY_EMPTY: string = '\xa0';

export const GROWL_SEVERITY_INFO: string = 'info';
export const GROWL_SEVERITY_WARN: string = 'warn';
export const GROWL_SEVERITY_ERROR: string = 'error';
export const GROWL_SEVERITY_SUCCESS: string = 'success';

export const GROWL_LIFE = 10000;

// Headers HTTP
export const HEADER_X_SECRET: string = 'X-Secret';
export const HEADER_X_TOKEN_ACCESS: string = 'X-TokenAccess';
export const HEADER_X_DIGEST: string = 'X-Digest';
export const HEADER_X_ONCE: string = 'X-Once';
export const HEADER_WWW_AUTHENTICATE: string = 'WWW-Authenticate';
export const HEADER_AUTHENTICATION: string = 'Authentication';
export const CSRF_CLAIM_HEADER: string = 'X-HMAC-CSRF';
export const HEADER_LOGOUT_LINK: string = 'LOGOUT-LINK';

// Local storage keys
export const STORAGE_ACCOUNT_TOKEN: string = 'hmacApp-account';
export const STORAGE_SECURITY_TOKEN: string = 'hmacApp-security';
export const STORAGE_INCIDENT_REQUEST = 'BAM.INCIDENT.REQUEST';
export const STORAGE_INCIDENT_PAGE = 'BAM.INCIDENT.PAGE';
export const STORAGE_INCIDENT_SORT = 'BAM.INCIDENT.SORT';
export const STORAGE_INCIDENT_PKZ = 'BAM.INCIDENT.PKZ';
export const STORAGE_INCIDENT_CASE_ID = 'BAM.INCIDENT.CASE_ID';
export const STORAGE_AUSSENSTELLER_REQUEST = 'BAM.AUSSENSTELLER.REQUEST';
export const STORAGE_AUSSENSTELLER_CASE_ID = 'BAM.AUSSENSTELLER.CASE_ID';
export const STORAGE_AUSSENSTELLER_PAGE = 'BAM.AUSSENSTELLER.PAGE';
export const STORAGE_AUSSENSTELLER_SORT = 'BAM.AUSSENSTELLER.SORT';


// Common http root api
export const BACKEND_API_AUTHENTICATE_PATH: string = '/login';

export const ROLE_USER = 'USER';
export const ROLE_SUPEUSER = 'SUPERUSER';
export const ROLE_ADMIN = 'ADMIN';
export const ROLE_AUSSENSTELLEUSER = 'AUSSENSTELLEUSER';
export const ROLE_COMPARER = 'COMPARER';
export const ROLE_SEARCHER = 'SEARCHER';

export const PAGE_WIDTH = 1700;
