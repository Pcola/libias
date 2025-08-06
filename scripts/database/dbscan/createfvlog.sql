-- FaceVACS Database Schema
-- Schema Version: 1.36
-- Script Revision: 1.171
-- Product: DBScan
-- Schema: LogDb
-- SQL Dialect: Oracle
-- Action: create
--
-- ----------------- EnrollmentLogs ----------------------
CREATE TABLE EnrollmentLogs (CaseID VARCHAR(255), Gender VARCHAR(10), Ethnicity VARCHAR(255), AuthName VARCHAR(255), TransactionTime TIMESTAMP, YearOfBirth INTEGER, StationName VARCHAR(255), Gallery VARCHAR(255), TransactionID VARCHAR(255) NOT NULL, Name VARCHAR(255), PRIMARY KEY (TransactionID));
-- ----------------- EnrollmentLogImages ----------------------
CREATE TABLE EnrollmentLogImages (CaptureTime TIMESTAMP, Source VARCHAR(255), TransactionID VARCHAR(255) CONSTRAINT R_ELogImg_TID REFERENCES EnrollmentLogs(TransactionID), Img BLOB, RecordID VARCHAR(255));
-- ----------------- VerificationLogs ----------------------
CREATE TABLE VerificationLogs (Name VARCHAR(255), TransactionID VARCHAR(255) NOT NULL, Gallery VARCHAR(255), Result VARCHAR(255), StationName VARCHAR(255), Threshold REAL, YearOfBirth INTEGER, Ethnicity VARCHAR(255), Gender VARCHAR(10), TokenID VARCHAR(255), TransactionTime TIMESTAMP, AuthName VARCHAR(255), CaseID VARCHAR(255), PRIMARY KEY (TransactionID));
-- ----------------- VerificationLogImages ----------------------
CREATE TABLE VerificationLogImages (CaptureTime TIMESTAMP, EyeRX REAL, RecordID VARCHAR(255), Img BLOB, FIR_12_0 BLOB, Score REAL, EyeRY REAL, Source VARCHAR(255), TransactionID VARCHAR(255) CONSTRAINT R_VLogImg_TID REFERENCES VerificationLogs(TransactionID), EyeLY REAL, Result VARCHAR(255), EyeLX REAL);
-- ----------------- IdentificationLogs ----------------------
CREATE TABLE IdentificationLogs (TransactionName VARCHAR(255), Threshold REAL, StationName VARCHAR(255), Result VARCHAR(255), AuthName VARCHAR(255), TransactionTime TIMESTAMP, TransactionID VARCHAR(255) NOT NULL, PRIMARY KEY (TransactionID));
-- ----------------- IdentificationLogImages ----------------------
CREATE TABLE IdentificationLogImages (EyeLX REAL, TransactionID VARCHAR(255) CONSTRAINT R_ILogImg_TID REFERENCES IdentificationLogs(TransactionID), EyeLY REAL, EyeRY REAL, Source VARCHAR(255), RecordID VARCHAR(255), Img BLOB, EyeRX REAL, EyeConfidence REAL, CaptureTime TIMESTAMP);
-- ----------------- IdentificationMatches ----------------------
CREATE TABLE IdentificationMatches (Ethnicity VARCHAR(255), Gender VARCHAR(10), RecordID VARCHAR(255), CaseID VARCHAR(255), YearOfBirth INTEGER, Gallery VARCHAR(255), TransactionID VARCHAR(255) CONSTRAINT R_IMatches_TID REFERENCES IdentificationLogs(TransactionID), Score REAL, Name VARCHAR(255));
-- ----------------- AdministrationLogs ----------------------
CREATE TABLE AdministrationLogs (CaseID VARCHAR(255), Gender VARCHAR(10), Ethnicity VARCHAR(255), TransactionTime TIMESTAMP, TokenID VARCHAR(255), AuthName VARCHAR(255), YearOfBirth INTEGER, StationName VARCHAR(255), TransactionID VARCHAR(255) NOT NULL, Gallery VARCHAR(255), Name VARCHAR(255), Message VARCHAR(255), Type VARCHAR(255), PRIMARY KEY (TransactionID));
-- ----------------- EventLogs ----------------------
CREATE TABLE EventLogs (Type VARCHAR(255), Message VARCHAR(1024), AuthName VARCHAR(255), TransactionTime TIMESTAMP, TransactionID VARCHAR(255), StationName VARCHAR(255));
