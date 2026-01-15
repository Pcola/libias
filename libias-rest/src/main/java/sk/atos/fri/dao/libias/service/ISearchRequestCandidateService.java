package sk.atos.fri.dao.libias.service;

import sk.atos.fri.dao.libias.model.SearchRequestCandidateEntity;

import java.util.List;
import java.util.Optional;

public interface ISearchRequestCandidateService {


    List<SearchRequestCandidateEntity> findByRequestId(Long id);

    SearchRequestCandidateEntity create(SearchRequestCandidateEntity entity);

    SearchRequestCandidateEntity update(SearchRequestCandidateEntity entity);

    void delete(Long id);

    long countAll();
}

