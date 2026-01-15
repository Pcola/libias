package sk.atos.fri.rest.model;

import sk.atos.fri.dao.libias.model.SearchRequestListItemDto;

import java.util.List;

public class SearchRequestSearchResponse {

    private List<SearchRequestListItemDto> searchRequests;
    private Long totalCount;

    public SearchRequestSearchResponse(List<SearchRequestListItemDto> searchRequests, Long totalCount) {
        this.searchRequests = searchRequests;
        this.totalCount = totalCount;
    }

    public List<SearchRequestListItemDto> getSearchRequests() {
        return searchRequests;
    }

    public void setSearchRequests(List<SearchRequestListItemDto> searchRequests) {
        this.searchRequests = searchRequests;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
