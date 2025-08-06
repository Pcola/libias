connect DBSCANADMIN/dbscanadmin123;
--------------------------------------------------------
--  File created - Thursday-May-18-2017   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Table ADMINISTRATIONLOGS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."ADMINISTRATIONLOGS" 
   (	"TYPE" VARCHAR2(255 BYTE), 
	"MESSAGE" VARCHAR2(255 BYTE), 
	"NAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONTIME" TIMESTAMP (6), 
	"ETHNICITY" VARCHAR2(255 BYTE), 
	"AUTHNAME" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE), 
	"GALLERY" VARCHAR2(255 BYTE), 
	"STATIONNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"GENDER" VARCHAR2(10 BYTE), 
	"YEAROFBIRTH" NUMBER(*,0), 
	"TOKENID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table CASES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."CASES" 
   (	"ETHNICITY" VARCHAR2(255 BYTE), 
	"SERIAL" NUMBER(*,0), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"AUTHSIG" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE), 
	"GALLERY" VARCHAR2(255 BYTE), 
	"ISSUER" VARCHAR2(255 BYTE), 
	"YEAROFBIRTH" NUMBER(*,0), 
	"AUTHNAME" VARCHAR2(255 BYTE), 
	"AUTHDATE" TIMESTAMP (6), 
	"PID" VARCHAR2(255 BYTE), 
	"NAME" VARCHAR2(255 BYTE), 
	"BINAUTHDATE" TIMESTAMP (6), 
	"GENDER" VARCHAR2(10 BYTE)
   );

--------------------------------------------------------
--  DDL for Table CROSSIDENTIFICATIONCASES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."CROSSIDENTIFICATIONCASES" 
   (	"PROBEID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table CROSSIDENTIFICATIONPROCESSED
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."CROSSIDENTIFICATIONPROCESSED" 
   (	"PROBEID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table CROSSIDENTIFICATIONRESULTS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."CROSSIDENTIFICATIONRESULTS" 
   (	"RANK" NUMBER(*,0), 
	"PROBEID" VARCHAR2(255 BYTE), 
	"GALLERYID" VARCHAR2(255 BYTE), 
	"SCORE" FLOAT(63), 
	"JOBID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table ENROLLMENTCASES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."ENROLLMENTCASES" 
   (	"AUTHDATE" TIMESTAMP (6), 
	"BINAUTHDATE" TIMESTAMP (6), 
	"BINDATE" TIMESTAMP (6), 
	"FIRPID" VARCHAR2(255 BYTE), 
	"AUTHSIG" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE), 
	"FIRCASEID" VARCHAR2(255 BYTE), 
	"PFDDATE" TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table ENROLLMENTLOGIMAGES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."ENROLLMENTLOGIMAGES" 
   (	"CAPTURETIME" TIMESTAMP (6), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"SOURCE" VARCHAR2(255 BYTE), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"IMG" BLOB
   );
--------------------------------------------------------
--  DDL for Table ENROLLMENTLOGS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."ENROLLMENTLOGS" 
   (	"STATIONNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE), 
	"GALLERY" VARCHAR2(255 BYTE), 
	"YEAROFBIRTH" NUMBER(*,0), 
	"GENDER" VARCHAR2(10 BYTE), 
	"NAME" VARCHAR2(255 BYTE), 
	"AUTHNAME" VARCHAR2(255 BYTE), 
	"ETHNICITY" VARCHAR2(255 BYTE), 
	"TRANSACTIONTIME" TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table ENROLLMENTRESULTS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."ENROLLMENTRESULTS" 
   (	"CASEID" VARCHAR2(255 BYTE), 
	"ENROLLMENTTIME" TIMESTAMP (6), 
	"IMAGEEVALUATION" NUMBER(*,0), 
	"IMAGERECORDID" VARCHAR2(255 BYTE), 
	"JOBID" VARCHAR2(255 BYTE), 
	"FIRGENERATION" NUMBER(*,0)
   );
--------------------------------------------------------
--  DDL for Table EVENTLOGS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."EVENTLOGS" 
   (	"STATIONNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"MESSAGE" VARCHAR2(1024 BYTE), 
	"TYPE" VARCHAR2(255 BYTE), 
	"AUTHNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONTIME" TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table FIR_18_2
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."FIR_18_2" 
   (	"BINDATE" TIMESTAMP (6), 
	"FIR" BLOB, 
	"QUALITY" FLOAT(63), 
	"DURATION" NUMBER(*,0), 
	"BINVAL" BLOB, 
	"SIGNATURE" VARCHAR2(255 BYTE), 
	"SERIAL" NUMBER(*,0), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"THRESHOLD" FLOAT(63), 
	"ISSUER" VARCHAR2(255 BYTE), 
	"PFDDATE" TIMESTAMP (6), 
	"GALLERY" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table IDENTIFICATIONLOGIMAGES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."IDENTIFICATIONLOGIMAGES" 
   (	"SOURCE" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"EYELY" FLOAT(63), 
	"EYERX" FLOAT(63), 
	"CAPTURETIME" TIMESTAMP (6), 
	"EYELX" FLOAT(63), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"EYECONFIDENCE" FLOAT(63), 
	"IMG" BLOB, 
	"EYERY" FLOAT(63)
   );
--------------------------------------------------------
--  DDL for Table IDENTIFICATIONLOGS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."IDENTIFICATIONLOGS" 
   (	"RESULT" VARCHAR2(255 BYTE), 
	"AUTHNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONTIME" TIMESTAMP (6), 
	"TRANSACTIONNAME" VARCHAR2(255 BYTE), 
	"STATIONNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"THRESHOLD" FLOAT(63)
   );
--------------------------------------------------------
--  DDL for Table IDENTIFICATIONMATCHES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."IDENTIFICATIONMATCHES" 
   (	"NAME" VARCHAR2(255 BYTE), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"ETHNICITY" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE), 
	"GALLERY" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"GENDER" VARCHAR2(10 BYTE), 
	"SCORE" FLOAT(63), 
	"YEAROFBIRTH" NUMBER(*,0)
   );
--------------------------------------------------------
--  DDL for Table IDENTIFICATIONPROBES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."IDENTIFICATIONPROBES" 
   (	"IMG" BLOB, 
	"FIR" BLOB, 
	"IMGREF" VARCHAR2(255 BYTE), 
	"PROBEID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table IDENTIFICATIONRESULTS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."IDENTIFICATIONRESULTS" 
   (	"PROBEID" VARCHAR2(255 BYTE), 
	"RANK" NUMBER(*,0), 
	"SCORE" FLOAT(63), 
	"GALLERYID" VARCHAR2(255 BYTE), 
	"JOBID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table IMAGES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."IMAGES" 
   (	"SOURCE" VARCHAR2(255 BYTE), 
	"ISSUER" VARCHAR2(255 BYTE), 
	"EYERY" FLOAT(63), 
	"CASEID" VARCHAR2(255 BYTE), 
	"EYELX" FLOAT(63), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"IMG" BLOB, 
	"SERIAL" NUMBER(*,0), 
	"IMGREF" VARCHAR2(255 BYTE), 
	"EYERX" FLOAT(63), 
	"EYECONFIDENCE" FLOAT(63), 
	"EYELY" FLOAT(63)
   );
--------------------------------------------------------
--  DDL for Table MATESIDENTIFICATIONCASES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."MATESIDENTIFICATIONCASES" 
   (	"PROBEID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table MATESIDENTIFICATIONPROCESSED
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."MATESIDENTIFICATIONPROCESSED" 
   (	"PROBEID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table MATESIDENTIFICATIONRESULTS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."MATESIDENTIFICATIONRESULTS" 
   (	"SCORE" FLOAT(63), 
	"GALLERYID" VARCHAR2(255 BYTE), 
	"JOBID" VARCHAR2(255 BYTE), 
	"PROBEID" VARCHAR2(255 BYTE), 
	"RANK" NUMBER(*,0)
   );
--------------------------------------------------------
--  DDL for Table REPDELETED
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."REPDELETED" 
   (	"ISSUER" VARCHAR2(255 BYTE), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"SERIAL" NUMBER(*,0), 
	"TABLENAME" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table REPSELF
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."REPSELF" 
   (	"REPLICAID" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Table REPTV
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."REPTV" 
   (	"REPLICAID" VARCHAR2(255 BYTE), 
	"SERIAL" NUMBER(*,0)
   );
--------------------------------------------------------
--  DDL for Table VERIFICATIONLOGIMAGES
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."VERIFICATIONLOGIMAGES" 
   (	"RESULT" VARCHAR2(255 BYTE), 
	"EYERX" FLOAT(63), 
	"EYELY" FLOAT(63), 
	"FIR_12_0" BLOB, 
	"SCORE" FLOAT(63), 
	"SOURCE" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"EYERY" FLOAT(63), 
	"IMG" BLOB, 
	"EYELX" FLOAT(63), 
	"RECORDID" VARCHAR2(255 BYTE), 
	"CAPTURETIME" TIMESTAMP (6)
   );
--------------------------------------------------------
--  DDL for Table VERIFICATIONLOGS
--------------------------------------------------------

  CREATE TABLE "DBSCANADMIN"."VERIFICATIONLOGS" 
   (	"YEAROFBIRTH" NUMBER(*,0), 
	"RESULT" VARCHAR2(255 BYTE), 
	"TOKENID" VARCHAR2(255 BYTE), 
	"GENDER" VARCHAR2(10 BYTE), 
	"THRESHOLD" FLOAT(63), 
	"STATIONNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONID" VARCHAR2(255 BYTE), 
	"GALLERY" VARCHAR2(255 BYTE), 
	"CASEID" VARCHAR2(255 BYTE), 
	"AUTHNAME" VARCHAR2(255 BYTE), 
	"TRANSACTIONTIME" TIMESTAMP (6), 
	"ETHNICITY" VARCHAR2(255 BYTE), 
	"NAME" VARCHAR2(255 BYTE)
   );
--------------------------------------------------------
--  DDL for Index I_MIP_PROBEID
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_MIP_PROBEID" ON "DBSCANADMIN"."MATESIDENTIFICATIONPROCESSED" ("PROBEID");
--------------------------------------------------------
--  DDL for Index U_IMAGES_RID
--------------------------------------------------------

  CREATE UNIQUE INDEX "DBSCANADMIN"."U_IMAGES_RID" ON "DBSCANADMIN"."IMAGES" ("RECORDID");
--------------------------------------------------------
--  DDL for Index I_REPDELETED_ISSUER
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_REPDELETED_ISSUER" ON "DBSCANADMIN"."REPDELETED" ("ISSUER");
--------------------------------------------------------
--  DDL for Index I_MIC_PROBEID
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_MIC_PROBEID" ON "DBSCANADMIN"."MATESIDENTIFICATIONCASES" ("PROBEID");
--------------------------------------------------------
--  DDL for Index U_FIR_18_2_RID
--------------------------------------------------------

  CREATE UNIQUE INDEX "DBSCANADMIN"."U_FIR_18_2_RID" ON "DBSCANADMIN"."FIR_18_2" ("RECORDID");
--------------------------------------------------------
--  DDL for Index I_CIP_PROBEID
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_CIP_PROBEID" ON "DBSCANADMIN"."CROSSIDENTIFICATIONPROCESSED" ("PROBEID");
--------------------------------------------------------
--  DDL for Index I_REPDELETED_SERIAL
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_REPDELETED_SERIAL" ON "DBSCANADMIN"."REPDELETED" ("SERIAL");
--------------------------------------------------------
--  DDL for Index U_CASES_RID
--------------------------------------------------------

  CREATE UNIQUE INDEX "DBSCANADMIN"."U_CASES_RID" ON "DBSCANADMIN"."CASES" ("RECORDID");
--------------------------------------------------------
--  DDL for Index U_FIR_18_2_CID
--------------------------------------------------------

  CREATE UNIQUE INDEX "DBSCANADMIN"."U_FIR_18_2_CID" ON "DBSCANADMIN"."FIR_18_2" ("CASEID");
--------------------------------------------------------
--  DDL for Index I_IMAGES_CID
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_IMAGES_CID" ON "DBSCANADMIN"."IMAGES" ("CASEID");
--------------------------------------------------------
--  DDL for Index I_CIC_P
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_CIC_P" ON "DBSCANADMIN"."CROSSIDENTIFICATIONCASES" ("PROBEID");
--------------------------------------------------------
--  DDL for Index I_REPDELETED_TABLENAME
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_REPDELETED_TABLENAME" ON "DBSCANADMIN"."REPDELETED" ("TABLENAME");
--------------------------------------------------------
--  DDL for Index I_CASES_PID
--------------------------------------------------------

  CREATE INDEX "DBSCANADMIN"."I_CASES_PID" ON "DBSCANADMIN"."CASES" ("PID");
--------------------------------------------------------
--  Constraints for Table ENROLLMENTCASES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."ENROLLMENTCASES" ADD PRIMARY KEY ("CASEID");
  ALTER TABLE "DBSCANADMIN"."ENROLLMENTCASES" MODIFY ("CASEID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ENROLLMENTLOGS
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."ENROLLMENTLOGS" ADD PRIMARY KEY ("TRANSACTIONID");
  ALTER TABLE "DBSCANADMIN"."ENROLLMENTLOGS" MODIFY ("TRANSACTIONID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table IDENTIFICATIONPROBES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."IDENTIFICATIONPROBES" ADD PRIMARY KEY ("PROBEID");
  ALTER TABLE "DBSCANADMIN"."IDENTIFICATIONPROBES" MODIFY ("PROBEID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table IDENTIFICATIONLOGS
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."IDENTIFICATIONLOGS" ADD PRIMARY KEY ("TRANSACTIONID");
  ALTER TABLE "DBSCANADMIN"."IDENTIFICATIONLOGS" MODIFY ("TRANSACTIONID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table IMAGES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."IMAGES" ADD CONSTRAINT "U_IMAGES_RID" UNIQUE ("RECORDID");
--------------------------------------------------------
--  Constraints for Table CASES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."CASES" ADD CONSTRAINT "U_CASES_RID" UNIQUE ("RECORDID");
  ALTER TABLE "DBSCANADMIN"."CASES" ADD PRIMARY KEY ("CASEID");
  ALTER TABLE "DBSCANADMIN"."CASES" MODIFY ("CASEID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table VERIFICATIONLOGS
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."VERIFICATIONLOGS" ADD PRIMARY KEY ("TRANSACTIONID");
  ALTER TABLE "DBSCANADMIN"."VERIFICATIONLOGS" MODIFY ("TRANSACTIONID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ADMINISTRATIONLOGS
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."ADMINISTRATIONLOGS" ADD PRIMARY KEY ("TRANSACTIONID");
  ALTER TABLE "DBSCANADMIN"."ADMINISTRATIONLOGS" MODIFY ("TRANSACTIONID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table FIR_18_2
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."FIR_18_2" ADD CONSTRAINT "U_FIR_18_2_CID" UNIQUE ("CASEID");
  ALTER TABLE "DBSCANADMIN"."FIR_18_2" ADD CONSTRAINT "U_FIR_18_2_RID" UNIQUE ("RECORDID");
--------------------------------------------------------
--  Ref Constraints for Table ENROLLMENTLOGIMAGES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."ENROLLMENTLOGIMAGES" ADD CONSTRAINT "R_ELOGIMG_TID" FOREIGN KEY ("TRANSACTIONID")
	  REFERENCES "DBSCANADMIN"."ENROLLMENTLOGS" ("TRANSACTIONID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FIR_18_2
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."FIR_18_2" ADD CONSTRAINT "R_FIR_18_2_CID" FOREIGN KEY ("CASEID")
	  REFERENCES "DBSCANADMIN"."CASES" ("CASEID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table IDENTIFICATIONLOGIMAGES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."IDENTIFICATIONLOGIMAGES" ADD CONSTRAINT "R_ILOGIMG_TID" FOREIGN KEY ("TRANSACTIONID")
	  REFERENCES "DBSCANADMIN"."IDENTIFICATIONLOGS" ("TRANSACTIONID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table IDENTIFICATIONMATCHES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."IDENTIFICATIONMATCHES" ADD CONSTRAINT "R_IMATCHES_TID" FOREIGN KEY ("TRANSACTIONID")
	  REFERENCES "DBSCANADMIN"."IDENTIFICATIONLOGS" ("TRANSACTIONID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table IMAGES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."IMAGES" ADD CONSTRAINT "R_IMAGES_CID" FOREIGN KEY ("CASEID")
	  REFERENCES "DBSCANADMIN"."CASES" ("CASEID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VERIFICATIONLOGIMAGES
--------------------------------------------------------

  ALTER TABLE "DBSCANADMIN"."VERIFICATIONLOGIMAGES" ADD CONSTRAINT "R_VLOGIMG_TID" FOREIGN KEY ("TRANSACTIONID")
	  REFERENCES "DBSCANADMIN"."VERIFICATIONLOGS" ("TRANSACTIONID") ENABLE;

disconnect;