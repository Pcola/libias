package sk.atos.mre.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import sk.atos.mre.entity.Antragsteller;
import sk.atos.mre.model.UpdatedAntragstellerResource;
import sk.atos.mre.model.UpdatedAntragstellerResources;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.http.HttpStatus;
import sk.atos.mre.service.AntragstellerService;

@RestController
public class UpdatedApplicantsController {
    @Autowired
    private AntragstellerService antragstellerService;

    @RequestMapping(path="bis/v1/antragsteller", 
				    method=RequestMethod.GET,
				    produces={"application/hal+json"})
    public ResponseEntity<UpdatedAntragstellerResources> getUpdatedApplicants() {       
	UpdatedAntragstellerResources resources = new UpdatedAntragstellerResources();
	try {
		List<Antragsteller> antragstellers = this.antragstellerService.findAllUpdatedAntragstellers();

		if (!antragstellers.isEmpty()) {			
			for (final Antragsteller antragsteller: antragstellers) {
				UpdatedAntragstellerResource resource = this.antragstellerService.getUpdatedAntragstellerResource(antragsteller);
				Link selfLink = linkTo(methodOn(UpdatedApplicantsController.class).getUpdatedApplicantById(resource.getAntragstellerOid())).withSelfRel();				
				resources.add(selfLink);
				resources.embedResource(resource);				 
			}

		}

		Link link = linkTo(methodOn(UpdatedApplicantsController.class).getUpdatedApplicants()).withSelfRel();			
		resources.add(link);
		return ResponseEntity.ok(resources);

	} catch (Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
    }

    @RequestMapping(method=RequestMethod.GET,
		    path="bis/v1/antragsteller/{id}",
		    produces= {"application/hal+json"})
    public ResponseEntity<UpdatedAntragstellerResource> getUpdatedApplicantById(@PathVariable Long id){      
	try {
		Antragsteller antragsteller = this.antragstellerService.findOneByOid(id);
		UpdatedAntragstellerResource resource = this.antragstellerService.getUpdatedAntragstellerResource(antragsteller);

		Link selfLink = linkTo(methodOn(UpdatedApplicantsController.class).getUpdatedApplicantById(id)).withSelfRel();
		resource.add(selfLink);

		return ResponseEntity.ok(resource);
	} catch (Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}
    }
}
