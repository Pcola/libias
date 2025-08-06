--------------------------------------------------------
--  File created - Monday-March-12-2018   
--  File edited - Wednesday-May-23-2018   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View AUSSENSTELLER_CASE
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "AUSSENSTELLER_CASE" ("CASE_ID", "PKZ_PROBE_ID", "PKZ_GALLERY_ID", "PROBE_ID", "GALLERY_ID", "SCORE", "STATUS_ID", "NOTE", "WORKPLACE_NOTE", "WORKPLACE_ID", "USERNAME", "LAST_NAME", "FIRST_NAME", "NATIONALITY", "AZR_NUMBER", "BIRTH_DATE", "D_NUMBER", "GENDER", "LAST_NAME2", "FIRST_NAME2", "NATIONALITY2", "AZR_NUMBER2", "BIRTH_DATE2", "D_NUMBER2", "GENDER2", "REFERENCE_NUMBER", "FILE_NUMBER", "FILE_NUMBER2") AS 
  SELECT
    case_id,
    pkz_probe_id,
    pkz_gallery_id,
    probe_id,
    gallery_id,
    score,
    status_id,
    note,
    workplace_note,
    workplace_id,
    username,
  FAMILIENNAME AS last_name,
    vorname AS first_name,
    STAATSANGEHOERIGKEIT AS nationality,
    AZRNUMMER AS AZR_NUMBER,
    Geburtsdatum AS birth_date,
    DNUMMER AS D_NUMBER,
    GESCHLECHT AS gender,
    FAMILIENNAME2 AS last_name2,
    vorname2 AS first_name2,
    STAATSANGEHOERIGKEIT2 AS nationality2,
    AZRNUMMER2 AS AZR_NUMBER2,
    Geburtsdatum2 AS birth_date2,
    DNUMMER2 AS D_NUMBER2,
    GESCHLECHT2 AS gender2 ,
    REFERENZBEZEICHNUNG AS REFERENCE_NUMBER,
    AKTENZEICHEN AS FILE_NUMBER,
    AKTENZEICHEN2 AS FILE_NUMBER2
FROM
    (
            SELECT
            cs.caseid AS case_id,
            a.PKZ AS pkz_probe_id,
            a2.PKZ AS pkz_gallery_id,
            cs.ProbeID AS probe_id,
            cs.galleryId AS gallery_id,
            cs.score AS score,
            s.StatusID AS status_id,
            a.GESCHLECHT AS gender,
            cs.BEMERKUNG AS note,
            cs.AUSSENSTELLE_BEMERKUNG AS workplace_note,
            cs.DIENSTSTELLE_ID AS workplace_id,
            u.username as username,
            a.FAMILIENNAME,
            a.vorname,
            a.STAATSANGEHOERIGKEIT,
            a.AZRNUMMER,
            a.Geburtsdatum,
            a.GESCHLECHT,
            a.DNUMMER,
            b.AKTENZEICHEN,
            a2.FAMILIENNAME as FAMILIENNAME2,
            a2.vorname AS vorname2,
            a2.STAATSANGEHOERIGKEIT AS STAATSANGEHOERIGKEIT2,
            a2.AZRNUMMER AS AZRNUMMER2,
            a2.Geburtsdatum AS Geburtsdatum2,
            a2.GESCHLECHT AS GESCHLECHT2,
            a2.DNUMMER AS DNUMMER2,
            r.REFERENZBEZEICHNUNG,
            b2.AKTENZEICHEN AS AKTENZEICHEN2
        FROM
            CrossIdentificationCases cs
        JOIN Bild b ON
            cs.PROBEID = b.OID
        JOIN Bild b2 ON
            cs.GALLERYID = b2.OID
        JOIN Antragsteller a ON
            b.PKZ = a.pkz
        JOIN Antragsteller a2 ON
            b2.PKZ = a2.pkz
        join BAM_USER u   on
            u.DIENSTSTELLE_ID=cs.DIENSTSTELLE_ID
        LEFT OUTER JOIN Status s ON
            s.StatusID = cs.StatusID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.FILTERED = 0

    ) ORDER BY case_id ASC
;
--------------------------------------------------------
--  DDL for View CASE_DETAIL
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "CASE_DETAIL" ("CASE_ID", "PKZ_PROBE_ID", "PKZ_GALLERY_ID", "PROBE_ID", "GALLERY_ID", "SCORE", "STATUS_ID", "GENDER", "NOTE", "WORKPLACE_NOTE", "WORKPLACE_ID", "WORKPLACE_NAME", "FAMILIENNAME", "VORNAME", "STAATSANGEHOERIGKEIT", "AZRNUMMER", "GEBURTSDATUM", "GESCHLECHT", "DNUMMER", "AKTENZEICHEN", "FAMILIENNAME2", "VORNAME2", "STAATSANGEHOERIGKEIT2", "AZRNUMMER2", "GEBURTSDATUM2", "GESCHLECHT2", "DNUMMER2", "REFERENZBEZEICHNUNG", "AKTENZEICHEN2") AS 
  SELECT
            cs.caseid AS case_id,
            a.PKZ AS pkz_probe_id,
            a2.PKZ AS pkz_gallery_id,
            cs.ProbeID AS probe_id,
            cs.galleryId AS gallery_id,
            cs.score AS score,
            s.StatusID AS status_id,
            a.GESCHLECHT AS gender,
            cs.BEMERKUNG AS note,
            cs.AUSSENSTELLE_BEMERKUNG AS workplace_note,
            cs.DIENSTSTELLE_ID AS workplace_id,
            d.DIENSTSTELLE AS workplace_name,
            a.FAMILIENNAME,
            a.vorname,
            a.STAATSANGEHOERIGKEIT,
            a.AZRNUMMER,
            a.Geburtsdatum,
            a.GESCHLECHT,
            a.DNUMMER,
            b.AKTENZEICHEN,
            a2.FAMILIENNAME as FAMILIENNAME2,
            a2.vorname AS vorname2,
            a2.STAATSANGEHOERIGKEIT AS STAATSANGEHOERIGKEIT2,
            a2.AZRNUMMER AS AZRNUMMER2,
            a2.Geburtsdatum AS Geburtsdatum2,
            a2.GESCHLECHT AS GESCHLECHT2,
            a2.DNUMMER AS DNUMMER2,
            r.REFERENZBEZEICHNUNG,
            b2.AKTENZEICHEN AS AKTENZEICHEN2
        FROM
            CrossIdentificationCases cs
        JOIN Bild b ON
            cs.PROBEID = b.OID
        JOIN Bild b2 ON
            cs.GALLERYID = b2.OID
        JOIN Antragsteller a ON
            b.PKZ = a.pkz
        JOIN Antragsteller a2 ON
            b2.PKZ = a2.pkz
        LEFT OUTER JOIN Status s ON
            s.StatusID = cs.StatusID
        LEFT OUTER JOIN DIENSTSTELLE d ON
            cs.DIENSTSTELLE_ID = d.ID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
;
--------------------------------------------------------
--  DDL for View COGNITEC_IMAGES
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "COGNITEC_IMAGES" ("EYERX", "EYERY", "EYELX", "EYELY", "RECORDID") AS 
  SELECT 0, 0, 0, 0, 0 FROM DUAL WHERE DUMMY <> 'X'
;
--------------------------------------------------------
--  DDL for View INCIDENT
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "INCIDENT" ("CASE_ID", "PKZ_PROBEID", "PKZ_GALLERYID", "LAST_NAME", "FIRST_NAME", "NATIONALITY", "AZR_NUMBER", "BIRTH_DATE", "GENDER", "D_NUMBER", "LAST_NAME2", "FIRST_NAME2", "NATIONALITY2", "AZR_NUMBER2", "BIRTH_DATE2", "GENDER2", "D_NUMBER2", "STATUS_ID", "FILE_NUMBER", "FILE_NUMBER2", "FILE_ID", "REFERENCE_NUMBER", "OFFICE", "OFFICE_ID") AS 
  SELECT
            case_id,
            pkz_probeid,
            pkz_galleryid,
            last_name,
            first_name,
            nationality,
            AZR_NUMBER,
            birth_date,
            gender,            
            D_NUMBER,
            last_name2,
            first_name2,
            nationality2,
            AZR_NUMBER2,
            birth_date2,
            gender2,
            D_NUMBER2,
            cic.STATUSID AS STATUS_ID,
            FILE_NUMBER,            
            FILE_NUMBER2,        
            FILE_ID,      
            REFERENCE_NUMBER,
            d.DIENSTSTELLE as office, 
            d.id as office_id
        FROM
            incident_mv imv
            JOIN CROSSIDENTIFICATIONCASES cic ON
                imv.case_id = cic.caseid
            LEFT OUTER JOIN DIENSTSTELLE d ON 
                d.ID = cic.DIENSTSTELLE_ID
;
--------------------------------------------------------
--  DDL for View INCIDENT_PROBEID
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "INCIDENT_PROBEID" ("CASE_ID", "PKZ_PROBEID", "PKZ_GALLERYID", "LAST_NAME", "FIRST_NAME", "NATIONALITY", "AZR_NUMBER", "BIRTH_DATE", "FILE_NUMBER", "GENDER", "STATUS_ID", "D_NUMBER", "FILE_ID", "REFERENCE_NUMBER", "OFFICE", "OFFICE_ID") AS 
  SELECT
        case_id,
        pkz_probeid,
        pkz_galleryid,
        last_name,
        first_name,
        nationality,
        AZR_NUMBER,
        birth_date,
        FILE_NUMBER,
        gender,
        cic.STATUSID AS STATUS_ID,
        D_NUMBER,
        FILE_ID,
        REFERENCE_NUMBER,
        d.DIENSTSTELLE as office, 
        d.id as office_id
        FROM
            incident_mv imv
            JOIN CROSSIDENTIFICATIONCASES cic ON
                imv.case_id = cic.caseid
            LEFT OUTER JOIN DIENSTSTELLE d ON 
                d.ID = cic.DIENSTSTELLE_ID
;
--------------------------------------------------------
--  DDL for Table AKTENREFERENZ
--------------------------------------------------------

  CREATE TABLE  "AKTENREFERENZ" 
   (	"OID" NUMBER(*,0), 
	"AKTENZEICHEN_A" VARCHAR2(32 BYTE), 
	"AKTENZEICHEN_B" VARCHAR2(32 BYTE), 
	"REFERENZBEZEICHNUNG" VARCHAR2(64 BYTE), 
	"DATEMODIFIED" DATE, 
	"PRIORITY" NUMBER(3,0)
   )
;
--------------------------------------------------------
--  DDL for Table ANTRAGSTELLER
--------------------------------------------------------

  CREATE TABLE  "ANTRAGSTELLER" 
   (	"OID" NUMBER(10,0), 
	"PKZ" NUMBER(10,0), 
	"DNUMMER" VARCHAR2(128 BYTE), 
	"ENUMMER" VARCHAR2(128 BYTE), 
	"EURODACNR" VARCHAR2(128 BYTE) DEFAULT NULL, 
	"FAMILIENNAME" VARCHAR2(64 BYTE) DEFAULT NULL, 
	"VORNAME" VARCHAR2(64 BYTE) DEFAULT NULL, 
	"GEBURTSDATUM" DATE DEFAULT NULL, 
	"GEBURTSLAND" VARCHAR2(64 BYTE) DEFAULT NULL, 
	"HERKUNFTSLAND" VARCHAR2(64 BYTE) DEFAULT NULL, 
	"ANTRAGSDATUM" DATE DEFAULT NULL, 
	"ANTRAGSTYP" VARCHAR2(64 BYTE) DEFAULT NULL, 
	"DATE_MODIFIED" DATE DEFAULT NULL, 
	"AUSSENSTELLE" VARCHAR2(64 BYTE), 
	"GESCHLECHT" VARCHAR2(1 BYTE), 
	"JOBID" VARCHAR2(20 BYTE), 
	"IMPORTED" TIMESTAMP (6) DEFAULT sysdate, 
	"STAATSANGEHOERIGKEIT" VARCHAR2(30 CHAR), 
	"AZRNUMMER" VARCHAR2(12 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table BAM_USER
--------------------------------------------------------

  CREATE TABLE  "BAM_USER" 
   (	"USER_ID" NUMBER(10,0), 
	"USERNAME" VARCHAR2(20 BYTE), 
	"PASSWORD" VARCHAR2(100 BYTE), 
	"FIRST_NAME" VARCHAR2(100 BYTE), 
	"LAST_NAME" VARCHAR2(100 BYTE), 
	"ACTIVE" NUMBER(3,0), 
	"PUBLIC_SECRET" VARCHAR2(100 BYTE), 
	"PRIVATE_SECRET" VARCHAR2(100 BYTE), 
	"DIENSTSTELLE_ID" VARCHAR2(32 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table BILD
--------------------------------------------------------

  CREATE TABLE  "BILD" 
   (	"OID" NUMBER(10,0), 
	"ANTRAGSTELLER_OID" NUMBER(10,0), 
	"BILDDATEN" BLOB, 
	"AKTENZEICHEN" VARCHAR2(32 BYTE), 
	"DATE_MODIFIED" DATE, 
	"PKZ" NUMBER(10,0), 
	"JOBID" VARCHAR2(20 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table CROSSIDENTIFICATIONCASES
--------------------------------------------------------

  CREATE TABLE  "CROSSIDENTIFICATIONCASES" 
   (	"CASEID" NUMBER(10,0), 
	"PROBEID" NUMBER(10,0), 
	"SCORE" BINARY_DOUBLE, 
	"RANK" NUMBER(3,0) DEFAULT NULL, 
	"GALLERYID" NUMBER(10,0), 
	"JOBID" VARCHAR2(100 BYTE), 
	"STATUSID" NUMBER(10,0) DEFAULT NULL, 
	"FILTERED" NUMBER(*,0), 
	"AKTENREFERENZID" NUMBER(10,0), 
	"BEMERKUNG" VARCHAR2(1000 BYTE), 
	"DIENSTSTELLE_ID" VARCHAR2(32 BYTE), 
	"AUSSENSTELLE_BEMERKUNG" VARCHAR2(1000 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table DIENSTSTELLE
--------------------------------------------------------

  CREATE TABLE  "DIENSTSTELLE" 
   (	"ID" VARCHAR2(32 BYTE), 
	"DIENSTSTELLE" VARCHAR2(32 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table LOG
--------------------------------------------------------

  CREATE TABLE  "LOG" 
   (	"LOG_ID" NUMBER(19,0), 
	"MESSAGE" VARCHAR2(2000 BYTE), 
	"TIMESTAMP" TIMESTAMP (0) DEFAULT SYSTIMESTAMP, 
	"USER_ID" NUMBER(10,0), 
	"TYPE" NUMBER(10,0)
   )
;
--------------------------------------------------------
--  DDL for Table LOG_TYPE
--------------------------------------------------------

  CREATE TABLE  "LOG_TYPE" 
   (	"TYPE_ID" NUMBER(10,0), 
	"TYPE" VARCHAR2(50 BYTE) DEFAULT NULL
   )
;
--------------------------------------------------------
--  DDL for Table REPORT
--------------------------------------------------------

  CREATE TABLE  "REPORT" 
   (	"ID" NUMBER(10,0), 
	"CASE_ID" NUMBER(10,0), 
	"REPORT" BLOB, 
	"CREATED" TIMESTAMP (6), 
	"CREATED_BY" NUMBER(10,0)
   )
;
--------------------------------------------------------
--  DDL for Table STATUS
--------------------------------------------------------

  CREATE TABLE  "STATUS" 
   (	"STATUSID" NUMBER(10,0), 
	"STATUS" VARCHAR2(30 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table USER_ROLE
--------------------------------------------------------

  CREATE TABLE  "USER_ROLE" 
   (	"ROLE_ID" NUMBER(10,0), 
	"ROLE" VARCHAR2(30 BYTE)
   )
;
--------------------------------------------------------
--  DDL for Table USER_ROLE2BAM_USER
--------------------------------------------------------

  CREATE TABLE  "USER_ROLE2BAM_USER" 
   (	"ROLE_ID" NUMBER, 
	"USER_ID" NUMBER
   )
;
--------------------------------------------------------
--  DDL for Sequence ANTRAGSTELLER_SEQ
--------------------------------------------------------

   CREATE SEQUENCE   "ANTRAGSTELLER_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 10 NOCACHE  NOORDER  NOCYCLE   ;
--------------------------------------------------------
--  DDL for Sequence CIC_SEQ
--------------------------------------------------------

   CREATE SEQUENCE   "CIC_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 2282588 NOCACHE  NOORDER  NOCYCLE   ;
--------------------------------------------------------
--  DDL for Sequence CROSSIDENTIFICATIONCASES_SEQ
--------------------------------------------------------

   CREATE SEQUENCE   "CROSSIDENTIFICATIONCASES_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1150 NOCACHE  NOORDER  NOCYCLE   ;
--------------------------------------------------------
--  DDL for Sequence LOG_SEQ
--------------------------------------------------------

   CREATE SEQUENCE   "LOG_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 35 NOCACHE  NOORDER  NOCYCLE   ;
--------------------------------------------------------
--  DDL for Sequence REPORT_SEQ
--------------------------------------------------------

   CREATE SEQUENCE   "REPORT_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 88 NOCACHE  NOORDER  NOCYCLE   ;
--------------------------------------------------------
--  DDL for Sequence USER_SEQ
--------------------------------------------------------

   CREATE SEQUENCE   "USER_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 50 NOCACHE  NOORDER  NOCYCLE   ;
--------------------------------------------------------

--------------------------------------------------------
--  DDL for Index ANTRAGSTELLER_DNUMMER
--------------------------------------------------------

  CREATE INDEX  "ANTRAGSTELLER_DNUMMER" ON  "ANTRAGSTELLER" ("DNUMMER") ;
--------------------------------------------------------
--  DDL for Index CIC_AKTENREFID
--------------------------------------------------------

  CREATE INDEX  "CIC_AKTENREFID" ON  "CROSSIDENTIFICATIONCASES" ("AKTENREFERENZID") ;
--------------------------------------------------------
--  DDL for Index CROSSIDENTIFICATIONCASES_PID
--------------------------------------------------------

  CREATE INDEX  "CROSSIDENTIFICATIONCASES_PID" ON  "CROSSIDENTIFICATIONCASES" ("PROBEID") ;
--------------------------------------------------------
--  DDL for Index BILD_AKTENZEICHEN
--------------------------------------------------------

  CREATE INDEX  "BILD_AKTENZEICHEN" ON  "BILD" ("AKTENZEICHEN") ;
--------------------------------------------------------
--  DDL for Index REPORT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX  "REPORT_PK" ON  "REPORT" ("ID") ;
--------------------------------------------------------
--  DDL for Index AKTENREFERENZ_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX  "AKTENREFERENZ_PK" ON  "AKTENREFERENZ" ("OID") ;
--------------------------------------------------------
--  DDL for Index BILD_OID
--------------------------------------------------------

  CREATE UNIQUE INDEX  "BILD_OID" ON  "BILD" ("OID") ;
--------------------------------------------------------
--  DDL for Index AKTENREFERENZ_AKZB
--------------------------------------------------------

  CREATE INDEX  "AKTENREFERENZ_AKZB" ON  "AKTENREFERENZ" ("AKTENZEICHEN_B") ;
--------------------------------------------------------
--  DDL for Index AKTENREFERENZ_AKZA
--------------------------------------------------------

  CREATE INDEX  "AKTENREFERENZ_AKZA" ON  "AKTENREFERENZ" ("AKTENZEICHEN_A") ;
--------------------------------------------------------
--  DDL for Index ANTRAGSTELLER_PKZ
--------------------------------------------------------

  CREATE INDEX  "ANTRAGSTELLER_PKZ" ON  "ANTRAGSTELLER" ("PKZ") ;
--------------------------------------------------------
--  DDL for Index DIENSTSTELLE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX  "DIENSTSTELLE_PK" ON  "DIENSTSTELLE" ("ID") ;
--------------------------------------------------------
--  DDL for Index CROSSIDENTIFICATIONCASES_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX  "CROSSIDENTIFICATIONCASES_PK" ON  "CROSSIDENTIFICATIONCASES" ("CASEID") ;
--------------------------------------------------------
--  DDL for Index ANTRAGSTELLER_OID
--------------------------------------------------------

  CREATE UNIQUE INDEX  "ANTRAGSTELLER_OID" ON  "ANTRAGSTELLER" ("OID") ;
--------------------------------------------------------
--  DDL for Index CROSSIDENTIFICATIONCASES_SID
--------------------------------------------------------

  CREATE INDEX  "CROSSIDENTIFICATIONCASES_SID" ON  "CROSSIDENTIFICATIONCASES" ("STATUSID") ;
--------------------------------------------------------
--  DDL for Index CROSSIDENTIFICATIONCASES_GID
--------------------------------------------------------

  CREATE INDEX  "CROSSIDENTIFICATIONCASES_GID" ON  "CROSSIDENTIFICATIONCASES" ("GALLERYID") ;
--------------------------------------------------------
--  DDL for Index USERNAME
--------------------------------------------------------

  CREATE UNIQUE INDEX  "USERNAME" ON  "BAM_USER" ("USERNAME") ;
--------------------------------------------------------
--  DDL for Index CIC_FILTERED
--------------------------------------------------------

  CREATE INDEX  "CIC_FILTERED" ON  "CROSSIDENTIFICATIONCASES" ("FILTERED") ;
--------------------------------------------------------
--  DDL for Index STATUS_ID
--------------------------------------------------------

  CREATE INDEX  "STATUS_ID" ON  "STATUS" ("STATUSID") ;
--------------------------------------------------------
--  DDL for Index BILD_PKZ
--------------------------------------------------------

  CREATE INDEX  "BILD_PKZ" ON  "BILD" ("PKZ") ;
--------------------------------------------------------
--  DDL for Trigger ANTRAGSTELLER_SEQ_TR
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER  "ANTRAGSTELLER_SEQ_TR" 
 BEFORE INSERT ON  Antragsteller FOR EACH ROW
      WHEN (NEW.OID IS NULL) BEGIN
 SELECT Antragsteller_seq.NEXTVAL INTO :NEW.OID FROM DUAL;
END;

/
ALTER TRIGGER  "ANTRAGSTELLER_SEQ_TR" ENABLE;
--------------------------------------------------------
--  DDL for Trigger CIC_SEQ_TR
--------------------------------------------------------

  CREATE OR REPLACE   TRIGGER  "CIC_SEQ_TR" 
 BEFORE INSERT ON  CROSSIDENTIFICATIONCASES FOR EACH ROW
      WHEN (NEW.CASEID IS NULL) BEGIN
 SELECT CIC_seq.NEXTVAL INTO :NEW.CASEID FROM DUAL;
END;

/
ALTER TRIGGER  "CIC_SEQ_TR" ENABLE;
--------------------------------------------------------
--  DDL for Procedure IMPORT_COGNITEC_TO_LIBIAS
--------------------------------------------------------

  CREATE OR REPLACE   PROCEDURE  "IMPORT_COGNITEC_TO_LIBIAS" (pJOB_ID IN VARCHAR) AS
BEGIN
  NULL;
END;

/
--------------------------------------------------------
--  DDL for Procedure IMPORT_LIBIAS_TO_COGNITEC
--------------------------------------------------------

  CREATE OR REPLACE   PROCEDURE  "IMPORT_LIBIAS_TO_COGNITEC" (JOB_ID IN VARCHAR) AS
BEGIN
  NULL;
END;

/
--------------------------------------------------------
--  DDL for Procedure IMPORT_MARIS_TO_AKTENREFERENZ
--------------------------------------------------------

  CREATE OR REPLACE   PROCEDURE  "IMPORT_MARIS_TO_AKTENREFERENZ" AS
BEGIN
  NULL;
END;

/
--------------------------------------------------------
--  DDL for Procedure IMPORT_MARIS_TO_LIBIAS
--------------------------------------------------------

  CREATE OR REPLACE   PROCEDURE  "IMPORT_MARIS_TO_LIBIAS" AS
BEGIN
  NULL;
END;

/
--------------------------------------------------------
--  Constraints for Table DIENSTSTELLE
--------------------------------------------------------

  ALTER TABLE  "DIENSTSTELLE" ADD CONSTRAINT "DIENSTSTELLE_PK" PRIMARY KEY ("ID")
  USING INDEX;
--------------------------------------------------------
--  Constraints for Table LOG
--------------------------------------------------------

  ALTER TABLE  "LOG" ADD PRIMARY KEY ("LOG_ID")
  USING INDEX;
  ALTER TABLE  "LOG" ADD CHECK (type > 0) ENABLE;
  ALTER TABLE  "LOG" ADD CHECK (user_id > 0) ENABLE;
  ALTER TABLE  "LOG" MODIFY ("TYPE" NOT NULL ENABLE);
  ALTER TABLE  "LOG" MODIFY ("USER_ID" NOT NULL ENABLE);
  ALTER TABLE  "LOG" MODIFY ("TIMESTAMP" NOT NULL ENABLE);
  ALTER TABLE  "LOG" MODIFY ("MESSAGE" NOT NULL ENABLE);
  ALTER TABLE  "LOG" MODIFY ("LOG_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table USER_ROLE
--------------------------------------------------------

  ALTER TABLE  "USER_ROLE" ADD PRIMARY KEY ("ROLE_ID")
  USING INDEX;
  ALTER TABLE  "USER_ROLE" ADD CHECK ("ROLE_ID">0) ENABLE;
  ALTER TABLE  "USER_ROLE" MODIFY ("ROLE" NOT NULL ENABLE);
  ALTER TABLE  "USER_ROLE" MODIFY ("ROLE_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table CROSSIDENTIFICATIONCASES
--------------------------------------------------------

  ALTER TABLE  "CROSSIDENTIFICATIONCASES" MODIFY ("JOBID" NOT NULL ENABLE);
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" MODIFY ("GALLERYID" NOT NULL ENABLE);
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CROSSIDENTIFICATIONCASES_PK" PRIMARY KEY ("CASEID")
  USING INDEX;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CHECK (StatusID > 0) ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CHECK (Rank > 0) ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CHECK (CaseID > 0) ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" MODIFY ("SCORE" NOT NULL ENABLE);
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" MODIFY ("PROBEID" NOT NULL ENABLE);
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" MODIFY ("CASEID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table BILD
--------------------------------------------------------

  ALTER TABLE  "BILD" ADD CONSTRAINT "BILD_PK" PRIMARY KEY ("OID")
  USING INDEX;
  ALTER TABLE  "BILD" MODIFY ("OID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table LOG_TYPE
--------------------------------------------------------

  ALTER TABLE  "LOG_TYPE" ADD PRIMARY KEY ("TYPE_ID")
  USING INDEX;
  ALTER TABLE  "LOG_TYPE" ADD CHECK (type_id > 0) ENABLE;
  ALTER TABLE  "LOG_TYPE" MODIFY ("TYPE_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table USER_ROLE2BAM_USER
--------------------------------------------------------

  ALTER TABLE  "USER_ROLE2BAM_USER" MODIFY ("USER_ID" NOT NULL ENABLE);
  ALTER TABLE  "USER_ROLE2BAM_USER" MODIFY ("ROLE_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table REPORT
--------------------------------------------------------

  ALTER TABLE  "REPORT" ADD CONSTRAINT "REPORT_PK" PRIMARY KEY ("ID")
  USING INDEX;
--------------------------------------------------------
--  Constraints for Table STATUS
--------------------------------------------------------

  ALTER TABLE  "STATUS" ADD PRIMARY KEY ("STATUSID")
  USING INDEX;
  ALTER TABLE  "STATUS" ADD CHECK (StatusID > 0) ENABLE;
  ALTER TABLE  "STATUS" MODIFY ("STATUS" NOT NULL ENABLE);
  ALTER TABLE  "STATUS" MODIFY ("STATUSID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table AKTENREFERENZ
--------------------------------------------------------

  ALTER TABLE  "AKTENREFERENZ" ADD CONSTRAINT "AKTENREFERENZ_PK" PRIMARY KEY ("OID")
  USING INDEX;
  ALTER TABLE  "AKTENREFERENZ" MODIFY ("OID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table BAM_USER
--------------------------------------------------------

  ALTER TABLE  "BAM_USER" ADD CONSTRAINT "USERNAME" UNIQUE ("USERNAME")
  USING INDEX;
  ALTER TABLE  "BAM_USER" ADD PRIMARY KEY ("USER_ID")
  USING INDEX;
  ALTER TABLE  "BAM_USER" ADD CHECK ("USER_ID">0) ENABLE;
  ALTER TABLE  "BAM_USER" MODIFY ("USER_ID" NOT NULL ENABLE);
  ALTER TABLE  "BAM_USER" MODIFY ("ACTIVE" NOT NULL ENABLE);
  ALTER TABLE  "BAM_USER" MODIFY ("LAST_NAME" NOT NULL ENABLE);
  ALTER TABLE  "BAM_USER" MODIFY ("FIRST_NAME" NOT NULL ENABLE);
  ALTER TABLE  "BAM_USER" MODIFY ("PASSWORD" NOT NULL ENABLE);
  ALTER TABLE  "BAM_USER" MODIFY ("USERNAME" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ANTRAGSTELLER
--------------------------------------------------------

  ALTER TABLE  "ANTRAGSTELLER" ADD CONSTRAINT "ANTRAGSTELLER_PK" PRIMARY KEY ("OID")
  USING INDEX;
  ALTER TABLE  "ANTRAGSTELLER" ADD CHECK (OID > 0) ENABLE;
  ALTER TABLE  "ANTRAGSTELLER" MODIFY ("OID" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table BAM_USER
--------------------------------------------------------

  ALTER TABLE  "BAM_USER" ADD CONSTRAINT "BAM_USER_FK1" FOREIGN KEY ("DIENSTSTELLE_ID")
	  REFERENCES  "DIENSTSTELLE" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table BILD
--------------------------------------------------------

  ALTER TABLE  "BILD" ADD CONSTRAINT "BILD_AS_OID_FK" FOREIGN KEY ("ANTRAGSTELLER_OID")
	  REFERENCES  "ANTRAGSTELLER" ("OID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table CROSSIDENTIFICATIONCASES
--------------------------------------------------------

  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CICASES_AKTENREFERENZ_FK" FOREIGN KEY ("AKTENREFERENZID")
	  REFERENCES  "AKTENREFERENZ" ("OID") ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CICASES_GALLERYID_BILD_FK" FOREIGN KEY ("GALLERYID")
	  REFERENCES  "BILD" ("OID") ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CICASES_PROBEID_BILD_FK" FOREIGN KEY ("PROBEID")
	  REFERENCES  "BILD" ("OID") ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CICASES_STATUS_FK" FOREIGN KEY ("STATUSID")
	  REFERENCES  "STATUS" ("STATUSID") ENABLE;
  ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CROSSIDENTIFICATIONCASES_FK1" FOREIGN KEY ("DIENSTSTELLE_ID")
	  REFERENCES  "DIENSTSTELLE" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table LOG
--------------------------------------------------------

  ALTER TABLE  "LOG" ADD CONSTRAINT "LOGS_LOGTYPE_FK" FOREIGN KEY ("TYPE")
	  REFERENCES  "LOG_TYPE" ("TYPE_ID") ENABLE;
  ALTER TABLE  "LOG" ADD CONSTRAINT "LOGS_USER_FK" FOREIGN KEY ("USER_ID")
	  REFERENCES  "BAM_USER" ("USER_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table REPORT
--------------------------------------------------------

  ALTER TABLE  "REPORT" ADD CONSTRAINT "REPORT_BAM_USER_FK" FOREIGN KEY ("CREATED_BY")
	  REFERENCES  "BAM_USER" ("USER_ID") ENABLE;
  ALTER TABLE  "REPORT" ADD CONSTRAINT "REPORT_CICASES_FK" FOREIGN KEY ("CASE_ID")
	  REFERENCES  "CROSSIDENTIFICATIONCASES" ("CASEID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table USER_ROLE2BAM_USER
--------------------------------------------------------

  ALTER TABLE  "USER_ROLE2BAM_USER" ADD CONSTRAINT "USER_ROLE2BAM_USER_FK1" FOREIGN KEY ("ROLE_ID")
	  REFERENCES  "USER_ROLE" ("ROLE_ID") ENABLE;
  ALTER TABLE  "USER_ROLE2BAM_USER" ADD CONSTRAINT "USER_ROLE2BAM_USER_FK2" FOREIGN KEY ("USER_ID")
	  REFERENCES  "BAM_USER" ("USER_ID") ENABLE;
