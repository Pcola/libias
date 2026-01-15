--------------------------------------------------------
--  DDL for Procedure FILTER_INCIDENT_CLEARING_CASES
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE FILTER_INCIDENT_CLEARING_CASES AS
  err_code NUMBER;
  err_message VARCHAR2(256);
BEGIN
    UPDATE INCIDENT SET FILTER = 7
    WHERE FILTER = 0 /* AND STATUS_ID = 1 */
    AND (A_ANTRAGSTYP = 'Clearingakten §16a' OR B_ANTRAGSTYP = 'Clearingakten §16a');
    COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    err_code := SQLCODE;
    err_message := SUBSTR(SQLERRM, 1, 256);
    ROLLBACK;
    INSERT INTO LOG(METHOD, SEVERITY, MESSAGE, TIMESTAMP, TYPE)
    VALUES ('FILTER_INCIDENT_CLEARING_CASES', 'ERROR', err_code || ' - ' || err_message, SYSDATE, 5);
    COMMIT;
    RAISE;
END;
/

--------------------------------------------------------
--  DDL for Procedure FILTER_AFTER_DATA_FETCH
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE FILTER_AFTER_DATA_FETCH AS
BEGIN
    FILTER_INCIDENT_MISSING_DATA();
    FILTER_INCIDENT_DELETED_DATA();
    FILTER_INCIDENT_EQUAL_PKZ();
    FILTER_INCIDENT_EQUAL_FILE_NUM();
    FILTER_INCIDENT_PKZ_PAIR_DUPL();
    FILTER_INCIDENT_CLEARING_CASES();
    FILTER_INCIDENT_PROOF_CASES();
END;
/

--------------------------------------------------------
--  DDL for Procedure RESET_FILTER_CLEARING_CASES
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE RESET_FILTER_CLEARING_CASES AS
  err_code NUMBER;
  err_message VARCHAR2(256);
BEGIN
    UPDATE INCIDENT SET FILTER = 0
    WHERE FILTER = 7 /* AND STATUS_ID = 1 */;
    COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    err_code := SQLCODE;
    err_message := SUBSTR(SQLERRM, 1, 256);
    ROLLBACK;
    INSERT INTO LOG(METHOD, SEVERITY, MESSAGE, TIMESTAMP, TYPE)
    VALUES ('RESET_FILTER_CLEARING_CASES', 'ERROR', err_code || ' - ' || err_message, SYSDATE, 5);
    COMMIT;
    RAISE;
END;
/

--------------------------------------------------------
--  DDL for Procedure RESET_FILTER_AFTER_DATA_FETCH
--------------------------------------------------------

CREATE OR REPLACE PROCEDURE RESET_FILTER_AFTER_DATA_FETCH AS
BEGIN
    RESET_FILTER_EQUAL_PKZ();
    RESET_FILTER_EQUAL_FILE_NUM();
    RESET_FILTER_PKZ_PAIR_DUPL();
    RESET_FILTER_CLEARING_CASES();
END;
/
