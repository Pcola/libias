package sk.atos.mre.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.mre.entity.Aktenreferenz;
import sk.atos.mre.service.AktenreferenzService;

import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import sk.atos.mre.model.AktenrefResource;
import sk.atos.mre.model.AktenrefResources;

@RestController
public class AktenreferenzController {	
	@Autowired
	private AktenreferenzService aktenreferencenzService;
	
	@RequestMapping(method=RequestMethod.GET,
				path="bis/v1/aktenreferenzen/{aktenzeichenA}/{aktenzeichenB}",
				produces={"application/hal+json"})
	public ResponseEntity<AktenrefResources> getAktenreferencenzByAktenzeichen(@PathVariable String aktenzeichenA, @PathVariable String aktenzeichenB) {	    
	    AktenrefResources resources = new AktenrefResources();		

	    try {
		    List<Aktenreferenz> aktenreferenzs = this.aktenreferencenzService.findAllByAktenzeichen(aktenzeichenA, aktenzeichenB);
		if (!aktenreferenzs.isEmpty()) {

			    for (final Aktenreferenz aktenref: aktenreferenzs) {
				    AktenrefResource aktenrefResource = this.aktenreferencenzService.getResource(aktenref);
				    Link selfLink = linkTo(methodOn(AktenreferenzController.class).getAktenreferenzByOid(aktenrefResource.getAktenreferenzOid())).withSelfRel();

				    aktenrefResource.add(selfLink);
				    resources.embedResource(aktenrefResource);
			    }
		} 
	    } catch (Exception ex) {
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }

	    Link link = linkTo(methodOn(AktenreferenzController.class).getAktenreferencenzByAktenzeichen(aktenzeichenA, aktenzeichenB)).withSelfRel();
	    resources.add(link);

	    return ResponseEntity.ok(resources);		
	}
	
	@RequestMapping(method=RequestMethod.GET,
					path="bis/v1/aktenreferenz/{oid}",
					produces= {"application/hal+json"})
	public ResponseEntity<AktenrefResource> getAktenreferenzByOid(@PathVariable Long oid){	    
	    try {
		    Aktenreferenz aktenreferenz = this.aktenreferencenzService.findOneByOid(oid);
		    AktenrefResource resource = this.aktenreferencenzService.getResource(aktenreferenz);

		    Link selfLink = linkTo(methodOn(AktenreferenzController.class).getAktenreferenzByOid(oid)).withSelfRel();
		    resource.add(selfLink);

		    return ResponseEntity.ok(resource);
	    } catch (Exception e) {
		    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}	
}