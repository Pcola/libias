package sk.atos.mre.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.atos.mre.entity.AktenGeloescht;
import sk.atos.mre.model.AktenGeloeschtResource;
import sk.atos.mre.repository.AktenGeloeschtRepository;

import java.util.List;

@Service
public class AktenGeloeschtService {

    @Autowired
    private AktenGeloeschtRepository aktenGeloeschtRepository;

    @Autowired
    private ModelMapper mapper;

    public List<AktenGeloescht> findAll(){
        return this.aktenGeloeschtRepository.findAll();
    }

    public AktenGeloescht findOneByAktenzeichen(String aktenzeichen){
        return aktenGeloeschtRepository.getOne(aktenzeichen);
    }

    public AktenGeloeschtResource getResource(AktenGeloescht aktenGeloescht) {
        PropertyMap<AktenGeloescht, AktenGeloeschtResource> propertyMap = new PropertyMap<AktenGeloescht, AktenGeloeschtResource>() {
            @Override
            protected void configure() {
                map().setAktenzeichen(source.getAktenzeichen());
            }
        };
        if (this.mapper.getTypeMap(AktenGeloescht.class, AktenGeloeschtResource.class) == null) {
            this.mapper.addMappings(propertyMap);
        }
        return this.mapper.map(aktenGeloescht, AktenGeloeschtResource.class);
    }
}
