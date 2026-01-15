package sk.atos.fri.ws.zimp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.atos.fri.log.Logger;
import sk.atos.fri.log.Error;
import sk.atos.fri.zimp.api.BestandsabfrageApi;
import sk.atos.fri.zimp.model.*;
import sk.atos.fri.ws.ges.service.model.SearchResponse;
import sk.atos.fri.ws.ges.service.model.ReturnSearchHit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZimpService {

    private static final Logger LOGGER = new Logger();
    private static final String NO_USER = "ANONYM_USER";

    @Autowired
    private BestandsabfrageApi bestandsabfrageApi;

    @Autowired
    private ObjectMapper objectMapper;

    public SearchResponse search(String queryReason, String imageBase64, String correlationId, Integer maxHits) {
        LOGGER.info("ZimpService: starting search");

        Bestandsabfrage anfrage = buildRequest(queryReason, imageBase64, correlationId, maxHits);

        try {
            List<Systemsuchergebnis> results = bestandsabfrageApi.bestandsabfrage(anfrage);
            return mapToSearchResponse(results);
        } catch (Exception e) {
            LOGGER.error(NO_USER, Error.valueOf("ZIMP Error"), e);
            throw e;
        }
    }

    private Bestandsabfrage buildRequest(String queryReason, String imageBase64, String correlationId, Integer maxHits) {
        Kontext kontext = new Kontext()
                .beauftragenderNutzer(new Benutzer()
                        .id("BKzimpusr1")
                        .familienname("ZIMP")
                        .vorname("Testnutzer1")
                        .telefon("0815/4711")
                        .eMail("di21-zimp-dev-meldungen@bka.bund.de"))
                .organisationseinheit(new Organisationseinheit()
                        .id("502000213")
                        .name("Testabteilung")
                        .telefon("11")
                        .eMail(""))
                .teilnehmer(createKatalogCode("287", "287_17", "Bundeskriminalamt", "V1.3"))
                .rolle(createKatalogCode("ZIMP_Rolle", "Rolle_Unbekannt", "keine Angabe", "1.0"))
                .anlass(createKatalogCode("ZIMP_Anlass", "Anlass_Unbekannt", "keine Angabe", "1.0"))
                .client("RC");

        Anfragedatei datei = new Anfragedatei()
                .dateischluessel("LIBIAS")
                .addSuchparameterItem(createSuchparam("ZIMP_BEGRUENDUNG_DER_ABFRAGE", queryReason))
                .addSuchparameterItem(createSuchparam("LICHTBILD", imageBase64));

        if (maxHits != null) {
            datei.addSuchparameterItem(createSuchparam("ANZAHL_TREFFER", String.valueOf(maxHits)));
        }

        Bestandsabfrage anfrage = new Bestandsabfrage();
        anfrage.setAbfragetyp("Bestandsabfrage");
        anfrage.setAbfragekontext(kontext);
        anfrage.setBestandsabfrageart("MIT_PERSONALIE");
        anfrage.addSystemeItem(new Anfragesystem().systemschluessel("GES").addDateienItem(datei));

        if (correlationId != null) {
            anfrage.addAbfrageparameterItem(new Parameter().parameterschluessel("Correlation-ID").wert(correlationId));
        }

        return anfrage;
    }

    private SearchResponse mapToSearchResponse(List<Systemsuchergebnis> systemResults) {
        SearchResponse response = new SearchResponse();
        if (systemResults == null) return response;

        systemResults.forEach(sys -> {
            if (sys.getDateisuchergebnisse() == null) return;

            sys.getDateisuchergebnisse().forEach(dateiRes -> {
                // Logovanie upozornení (Hinweise)
                if (dateiRes.getHinweise() != null) {
                    dateiRes.getHinweise().forEach(h -> LOGGER.info("ZIMP Hinweis: " + h.getNachricht()));
                }

                // Spracovanie trefferlistov
                if (dateiRes instanceof DateisuchergebnisMitTrefferlisten) {
                    DateisuchergebnisMitTrefferlisten mitListen = (DateisuchergebnisMitTrefferlisten) dateiRes;
                    if (mitListen.getTrefferlisten() != null) {
                        mitListen.getTrefferlisten().forEach(tl -> {
                            if (tl.getTreffer() != null) {
                                tl.getTreffer().stream()
                                        .map(this::mapToReturnSearchHit)
                                        .forEach(hit -> response.getHits().add(hit));
                            }
                        });
                    }
                }
            });
        });

        return response;
    }

    private ReturnSearchHit mapToReturnSearchHit(Treffer treffer) {
        ReturnSearchHit hit = new ReturnSearchHit();
        if (treffer.getTrefferattribute() == null) return hit;

        for (Trefferattribut attr : treffer.getTrefferattribute()) {
            String key = attr.getTrefferattributschluessel();
            String val = attr.getTrefferattributwert();
            if (key == null) continue;

            switch (key) {
                case "RANG":
                    hit.setRank(tryParseInt(val));
                    break;
                case "SCORE":
                    hit.setScore(tryParseDouble(val));
                    break;
                case "TA_QUALITAET":
                    hit.setQuality(tryParseDouble(val));
                    break;
                case "DAM":
                    hit.setId(tryParseLong(val));
                    break;
                case "LANDMARKS":
                    hit.setLandmarks(parseLandmarks(val));
                    break;
                case "BEWERTUNG":
                    hit.setAutomation_advice("Verdacht".equalsIgnoreCase(val) ? 1 : 0);
                    break;
            }
        }
        return hit;
    }

    private KatalogCode createKatalogCode(String kat, String code, String wert, String ver) {
        KatalogCode katalogCode = new KatalogCode();
        katalogCode.setKatalog(kat);
        katalogCode.setCode(code);
        katalogCode.setWert(wert);
        katalogCode.setVersion(ver);
        katalogCode.setKatalogeintragstyp("KatalogCode");
        return katalogCode;
    }

    private Suchparameter createSuchparam(String key, String val) {
        return new Suchparameter().suchparameterschluessel(key).addSuchwerteItem(val);
    }

    private Integer tryParseInt(String v) {
        try { return (v == null || v.isEmpty()) ? null : Integer.parseInt(v); } catch (Exception e) { return null; }
    }

    private Long tryParseLong(String v) {
        try { return (v == null || v.isEmpty()) ? null : Long.parseLong(v); } catch (Exception e) { return null; }
    }

    private Double tryParseDouble(String v) {
        try { return (v == null || v.isEmpty()) ? null : Double.parseDouble(v.replace(",", ".")); } catch (Exception e) { return null; }
    }

    private List<Double> parseLandmarks(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            List<Double> list = new ArrayList<>();
            String[] points = {"leftEye", "rightEye", "noseTip", "leftMouthCorner", "rightMouthCorner"};
            for (String p : points) {
                JsonNode point = node.get(p);
                if (point != null) {
                    list.add(point.get("x").asDouble());
                    list.add(point.get("y").asDouble());
                }
            }
            return list;
        } catch (Exception e) { return null; }
    }
}