package sk.atos.fri.ws.ges.service.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
    public List<ReturnSearchHit> hits = new ArrayList<ReturnSearchHit>();

    public SearchResponse() {
    }

    public List<ReturnSearchHit> getHits() {
        return hits;
    }

    public void setHits(List<ReturnSearchHit> hits) {
        this.hits = hits;
    }
}