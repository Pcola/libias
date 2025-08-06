package sk.atos.mre.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.atos.mre.entity.AktenGesperrt;
import sk.atos.mre.model.AktenGesperrtResource;
import sk.atos.mre.repository.AktenGesperrtRepository;

import java.util.List;

@Service
public class AktenGesperrtService {

    @Autowired
    private AktenGesperrtRepository aktenGesperrtRepository;

    @Autowired
    private ModelMapper mapper;

    public List<AktenGesperrt> findAll(){
        return this.aktenGesperrtRepository.findAll();
    }

    public AktenGesperrt findOneByAktenzeichen(String aktenzeichen){
        return aktenGesperrtRepository.getOne(aktenzeichen);
    }

    public AktenGesperrtResource getResource(AktenGesperrt aktenGesperrt) {
        PropertyMap<AktenGesperrt, AktenGesperrtResource> propertyMap = new PropertyMap<AktenGesperrt, AktenGesperrtResource>() {
            @Override
            protected void configure() {
                map().setAktenzeichen(source.getAktenzeichen());
            }
        };
        if (this.mapper.getTypeMap(AktenGesperrt.class, AktenGesperrtResource.class) == null) {
            this.mapper.addMappings(propertyMap);
        }
        return this.mapper.map(aktenGesperrt, AktenGesperrtResource.class);
    }
}
