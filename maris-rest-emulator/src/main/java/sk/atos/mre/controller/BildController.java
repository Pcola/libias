package sk.atos.mre.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.mre.entity.Antragsteller;
import sk.atos.mre.model.AntragstellerResource;
import sk.atos.mre.service.BildService;

import org.springframework.hateoas.Link;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import sk.atos.mre.service.AntragstellerService;

@RestController
public class BildController {
	@Autowired
	private BildService bildService;
	
	@Autowired
	private AntragstellerService antragstellerService;

	@RequestMapping(path="bis/v1/bild/{oid}/antragsteller", 
					method=RequestMethod.GET,
					produces={"application/hal+json"})
	public ResponseEntity<AntragstellerResource> getAntragstellerByBildOid(@PathVariable Long oid){          
		try {
			Antragsteller antragsteller = this.bildService.getBild(oid).getAntragsteller();
			AntragstellerResource resource = this.antragstellerService.getResource(antragsteller);
			
			Link selfLink = linkTo(methodOn(BildController.class).getAntragstellerByBildOid(oid)).withSelfRel();
			resource.add(selfLink);
			
			return ResponseEntity.ok(resource);
		} catch (NullPointerException e) {
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
