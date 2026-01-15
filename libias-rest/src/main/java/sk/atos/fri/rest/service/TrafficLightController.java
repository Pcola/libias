package sk.atos.fri.rest.service;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sk.atos.fri.dao.libias.model.TrafficLight;
import sk.atos.fri.dao.libias.service.TrafficLightService;

import java.util.List;


@RestController
@RequestMapping("/api/traffic-lights")
public class TrafficLightController {

    private final TrafficLightService service;

    public TrafficLightController(TrafficLightService service) {
        this.service = service;
    }

    @GetMapping
    public List<TrafficLight> list() {
        return service.findAll();
    }

    @GetMapping("/{roleId}")
    public TrafficLight get(@PathVariable Long roleId) {
        return service.findOne(roleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrafficLight create(@Validated @RequestBody TrafficLight body) {
        return service.create(body);
    }

    @PutMapping("/{roleId}")
    public TrafficLight update(@PathVariable Long roleId,
                               @Validated @RequestBody TrafficLight body) {
        return service.update(roleId, body);
    }

    @DeleteMapping("/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long roleId) {
        service.delete(roleId);
    }
}