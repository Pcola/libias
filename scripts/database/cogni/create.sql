connect cogni/cogni123;

--------------------------------------------------------
--  DDL for Table BILD
--------------------------------------------------------

CREATE TABLE BILD (
	OID NUMBER(10,0),
	DATE_MODIFIED DATE,
	BILDDATEN BLOB
);

--------------------------------------------------------
--  DDL for Index BILD_OID
--------------------------------------------------------

CREATE UNIQUE INDEX PK_BILD ON BILD(OID);

--------------------------------------------------------
--  Constraints for Table BILD
--------------------------------------------------------

ALTER TABLE BILD MODIFY (OID NOT NULL ENABLE);

ALTER TABLE BILD ADD CONSTRAINT PK_BILD PRIMARY KEY (OID) USING INDEX;

disconnect;