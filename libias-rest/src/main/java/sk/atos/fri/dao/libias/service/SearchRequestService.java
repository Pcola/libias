package sk.atos.fri.dao.libias.service;

import org.springframework.stereotype.Service;
import sk.atos.fri.dao.libias.mapper.SearchRequestMapper;
import sk.atos.fri.dao.libias.model.SearchRequestEntity;
import sk.atos.fri.dao.libias.model.SearchRequestListItemDto;
import sk.atos.fri.dao.libias.repository.SearchRequestRepository;
import sk.atos.fri.rest.model.SearchRequestSearchRequest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SearchRequestService implements ISearchRequestService {

    private final SearchRequestRepository repository;

    public SearchRequestService(SearchRequestRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<SearchRequestEntity> findById(Long requestId) {
        return repository.findById(requestId);
    }

    @Override
    @Transactional
    public SearchRequestEntity create(SearchRequestEntity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public SearchRequestEntity update(SearchRequestEntity entity) {
        return repository.update(entity);
    }

    @Override
    @Transactional
    public void delete(Long requestId) {
        repository.deleteById(requestId);
    }

    @Override
    public long countAll() {
        return repository.countAll();
    }

    @Override
    public List<SearchRequestEntity> findByCreatedBy(String createdBy, int first, int max) {
        return repository.findByCreatedBy(createdBy, first, max);
    }

    @Override
    public List<SearchRequestListItemDto> search(SearchRequestSearchRequest request) {
        return repository.search(request).stream()
                .map(SearchRequestMapper::toListItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public long count(SearchRequestSearchRequest request) {
        return repository.count(request);
    }
}