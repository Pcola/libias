--------------------------------------------------------
--  File created - Monday-March-12-2018   
--  File edited - Wednesday-May-23-2018   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View AUSSENSTELLER_CASE
--------------------------------------------------------

  CREATE TABLE  "AKTENREFERENZ" 
   (	
	"AKTENREFERENZ_OID" NUMBER(*,0) NOT NULL, 
	"AKTENZEICHEN_A" VARCHAR2(32 BYTE), 
	"AKTENZEICHEN_B" VARCHAR2(32 BYTE), 
	"REFERENZBEZEICHNUNG" VARCHAR2(64 BYTE), 
	"DATE_MODIFIED" DATE	
   )
;
--------------------------------------------------------
--  DDL for Table ANTRAGSTELLER
--------------------------------------------------------
	CREATE TABLE  "ANTRAGSTELLER" 
	   (	
		"ANTRAGSTELLER_OID" NUMBER(10,0) NOT NULL, 
		"PKZ" NUMBER(10,0), 
		"AKTENZEICHEN" VARCHAR2(32 BYTE), 
		"AZRNUMMER" VARCHAR2(12 BYTE),
		"DNUMMER" VARCHAR2(128 BYTE), 
		"ENUMMER" VARCHAR2(128 BYTE), 
		"EURODACNR" VARCHAR2(128 BYTE), 
		"FAMILIENNAME" VARCHAR2(64 BYTE), 
		"VORNAME" VARCHAR2(64 BYTE), 
		"GEBURTSDATUM" DATE, 
		"GEBURTSLAND" VARCHAR2(64 BYTE), 
		"GEBURTSORT" VARCHAR2(64 BYTE), 
		"HERKUNFTSLAND" VARCHAR2(64 BYTE), 
		"ANTRAGSDATUM" DATE, 
		"ANTRAGSTYP" VARCHAR2(64 BYTE), 	
		"AUSSENSTELLE" VARCHAR2(64 BYTE), 
		"GESCHLECHT" VARCHAR2(20 BYTE), 	
		"STAATSANGEHOERIGKEIT" VARCHAR2(30 CHAR), 
		"DATE_MODIFIED" DATE,
		"CHANGED" NUMBER(1)
	   )
	;

--------------------------------------------------------
--  DDL for Table BILD
--------------------------------------------------------

  CREATE TABLE  "BILD" 
   (	"OID" NUMBER(10,0) NOT NULL, 
		"ANTRAGSTELLER_OID" NUMBER(10,0) NOT NULL
   )
;

--------------------------------------------------------
--  DDL for Sequence ANTRAGSTELLER_SEQ
--------------------------------------------------------
   CREATE SEQUENCE   "ANTRAGSTELLER_SEQ"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 10 NOCACHE  NOORDER  NOCYCLE;
   
   --------------------------------------------------------
--  DDL for Trigger ANTRAGSTELLER_SEQ_TR
--------------------------------------------------------
CREATE TRIGGER  "ANTRAGSTELLER_SEQ_TR" 
BEFORE INSERT ON  Antragsteller FOR EACH ROW
  WHEN (NEW.ANTRAGSTELLER_OID IS NULL) BEGIN
SELECT Antragsteller_seq.NEXTVAL INTO :NEW.ANTRAGSTELLER_OID FROM DUAL;
END;

/
ALTER TRIGGER  "ANTRAGSTELLER_SEQ_TR" ENABLE;

--------------------------------------------------------
--  DDL for Index BILD_AKTENZEICHEN
--------------------------------------------------------
  CREATE INDEX  "ANTRAGSTELLER_AKTENZEICHEN" ON  "ANTRAGSTELLER" ("AKTENZEICHEN") ;
  
--------------------------------------------------------
--  DDL for Index AKTENREFERENZ_PK
--------------------------------------------------------
  CREATE UNIQUE INDEX  "AKTENREFERENZ_PK" ON  "AKTENREFERENZ" ("AKTENREFERENZ_OID") ;
  
--------------------------------------------------------
--  DDL for Index BILD_OID
--------------------------------------------------------
  CREATE UNIQUE INDEX  "BILD_OID" ON  "BILD" ("OID") ;
 
--------------------------------------------------------
--  DDL for Index AKTENREFERENZ_AKZA
--------------------------------------------------------
  CREATE INDEX  "AKTENREFERENZ_AKZA" ON  "AKTENREFERENZ" ("AKTENZEICHEN_A") ;
  
  --------------------------------------------------------
--  DDL for Index AKTENREFERENZ_AKZB
--------------------------------------------------------
  CREATE INDEX  "AKTENREFERENZ_AKZB" ON  "AKTENREFERENZ" ("AKTENZEICHEN_B") ;
  
--------------------------------------------------------
--  DDL for Index ANTRAGSTELLER_OID
--------------------------------------------------------
  CREATE UNIQUE INDEX  "ANTRAGSTELLER_OID" ON  "ANTRAGSTELLER" ("ANTRAGSTELLER_OID") ;

--------------------------------------------------------
--  Constraints for Table BILD
--------------------------------------------------------

  ALTER TABLE  "BILD" ADD CONSTRAINT "BILD_PK" PRIMARY KEY ("OID")
  USING INDEX;
  
--------------------------------------------------------
--  Constraints for Table AKTENREFERENZ
--------------------------------------------------------

	ALTER TABLE  "AKTENREFERENZ" ADD CONSTRAINT "AKTENREFERENZ_PK" PRIMARY KEY ("AKTENREFERENZ_OID")
	USING INDEX;   

--------------------------------------------------------
--  Constraints for Table ANTRAGSTELLER
--------------------------------------------------------

  ALTER TABLE  "ANTRAGSTELLER" ADD CONSTRAINT "ANTRAGSTELLER_PK" PRIMARY KEY ("ANTRAGSTELLER_OID")
  USING INDEX;
  ALTER TABLE  "ANTRAGSTELLER" ADD CHECK (ANTRAGSTELLER_OID > 0) ENABLE;  

--------------------------------------------------------
--  Ref Constraints for Table BILD
--------------------------------------------------------

  ALTER TABLE  "BILD" ADD CONSTRAINT "BILD_AS_OID_FK" FOREIGN KEY ("ANTRAGSTELLER_OID")
	  REFERENCES  "ANTRAGSTELLER" ("ANTRAGSTELLER_OID") ENABLE;
