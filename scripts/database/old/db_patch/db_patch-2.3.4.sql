CREATE OR REPLACE VIEW INCIDENT AS
SELECT  DISTINCT
    caseid AS case_id,
    pkz_1 AS pkz_probeid,
    pkz_2 AS pkz_galleryid,
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
    GESCHLECHT2 AS gender2,
    AKTENZEICHEN AS FILE_NUMBER,    
    AKTENZEICHEN2 AS FILE_NUMBER2,    
    STATUSID AS STATUS_ID,    
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
            a.DNUMMER,
            aa.FAMILIENNAME as FAMILIENNAME2,
            aa.vorname AS vorname2,
            aa.STAATSANGEHOERIGKEIT AS STAATSANGEHOERIGKEIT2,
            aa.AZRNUMMER AS AZRNUMMER2,
            aa.Geburtsdatum AS Geburtsdatum2,
            aa.GESCHLECHT AS GESCHLECHT2,
            aa.DNUMMER AS DNUMMER2,
            cs.STATUSID,
            b1.AKTENZEICHEN,            
            b2.AKTENZEICHEN AS AKTENZEICHEN2,        
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
        JOIN Antragsteller aa ON
            aa.OID = b2.ANTRAGSTELLER_OID
        LEFT OUTER JOIN DIENSTSTELLE d ON 
            d.ID = cs.DIENSTSTELLE_ID        
        LEFT OUTER JOIN AKTENREFERENZ r ON
            cs.AKTENREFERENZID = r.OID            
        WHERE
            cs.Filtered = 0
            );
            
