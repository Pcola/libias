CREATE OR REPLACE FORCE VIEW INCIDENT AS
  SELECT  DISTINCT
    caseid as case_id,
    pkz_1 AS pkz_probeid,
    pkz_2 AS pkz_galleryid,
    FAMILIENNAME AS last_name,
    vorname AS first_name,
    STAATSANGEHOERIGKEIT AS nationality,
    AZRNUMMER AS AZR_NUMBER,
    Geburtsdatum AS birth_date,
    AKTENZEICHEN AS FILE_NUMBER,
    GESCHLECHT AS gender,
    STATUSID AS STATUS_ID,
    DNUMMER AS D_NUMBER,
    AKTENREFERENZID AS FILE_ID,
    REFERENZBEZEICHNUNG AS REFERENCE_NUMBER,
    dienststelle as office,
    dienststelle_id as office_id
FROM
    (
SELECT
            cs.caseid,
            b1.PKZ as PKZ_1,
            b2.PKZ as PKZ_2,
            a.FAMILIENNAME,
            a.vorname,
            a.STAATSANGEHOERIGKEIT,
            a.AZRNUMMER,
            a.Geburtsdatum,
            a.GESCHLECHT,
            cs.STATUSID,
            b1.AKTENZEICHEN,
            a.DNUMMER,
            cs.AKTENREFERENZID,
            r.REFERENZBEZEICHNUNG,
            d.DIENSTSTELLE,
            d.id as dienststelle_id
        FROM
            CrossIdentificationCases cs
        LEFT OUTER JOIN Status s ON
            cs.statusid = s.statusid
        JOIN Bild b1 ON
            cs.ProbeID = b1.OID
        JOIN Bild b2 ON
            cs.GALLERYID = b2.OID
        JOIN Antragsteller a ON
            a.OID = b1.ANTRAGSTELLER_OID
        LEFT OUTER JOIN DIENSTSTELLE d ON
            d.ID = cs.DIENSTSTELLE_ID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.Filtered = 0
);

CREATE OR REPLACE FORCE VIEW INCIDENT_PROBEID AS
  SELECT  DISTINCT
    caseid as case_id,
    pkz_1 AS pkz_probeid,
    pkz_2 AS pkz_galleryid,
    FAMILIENNAME AS last_name,
    vorname AS first_name,
    STAATSANGEHOERIGKEIT AS nationality,
    AZRNUMMER AS AZR_NUMBER,
    Geburtsdatum AS birth_date,
    AKTENZEICHEN AS FILE_NUMBER,
    GESCHLECHT AS gender,
    STATUSID AS STATUS_ID,
    DNUMMER AS D_NUMBER,
    AKTENREFERENZID AS FILE_ID,
    REFERENZBEZEICHNUNG AS REFERENCE_NUMBER,
    dienststelle as office,
    dienststelle_id as office_id
FROM
    (
SELECT
            cs.caseid,
            b1.PKZ as PKZ_1,
            b2.PKZ as PKZ_2,
            a.FAMILIENNAME,
            a.vorname,
            a.STAATSANGEHOERIGKEIT,
            a.AZRNUMMER,
            a.Geburtsdatum,
            a.GESCHLECHT,
            cs.STATUSID,
            b1.AKTENZEICHEN,
            a.DNUMMER,
            cs.AKTENREFERENZID,
            r.REFERENZBEZEICHNUNG,
            d.DIENSTSTELLE,
            d.id as dienststelle_id
        FROM
            CrossIdentificationCases cs
        LEFT OUTER JOIN Status s ON
            cs.statusid = s.statusid
        JOIN Bild b1 ON
            cs.ProbeID = b1.OID
        JOIN Bild b2 ON
            cs.GALLERYID = b2.OID
        JOIN Antragsteller a ON
            a.OID = b1.ANTRAGSTELLER_OID
        LEFT OUTER JOIN DIENSTSTELLE d ON
            d.ID = cs.DIENSTSTELLE_ID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.Filtered = 0
);