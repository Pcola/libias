  CREATE OR REPLACE FORCE VIEW AUSSENSTELLER_CASE AS
  SELECT
    case_id,
    pkz_probe_id,
    pkz_gallery_id,
    probe_id,
    gallery_id,
    score,
    status_id,
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
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID
        WHERE
            cs.FILTERED = 0

    ) ORDER BY case_id ASC;

