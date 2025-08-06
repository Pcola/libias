-- FaceVACS Database Schema
-- Schema Version: 1.36
-- Script Revision: 1.171
-- Product: DBScan
-- Schema: FvDb
-- SQL Dialect: Oracle
-- Action: create
--
-- ----------------- Cases ----------------------
CREATE TABLE Cases (Gender VARCHAR(10), AuthDate TIMESTAMP, PID VARCHAR(255), Name VARCHAR(255), AuthSig VARCHAR(255), Serial INTEGER, CaseID VARCHAR(255) NOT NULL, Ethnicity VARCHAR(255), BinAuthDate TIMESTAMP, YearOfBirth INTEGER, AuthName VARCHAR(255), RecordID VARCHAR(255) CONSTRAINT U_Cases_RID UNIQUE, Gallery VARCHAR(255), Issuer VARCHAR(255), PRIMARY KEY (CaseID));
CREATE INDEX I_Cases_PID ON Cases (PID);
CREATE INDEX I_Cases_RID ON Cases (RecordID);
-- ----------------- Images ----------------------
CREATE TABLE Images (ImgRef VARCHAR(255), CaseID VARCHAR(255) CONSTRAINT R_Images_CID REFERENCES Cases(CaseID), Source VARCHAR(255), Serial INTEGER, EyeLX REAL, Img BLOB, EyeRX REAL, EyeRY REAL, EyeLY REAL, Issuer VARCHAR(255), RecordID VARCHAR(255) CONSTRAINT U_Images_RID UNIQUE, EyeConfidence REAL);
CREATE INDEX I_Images_CID ON Images (CaseID);
CREATE INDEX I_Images_RID ON Images (RecordID);
-- ----------------- FIR_18_2 ----------------------
CREATE TABLE FIR_18_2 (Signature VARCHAR(255), PfdDate TIMESTAMP, BinDate TIMESTAMP, Gallery VARCHAR(255), Issuer VARCHAR(255), RecordID VARCHAR(255) CONSTRAINT U_FIR_18_2_RID UNIQUE, Threshold REAL, FIR BLOB, CaseID VARCHAR(255) CONSTRAINT U_FIR_18_2_CID UNIQUE CONSTRAINT R_FIR_18_2_CID REFERENCES Cases(CaseID), BinVal BLOB, Duration INTEGER, Quality REAL, Serial INTEGER);
CREATE INDEX I_FIR_18_2_RID ON FIR_18_2 (RecordID);
CREATE INDEX I_FIR_18_2_CID ON FIR_18_2 (CaseID);
-- ----------------- EnrollmentResults ----------------------
CREATE TABLE EnrollmentResults (FIRGeneration INTEGER, EnrollmentTime TIMESTAMP, CaseID VARCHAR(255), ImageEvaluation INTEGER, ImageRecordID VARCHAR(255), JobID VARCHAR(255));
-- ----------------- EnrollmentCases ----------------------
CREATE TABLE EnrollmentCases (BinAuthDate TIMESTAMP, CaseID VARCHAR(255) NOT NULL, AuthSig VARCHAR(255), FIRCaseID VARCHAR(255), AuthDate TIMESTAMP, FIRPID VARCHAR(255), BinDate TIMESTAMP, PfdDate TIMESTAMP, PRIMARY KEY (CaseID));
-- ----------------- IdentificationProbes ----------------------
CREATE TABLE IdentificationProbes (Img BLOB, ImgRef VARCHAR(255), ProbeID VARCHAR(255) NOT NULL, FIR BLOB, PRIMARY KEY (ProbeID));
-- ----------------- IdentificationResults ----------------------
CREATE TABLE IdentificationResults (GalleryID VARCHAR(255), JobID VARCHAR(255), Rank INTEGER, ProbeID VARCHAR(255), Score REAL);
-- ----------------- CrossIdentificationCases ----------------------
CREATE TABLE CrossIdentificationCases (ProbeID VARCHAR(255));
CREATE INDEX I_CIC_P ON CrossIdentificationCases (ProbeID);
-- ----------------- CrossIdentificationProcessed ----------------------
CREATE TABLE CrossIdentificationProcessed (ProbeID VARCHAR(255));
CREATE INDEX I_CIP_ProbeID ON CrossIdentificationProcessed (ProbeID);
-- ----------------- CrossIdentificationResults ----------------------
CREATE TABLE CrossIdentificationResults (Rank INTEGER, JobID VARCHAR(255), GalleryID VARCHAR(255), Score REAL, ProbeID VARCHAR(255));
-- ----------------- MatesIdentificationCases ----------------------
CREATE TABLE MatesIdentificationCases (ProbeID VARCHAR(255));
CREATE INDEX I_MIC_ProbeID ON MatesIdentificationCases (ProbeID);
-- ----------------- MatesIdentificationProcessed ----------------------
CREATE TABLE MatesIdentificationProcessed (ProbeID VARCHAR(255));
CREATE INDEX I_MIP_ProbeID ON MatesIdentificationProcessed (ProbeID);
-- ----------------- MatesIdentificationResults ----------------------
CREATE TABLE MatesIdentificationResults (Score REAL, ProbeID VARCHAR(255), Rank INTEGER, JobID VARCHAR(255), GalleryID VARCHAR(255));
-- ----------------- RepSelf ----------------------
CREATE TABLE RepSelf (ReplicaID VARCHAR(255));
-- ----------------- RepTV ----------------------
CREATE TABLE RepTV (Serial INTEGER, ReplicaID VARCHAR(255));
-- ----------------- RepDeleted ----------------------
CREATE TABLE RepDeleted (TableName VARCHAR(255), RecordID VARCHAR(255), Serial INTEGER, Issuer VARCHAR(255));
CREATE INDEX I_RepDeleted_TableName ON RepDeleted (TableName);
CREATE INDEX I_RepDeleted_Serial ON RepDeleted (Serial);
CREATE INDEX I_RepDeleted_Issuer ON RepDeleted (Issuer);
