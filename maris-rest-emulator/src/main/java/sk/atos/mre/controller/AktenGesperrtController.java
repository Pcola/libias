package sk.atos.mre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sk.atos.mre.entity.AktenGesperrt;
//import sk.atos.mre.model.AktenGesperrtResourceList;
import sk.atos.mre.model.AktenGesperrtResources;
import sk.atos.mre.service.AktenGesperrtService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AktenGesperrtController {

    @Autowired
    private AktenGesperrtService aktenGesperrtService;

    @RequestMapping(method = RequestMethod.GET,
            path = "/gal/v1/aktenzeichen",
            produces = {"application/hal+json"})
    public ResponseEntity<AktenGesperrtResources> getAktenGesperrt() {
        AktenGesperrtResources resources = new AktenGesperrtResources();
        //AktenGesperrtResourceList resourceList = new AktenGesperrtResourceList();
        //resources.setEmbedded(resourceList);

        try {
            for (AktenGesperrt aktenGesperrt : aktenGesperrtService.findAll()) {
                resources.addResource(aktenGesperrtService.getResource(aktenGesperrt));
                //resourceList.addResource(aktenGesperrtService.getResource(aktenGesperrt));
            }

            Link link = linkTo(methodOn(AktenGesperrtController.class).getAktenGesperrt()).withSelfRel();
            resources.add(link);

            return ResponseEntity.ok(resources);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
