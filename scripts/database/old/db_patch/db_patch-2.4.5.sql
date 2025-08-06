drop view INCIDENT_MATCH;

CREATE OR REPLACE FORCE VIEW CASE_DETAIL AS
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
        LEFT OUTER JOIN Status s ON
            s.StatusID = cs.StatusID
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID;

