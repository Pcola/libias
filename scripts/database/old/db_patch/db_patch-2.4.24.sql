INSERT INTO LOG_TYPE (TYPE_ID, TYPE) VALUES ('4', 'TECH_LOG');

ALTER TABLE LOG MODIFY (USER_ID NULL);

ALTER TABLE LOG ADD (SEVERITY VARCHAR2(10 BYTE), METHOD VARCHAR2(200 BYTE));

/* Treba dat nazov constrainut podla toho, aky je v DB nad tabulkou LOG a konkretne constrain vyzera userId > 0, ten potrebujeme dropnut, lebo chceme logovat aj teoreticky veci bez username */
ALTER TABLE LOG DROP CONSTRAINT SYS_C007000;