package sk.atos.mre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sk.atos.mre.entity.AktenGeloescht;
//import sk.atos.mre.model.AktenGeloeschtResourceList;
import sk.atos.mre.model.AktenGeloeschtResources;
import sk.atos.mre.service.AktenGeloeschtService;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class AktenGeloeschtController {

    @Autowired
    private AktenGeloeschtService aktenGeloeschtService;

    @RequestMapping(method = RequestMethod.GET,
            path = "/agl/v1/akten/{datum}",
            produces = {"application/hal+json"})
    public ResponseEntity<AktenGeloeschtResources> getAktenGeloeschtByDatum(@PathVariable String datum) {
        AktenGeloeschtResources resources = new AktenGeloeschtResources();
        //AktenGeloeschtResourceList resourceList = new AktenGeloeschtResourceList();
        //resources.setEmbedded(resourceList);

        try {
            for (AktenGeloescht aktenGeloescht : aktenGeloeschtService.findAll()) {
                resources.addResource(aktenGeloeschtService.getResource(aktenGeloescht));
                //resourceList.addResource(aktenGeloeschtService.getResource(aktenGeloescht));
            }

            Link link = linkTo(methodOn(AktenGeloeschtController.class).getAktenGeloeschtByDatum(datum)).withSelfRel();
            resources.add(link);

            return ResponseEntity.ok(resources);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
