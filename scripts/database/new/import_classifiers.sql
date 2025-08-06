Insert into LOG_TYPE (TYPE_ID,TYPE) values (1,'CREATE_USER');
Insert into LOG_TYPE (TYPE_ID,TYPE) values (2,'UPDATE_USER');
Insert into LOG_TYPE (TYPE_ID,TYPE) values (3,'STATUS_CHANGE');
Insert into LOG_TYPE (TYPE_ID,TYPE) values (4,'TECH_LOG');
Insert into LOG_TYPE (TYPE_ID,TYPE) values (5,'IMPORT_INFO');

Insert into STATUS (STATUS_ID,STATUS) values (1,'OPEN');
Insert into STATUS (STATUS_ID,STATUS) values (2,'FILES_DOUBLET');
Insert into STATUS (STATUS_ID,STATUS) values (3,'FILES_NO_DOUBLET');
Insert into STATUS (STATUS_ID,STATUS) values (4,'NOT_CLEAR');
Insert into STATUS (STATUS_ID,STATUS) values (5,'NO_PROCESSING');
Insert into STATUS (STATUS_ID,STATUS) values (6,'FILES_NO_LINK');
Insert into STATUS (STATUS_ID,STATUS) values (7,'ADJUSTED');
Insert into STATUS (STATUS_ID,STATUS) values (8,'READY_TO_QA');
Insert into STATUS (STATUS_ID,STATUS) values (9,'DNUMBER_DIFF');
Insert into STATUS (STATUS_ID,STATUS) values (10,'AUTO_ADJUSTED');

Insert into USER_ROLE (ROLE_ID,ROLE) values (1,'ADMIN');
Insert into USER_ROLE (ROLE_ID,ROLE) values (2,'SUPERUSER');
Insert into USER_ROLE (ROLE_ID,ROLE) values (3,'USER');
Insert into USER_ROLE (ROLE_ID,ROLE) values (4,'AUSSENSTELLEUSER');
Insert into USER_ROLE (ROLE_ID,ROLE) values (5,'COMPARER');
Insert into USER_ROLE (ROLE_ID,ROLE) values (6,'SEARCHER');

Insert into PRIORITY (PRIORITY_ID,PRIORITY) values (1,'PRIORITY 1');
Insert into PRIORITY (PRIORITY_ID,PRIORITY) values (2,'PRIORITY 2');
Insert into PRIORITY (PRIORITY_ID,PRIORITY) values (3,'PRIORITY 3');
Insert into PRIORITY (PRIORITY_ID,PRIORITY) values (4,'PRIORITY 4');
Insert into PRIORITY (PRIORITY_ID,PRIORITY) values (5,'PRIORITY 5');

commit;
