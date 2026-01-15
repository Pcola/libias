package sk.atos.fri.dao.libias.service;

import org.springframework.stereotype.Service;
import sk.atos.fri.dao.libias.model.SearchRequestCandidateEntity;
import sk.atos.fri.dao.libias.repository.SearchRequestCandidateRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class SearchRequestCandidateService implements ISearchRequestCandidateService {

    private final SearchRequestCandidateRepository repository;

    public SearchRequestCandidateService(SearchRequestCandidateRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<SearchRequestCandidateEntity> findByRequestId(Long id) {
        return repository.findByRequestId(id);
    }

    @Override
    @Transactional
    public SearchRequestCandidateEntity create(SearchRequestCandidateEntity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public SearchRequestCandidateEntity update(SearchRequestCandidateEntity entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        repository.deleteByRequestId(id);
    }

    @Override
    public long countAll() {
        return repository.countAll();
    }
}