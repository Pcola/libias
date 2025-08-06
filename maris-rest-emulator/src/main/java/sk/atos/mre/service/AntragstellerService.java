package sk.atos.mre.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.atos.mre.entity.Antragsteller;
import sk.atos.mre.model.AntragstellerResource;
import sk.atos.mre.model.UpdatedAntragstellerResource;
import sk.atos.mre.repository.AntragstellerRepository;

@Service
public class AntragstellerService {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AntragstellerRepository antragstellerRepository;
	
	public List<Antragsteller> findAllUpdatedAntragstellers() {
		return this.antragstellerRepository.findAllUpdatedAntragstellers();
	}
	
	public AntragstellerResource getResource(Antragsteller antragsteller) {
		
		PropertyMap<Antragsteller, AntragstellerResource> propertyMap = new PropertyMap<Antragsteller, AntragstellerResource>() {
			
			@Override
			protected void configure() {
				map().setAktenzeichen(source.getAktenzeichen());
				map().setAntragsDatum(source.getAntragsdatum());
				map().setAntragstellerOid(source.getAntragstellerOid());
				map().setAntragsTyp(source.getAntragsTyp());
				map().setAussenstelle(source.getAussenstelle());
				map().setAzrNummer(source.getAzrnummer());
				map().setDateModified(source.getDateModified());
				map().setDnummer(source.getDnummer());
				map().setEnummer(source.getEnummer());
				map().setEuroDacNr(source.getEurodacnr());
				map().setFamilienname(source.getFamilienname());
				map().setGeburtsdatum(source.getGeburtsdatum());
				map().setGeburtsland(source.getGeburtsland());
				map().setGeburtsort(source.getGeburtsort());
				map().setGeschlecht(source.getGeschlecht());
				map().setHerkunftsland(source.getHerkunftsland());
				map().setPkz(source.getPkz());
				map().setStaatsangehoerigkeit(source.getStaatsangehoerigkeit());
				map().setVorname(source.getVorname());
				map().setAkteGesperrt(source.getAkteGesperrt());
			}};
			
			if (this.modelMapper.getTypeMap(Antragsteller.class, AntragstellerResource.class) == null) {
				this.modelMapper.addMappings(propertyMap);
			}
			
			return this.modelMapper.map(antragsteller, AntragstellerResource.class);		
	}	
	
	public UpdatedAntragstellerResource getUpdatedAntragstellerResource(Antragsteller antragsteller) {
		
		PropertyMap<Antragsteller, UpdatedAntragstellerResource> propertyMap = new PropertyMap<Antragsteller, UpdatedAntragstellerResource>() {
			
			@Override
			protected void configure() {
				map().setAntragstellerOid(source.getAntragstellerOid());
				map().setDateModified(source.getDateModified());				
			}};
			
			if (this.modelMapper.getTypeMap(Antragsteller.class, UpdatedAntragstellerResource.class) == null) {
				this.modelMapper.addMappings(propertyMap);
			}
			
			return this.modelMapper.map(antragsteller, UpdatedAntragstellerResource.class);		
	}

	public Antragsteller findOneByOid(Long id) {
		return this.antragstellerRepository.getOne(id);
	}
}
