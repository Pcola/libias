ALTER TABLE BAM_USER
ADD (AUSSENSTELLE_ID VARCHAR2(32) );

ALTER TABLE BAM_USER
ADD CONSTRAINT BAM_USER_FK1 FOREIGN KEY
(
  AUSSENSTELLE_ID
)
REFERENCES DIENSTSTELLE
(
  ID
)
ENABLE;

ALTER TABLE CROSSIDENTIFICATIONCASES
ADD (AUSSENSTELLE_BEMERKUNG VARCHAR2(1000) );

CREATE TABLE USER_ROLE2BAM_USER
(
  ROLE_ID NUMBER
, USER_ID NUMBER
);

ALTER TABLE USER_ROLE2BAM_USER
MODIFY (ROLE_ID NOT NULL);

ALTER TABLE USER_ROLE2BAM_USER
MODIFY (USER_ID NOT NULL);

ALTER TABLE USER_ROLE2BAM_USER
ADD CONSTRAINT USER_ROLE2BAM_USER_FK1 FOREIGN KEY
(
  ROLE_ID
)
REFERENCES USER_ROLE
(
  ROLE_ID
)
ENABLE;

ALTER TABLE USER_ROLE2BAM_USER
ADD CONSTRAINT USER_ROLE2BAM_USER_FK2 FOREIGN KEY
(
  USER_ID
)
REFERENCES BAM_USER
(
  USER_ID
)
ENABLE;

ALTER TABLE BAM_USER DROP COLUMN ROLE;

DROP TRIGGER USER_ROLE_SEQ_TR;
INSERT INTO USER_ROLE (ROLE_ID, ROLE) VALUES ('4', 'AUSSENSTELLEUSER');
INSERT INTO USER_ROLE (ROLE_ID, ROLE) VALUES ('5', 'COMPARER');
INSERT INTO USER_ROLE (ROLE_ID, ROLE) VALUES ('6', 'SEARCHER');


INSERT INTO "STATUS" (STATUSID, STATUS) VALUES ('8', 'READY_TO_QA');
INSERT INTO "STATUS" (STATUSID, STATUS) VALUES ('9', 'SOLVED');

ALTER TABLE CROSSIDENTIFICATIONCASES RENAME COLUMN DIENSTSTELLE TO DIENSTSTELLE_ID;
ALTER TABLE CROSSIDENTIFICATIONCASES
ADD CONSTRAINT CROSSIDENTIFICATIONCASES_FK1 FOREIGN KEY
(
  DIENSTSTELLE_ID
)
REFERENCES DIENSTSTELLE
(
  ID
)
ENABLE;


  CREATE OR REPLACE FORCE VIEW "INCIDENT_MATCH" ("ROW_ID", "CASE_ID", "PKZ", "PROBE_ID", "GALLERY_ID", "LAST_NAME", "FIRST_NAME", "BIRTH_DATE", "COUNTRY_BIRTH", "COUNTRY_ORIGIN", "APPLICATION_TYPE", "APPLICATION_DATE", "PLACE_ISSUE", "DNUMBER", "ENUMBER", "EURODAC_NR", "FILE_NUMBER", "SCORE", "STATUS_ID", "GENDER", "NOTE", "WORKPLACE_NOTE", "REFERENCE_NUMBER", "WORKPLACE_ID") AS
  SELECT
    ROW_NUMBER() OVER(ORDER BY pkz ASC) AS row_id,
    case_id,
    PKZ,
    probe_id,
    gallery_id,
    last_name,
    first_name,
    birth_date,
    country_birth,
    country_origin,
    application_type,
    application_date,
    place_issue,
    dnumber,
    enumber,
    eurodac_nr,
    file_number,
    score,
    status_id,
    gender,
    note,
    workplace_note,
    reference_number,
    workplace_id
FROM
    (
            SELECT
            a2.PKZ AS pkz,
            cs.caseid AS case_id,
            cs.ProbeID AS probe_id,
            cs.galleryId AS gallery_id,
            a.FAMILIENNAME AS last_name,
            a.vorname AS first_name,
            a.Geburtsdatum AS birth_date,
            a.Geburtsland AS country_birth,
            a.HERKUNFTSLAND AS country_origin,
            a.Antragstyp AS application_type,
            a.ANTRAGSDATUM AS application_date,
            a.AUSSENSTELLE AS place_issue,
            a.DNUMMER AS dnumber,
            a.ENUMMER AS enumber,
            a.EURODACNR AS eurodac_nr,
            b.AKTENZEICHEN AS file_number,
            cs.score AS score,
            s.StatusID AS status_id,
            a.GESCHLECHT AS gender,
            cs.BEMERKUNG AS note,
            cs.AUSSENSTELLE_BEMERKUNG AS workplace_note,
            r.REFERENZBEZEICHNUNG AS reference_number,
            cs.DIENSTSTELLE_ID AS workplace_id
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
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.FILTERED = 0
        UNION
            SELECT
            a.PKZ AS pkz,
            cs.caseid AS case_id,
            cs.ProbeID AS probe_id,
            cs.galleryId AS gallery_id,
            a2.FAMILIENNAME AS last_name,
            a2.vorname AS first_name,
            a2.Geburtsdatum AS birth_date,
            a2.Geburtsland AS country_birth,
            a2.HERKUNFTSLAND AS country_origin,
            a2.Antragstyp AS application_type,
            a2.ANTRAGSDATUM AS application_date,
            a2.AUSSENSTELLE AS place_issue,
            a2.DNUMMER AS dnumber,
            a2.ENUMMER AS enumber,
            a2.EURODACNR AS eurodac_nr,
            b2.AKTENZEICHEN AS file_number,
            cs.score AS score,
            s.StatusID AS status_id,
            a2.GESCHLECHT AS gender,
            cs.BEMERKUNG AS note,
            cs.AUSSENSTELLE_BEMERKUNG AS workplace_note,
            r.REFERENZBEZEICHNUNG AS reference_number,
            cs.DIENSTSTELLE_ID AS workplace_id
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
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.FILTERED = 0

    ) ORDER BY row_id ASC;





  CREATE OR REPLACE FORCE VIEW "AUSSENSTELLER_CASE" ("CASE_ID", "PKZ_PROBE_ID", "PKZ_GALLERY_ID", "PROBE_ID", "GALLERY_ID", "SCORE", "STATUS_ID", "NOTE", "WORKPLACE_NOTE", "WORKPLACE_ID", "USERNAME") AS
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
    username
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
            u.username as username
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
        WHERE
            cs.FILTERED = 0

    ) ORDER BY case_id ASC;

