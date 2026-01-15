package sk.atos.fri.dao.libias.service;

import sk.atos.fri.dao.libias.model.SearchRequestEntity;
import sk.atos.fri.dao.libias.model.SearchRequestListItemDto;
import sk.atos.fri.rest.model.SearchRequestSearchRequest;

import java.util.List;
import java.util.Optional;

public interface ISearchRequestService {

    Optional<SearchRequestEntity> findById(Long requestId);

    SearchRequestEntity create(SearchRequestEntity entity);

    SearchRequestEntity update(SearchRequestEntity entity);

    void delete(Long requestId);

    long countAll();

    List<SearchRequestEntity> findByCreatedBy(String createdBy, int first, int max);

    List<SearchRequestListItemDto> search(SearchRequestSearchRequest request);

    long count(SearchRequestSearchRequest request);
}