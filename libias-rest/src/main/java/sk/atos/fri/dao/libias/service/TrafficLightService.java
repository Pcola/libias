package sk.atos.fri.dao.libias.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sk.atos.fri.dao.libias.model.TrafficLight;
import sk.atos.fri.dao.libias.repository.TrafficLightRepository;

import java.util.List;


@Service
public class TrafficLightService {

    private final TrafficLightRepository repo;

    public TrafficLightService(TrafficLightRepository repo) {
        this.repo = repo;
    }

    @Transactional(readOnly = true)
    public List<TrafficLight> findAll() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public TrafficLight findOne(Long roleId) {
        return repo.findById(roleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "TrafficLight not found"));
    }

    @Transactional
    public TrafficLight create(TrafficLight tl) {
        if (tl.getRoleId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ROLE_ID is required");
        }
        if (repo.existsById(tl.getRoleId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ROLE_ID already exists");
        }
        return repo.create(tl);
    }

    @Transactional
    public TrafficLight update(Long roleId, TrafficLight body) {
        try {
            return repo.update(roleId, body);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @Transactional
    public void delete(Long roleId) {
        try {
            repo.delete(roleId);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}