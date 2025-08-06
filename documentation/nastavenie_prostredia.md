# Nastavenie prostredia

[Oracle XE](http://www.oracle.com/technetwork/database/database-technologies/express-edition/overview/index.html)
<br>
[Git](https://git-scm.com/downloads)
<br>
[Maven](https://pdfbox.apache.org/download.cgi)
<br>
[Npm](https://www.npmjs.com/get-npm)


1. Naklonovanie projektu.
	
	cd /path/to/repository
	git clone http://[login-name]@10.95.159.192:7990/scm/bam/bamfface.git

2. Vytvorenie lokálnej databázy pre libias (staré/nepotrebné - nahradené bodom 4).

	* Vytvorenie schémy a používateľa. Spustite RUN SQL COMMAND LINE a zadajte: *(alebo scripts/database/old/settings.sql)* :

		connect system/password;

		create user libias identified by password;

		grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK,
		CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM,
		CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE,
		CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE
		to libias;

	* Vytvorenie všetkého potrebného a naplnenie dátami:

		connect libias/password

		@/path/to/repository/bamfface/scripts/database/old/create.sql;
		@/path/to/repository/bamfface/scripts/database/old/import.sql;

	* Následne je potrebné vložiť aj obrázky. Obrázky sú v priečinku `scripts/database/images/`. Obrázky vložte do blobu v tabuľke `BILD`, v ktorej sa už nachádzajú dva riadky.

3. Vytvorenie lokálnej databázy pre maris emulátor.

	* Vytvorenie schémy a používateľa. Spustite RUN SQL COMMAND LINE a zadajte: *(alebo scripts/database/emulator/settings_emul.sql)*

		connect system/password;

		create user maris identified by maris123;

		grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK,
		CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM,
		CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE,
		CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE
		to maris;

	* Vytvorenie všetkého potrebného a naplnenie dátami:

		connect maris/maris123
	
		@/path/to/repository/bamfface/scripts/database/emulator/create_emul.sql;
		@/path/to/repository/bamfface/scripts/database/emulator/import_emul.sql;

4. Vytvorenie lokálnej databázy pre novú schému libias.

	* Vytvorenie schémy a používateľa. Spustite RUN SQL COMMAND LINE a zadajte: *(alebo scripts/database/new/settings_new.sql)*

		connect system/password;

		create user libiasNew identified by libias123;

		grant CREATE SESSION, ALTER SESSION, CREATE DATABASE LINK,
		CREATE MATERIALIZED VIEW, CREATE PROCEDURE, CREATE PUBLIC SYNONYM,
		CREATE ROLE, CREATE SEQUENCE, CREATE SYNONYM, CREATE TABLE,
		CREATE TRIGGER, CREATE TYPE, CREATE VIEW, UNLIMITED TABLESPACE
		to libiasNew;

	* Vytvorenie všetkého potrebného a naplnenie dátami:

		connect libiasNew/libias123

		@/path/to/repository/bamfface/scripts/database/new/create_new.sql;
		@/path/to/repository/bamfface/scripts/database/new/import_classifiers.sql;
		@/path/to/repository/bamfface/scripts/database/new/import_user.sql;
		@/path/to/repository/bamfface/scripts/database/new/import_data.sql;

		disconnect;

5. Rozbehanie backendu *(priečinok libias-rest)*.

	* Spustenie inštalácie `ojdbc` do `maven`-u:
		* Spustite `install-lib.sh` v priečinku `libs`, prípadne spustite príkaz, ktorý obsahuje tento skript.
	* Skontrolovanie/nastavenie jdbc.datasource v súbore `src/main/resources/application-localhost.properties` - najmä v prípade vlastného hesla.
	* Po spustení `start_rest.cmd` by sa mal spustiť backend. Pri prvom spustení sa inštalujú knižnice do `maven`-u - je potrebné internetové pripojenie.

6. Rozbehanie frontendu *(priečinok libias-web)*.

	* Spustite príkaz `npm install` *(Aj keby toto na konci spadlo, tak by mali byť naištalované všetky potrebné balíčky na spustenie frontendu)*
	* Spustite skript `build-local.sh`.
	* Spustenie frontendu príkazom `npm start` (resp. pomocou `start_web.cmd`). Na localhoste na porte 5555 bude bežať frontend.

7. Prístup do aplikácie: admin:admin123

8. Vytvorenie lokálnej databázy pre schému cogni (v prípade, že potrebujete používať procedúry pre import z cogni schémy).

	* Vytvorenie všetkého potrebného:

		@/path/to/repository/bamfface/scripts/database/cogni/settings.sql;
		@/path/to/repository/bamfface/scripts/database/cogni/create.sql;

	* Nasledovné počíta s tým, že existuje user libiasnew

		@/path/to/repository/bamfface/scripts/database/cogni/libias_grant.sql;

	* Naplnenie dátami:
		- nastavte si cestu v súbore import.sql podľa toho, kde máte repozitár
		- nastavte celkový počet fotiek, koľko potrebujete (numOfImages)

		@/path/to/repository/bamfface/scripts/database/cogni/import.sql;

9. Vytvorenie lokálnej databázy pre schemu dbscan (V prípade, že potrebujete používať procedúry, ktoré pristupujú do tabuliek v dbscan schéme).

	* Vytvorenie všetkého potrebného:

		@/path/to/repository/bamfface/scripts/database/dbscan/settings.sql;
		@/path/to/repository/bamfface/scripts/database/dbscan/create.sql;

	* Nasledovné počíta s tým, že existuje user libiasnew
		@/path/to/repository/bamfface/scripts/database/dbscan/libias_grant.sql;
