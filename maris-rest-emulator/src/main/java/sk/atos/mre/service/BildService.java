package sk.atos.mre.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.atos.mre.entity.Bild;
import sk.atos.mre.repository.BildRepository;

@Service
public class BildService {
		
	@Autowired
	private BildRepository bildRepository;
	
	public Bild getBild(Long oid) {
	    return this.bildRepository.getOne(oid);
	}
}
