package sk.atos.mre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sk.atos.mre.entity.PersonenGeloescht;
//import sk.atos.mre.model.PersonenGeloeschtResourceList;
import sk.atos.mre.model.PersonenGeloeschtResources;
import sk.atos.mre.service.PersonenGeloeschtService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class PersonenGeloeschtController {

    @Autowired
    private PersonenGeloeschtService personenGeloeschtService;

    @RequestMapping(method = RequestMethod.GET,
            path = "/pgl/v1/datum/{datum}",
            produces = {"application/hal+json"})
    public ResponseEntity<PersonenGeloeschtResources> getPersonenGeloeschtByDatum(@PathVariable String datum) {
        PersonenGeloeschtResources resources = new PersonenGeloeschtResources();
        //PersonenGeloeschtResourceList resourceList = new PersonenGeloeschtResourceList();
        //resources.setEmbedded(resourceList);

        try {
            for (PersonenGeloescht personenGeloescht : personenGeloeschtService.findAll()) {
                resources.addResource(personenGeloeschtService.getResource(personenGeloescht));
                //resourceList.addResource(personenGeloeschtService.getResource(personenGeloescht));
            }

            Link link = linkTo(methodOn(PersonenGeloeschtController.class).getPersonenGeloeschtByDatum(datum)).withSelfRel();
            resources.add(link);

            return ResponseEntity.ok(resources);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
