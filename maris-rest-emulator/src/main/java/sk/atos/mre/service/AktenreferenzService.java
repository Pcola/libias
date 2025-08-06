package sk.atos.mre.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.atos.mre.entity.Aktenreferenz;	
import sk.atos.mre.model.AktenrefResource;
import sk.atos.mre.repository.AktenreferenzRepository;

@Service
public class AktenreferenzService {

	@Autowired
	private AktenreferenzRepository aktenreferencenzRepository;
	
	@Autowired
	private ModelMapper mapper;
	
	public List<Aktenreferenz> findAllByAktenzeichen(String aktenzeichenA, String aktenzeichenB) {
		return this.aktenreferencenzRepository.findAllByAktenzeichen(aktenzeichenA, aktenzeichenB);		
	}
	
	public Aktenreferenz findOneByOid(Long oid) {
		return this.aktenreferencenzRepository.getOne(oid);
	}
	
	public AktenrefResource getResource(Aktenreferenz aktenreferenz) {
		PropertyMap<Aktenreferenz, AktenrefResource> propertyMap = new PropertyMap<Aktenreferenz, AktenrefResource>() {
			
			@Override
			protected void configure() {
				map().setAktenreferenzOid(source.getAktenreferenzOid());
				map().setAktenzeichenA(source.getAktenzeichenA());
				map().setAktenzeichenB(source.getAktenzeichenB());
				map().setDateModified(source.getDateModified());
				map().setReferenzBezeichnung(source.getReferenzbezeichnung());
			}
		};
		if (this.mapper.getTypeMap(Aktenreferenz.class, AktenrefResource.class) == null) {
			this.mapper.addMappings(propertyMap);
		}		
		return this.mapper.map(aktenreferenz, AktenrefResource.class);
	}		
}
