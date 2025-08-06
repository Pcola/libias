--------------------------------------------------------
--  NEW COLUMN PRIORITY ID
--------------------------------------------------------

ALTER TABLE CROSSIDENTIFICATIONCASES ADD ("PRIORITYID" NUMBER(10,0) DEFAULT NULL);

UPDATE CROSSIDENTIFICATIONCASES SET PRIORITYID = '5';

--------------------------------------------------------
--  NEW TABLE PRIORITY
--------------------------------------------------------

CREATE TABLE  "PRIORITY" 
   (	"PRIORITYID" NUMBER(10,0), 
		"PRIORITY" VARCHAR2(30 BYTE)
   )
;

Insert into PRIORITY (PRIORITYID,PRIORITY) values (1,'PRIORITY 1');
Insert into PRIORITY (PRIORITYID,PRIORITY) values (2,'PRIORITY 2');
Insert into PRIORITY (PRIORITYID,PRIORITY) values (3,'PRIORITY 3');
Insert into PRIORITY (PRIORITYID,PRIORITY) values (4,'PRIORITY 4');
Insert into PRIORITY (PRIORITYID,PRIORITY) values (5,'PRIORITY 5');

--------------------------------------------------------
--  NEW INDEXES
--------------------------------------------------------
CREATE INDEX  "PRIORITY_ID" ON  "PRIORITY" ("PRIORITYID") ;

CREATE INDEX  "CROSSIDENTIFICATIONCASES_PRID" ON  "CROSSIDENTIFICATIONCASES" ("PRIORITYID") ;


--------------------------------------------------------
--  NEW CONSTRAINTS
--------------------------------------------------------

	--------------------------------------------------------
	--  Constraints for Table PRIORITY
	--------------------------------------------------------

	  ALTER TABLE  "PRIORITY" ADD PRIMARY KEY ("PRIORITYID")
	  USING INDEX;
	  ALTER TABLE  "PRIORITY" ADD CHECK (PriorityID > 0) ENABLE;
	  ALTER TABLE  "PRIORITY" MODIFY ("PRIORITY" NOT NULL ENABLE);
	  ALTER TABLE  "PRIORITY" MODIFY ("PRIORITYID" NOT NULL ENABLE);
  
	--------------------------------------------------------
	--  Constraints for Table CROSSIDENTIFICATIONCASES
	--------------------------------------------------------
		  
	ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CONSTRAINT "CICASES_PRIORITIES_FK" FOREIGN KEY ("PRIORITYID")
		  REFERENCES  "PRIORITY" ("PRIORITYID") ENABLE;
		  
	ALTER TABLE  "CROSSIDENTIFICATIONCASES" ADD CHECK (PriorityID > 0) ENABLE;
	  
--------------------------------------------------------
--  DDL for View AUSSENSTELLER_CASE
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "AUSSENSTELLER_CASE" ("CASE_ID", "PKZ_PROBE_ID", "PKZ_GALLERY_ID", "PROBE_ID", "GALLERY_ID", "SCORE", "STATUS_ID", "PRIORITY_ID", "NOTE", "WORKPLACE_NOTE", "WORKPLACE_ID", "USERNAME", "LAST_NAME", "FIRST_NAME", "NATIONALITY", "AZR_NUMBER", "BIRTH_DATE", "D_NUMBER", "GENDER", "LAST_NAME2", "FIRST_NAME2", "NATIONALITY2", "AZR_NUMBER2", "BIRTH_DATE2", "D_NUMBER2", "GENDER2", "REFERENCE_NUMBER", "FILE_NUMBER", "FILE_NUMBER2") AS 
  SELECT
    case_id,
    pkz_probe_id,
    pkz_gallery_id,
    probe_id,
    gallery_id,
    score,
    status_id,
	priority_id,
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
			p.PriorityID as priority_id,
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
		LEFT OUTER JOIN Priority p ON
            p.PriorityID = cs.PriorityID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.FILTERED = 0
    ) ORDER BY case_id ASC
;

--------------------------------------------------------
--  DDL for View CASE_DETAIL
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "CASE_DETAIL" ("CASE_ID", "PKZ_PROBE_ID", "PKZ_GALLERY_ID", "PROBE_ID", "GALLERY_ID", "SCORE", "STATUS_ID", "PRIORITY_ID", "GENDER", "NOTE", "WORKPLACE_NOTE", "WORKPLACE_ID", "WORKPLACE_NAME", "FAMILIENNAME", "VORNAME", "STAATSANGEHOERIGKEIT", "AZRNUMMER", "GEBURTSDATUM", "GESCHLECHT", "DNUMMER", "AKTENZEICHEN", "FAMILIENNAME2", "VORNAME2", "STAATSANGEHOERIGKEIT2", "AZRNUMMER2", "GEBURTSDATUM2", "GESCHLECHT2", "DNUMMER2", "REFERENZBEZEICHNUNG", "AKTENZEICHEN2") AS 
  SELECT
            cs.caseid AS case_id,
            a.PKZ AS pkz_probe_id,
            a2.PKZ AS pkz_gallery_id,
            cs.ProbeID AS probe_id,
            cs.galleryId AS gallery_id,
            cs.score AS score,
            s.StatusID AS status_id,
			p.PriorityID AS priority_id,
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
		LEFT OUTER JOIN Priority p ON
            p.PriorityID = cs.PriorityID
        LEFT OUTER JOIN DIENSTSTELLE d ON
            cs.DIENSTSTELLE_ID = d.ID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
;

--------------------------------------------------------
--  DDL for View INCIDENT
--------------------------------------------------------

  CREATE OR REPLACE FORCE   VIEW  "INCIDENT" ("CASE_ID", "PKZ_PROBEID", "PKZ_GALLERYID", "LAST_NAME", "FIRST_NAME", "NATIONALITY", "AZR_NUMBER", "BIRTH_DATE", "GENDER", "D_NUMBER", "LAST_NAME2", "FIRST_NAME2", "NATIONALITY2", "AZR_NUMBER2", "BIRTH_DATE2", "GENDER2", "D_NUMBER2", "STATUS_ID", "PRIORITY_ID", "FILE_NUMBER", "FILE_NUMBER2", "FILE_ID", "REFERENCE_NUMBER", "OFFICE", "OFFICE_ID") AS 
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
			cic.PRIORITYID AS PRIORITY_ID,
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

  CREATE OR REPLACE FORCE   VIEW  "INCIDENT_PROBEID" ("CASE_ID", "PKZ_PROBEID", "PKZ_GALLERYID", "LAST_NAME", "FIRST_NAME", "NATIONALITY", "AZR_NUMBER", "BIRTH_DATE", "FILE_NUMBER", "GENDER", "STATUS_ID", "PRIORITY_ID", "D_NUMBER", "FILE_ID", "REFERENCE_NUMBER", "OFFICE", "OFFICE_ID") AS 
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
		cic.PRIORITYID AS PRIORITY_ID,
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

commit;