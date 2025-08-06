package sk.atos.mre.service;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sk.atos.mre.entity.PersonenGeloescht;
import sk.atos.mre.model.PersonenGeloeschtResource;
import sk.atos.mre.repository.PersonenGeloeschtRepository;

import java.util.List;

@Service
public class PersonenGeloeschtService {

    @Autowired
    private PersonenGeloeschtRepository personenGeloeschtRepository;

    @Autowired
    private ModelMapper mapper;

    public List<PersonenGeloescht> findAll(){
        return this.personenGeloeschtRepository.findAll();
    }

    public PersonenGeloescht findOneByPersonennummer(Long personennummer) {
        return personenGeloeschtRepository.getOne(personennummer);
    }

    public PersonenGeloeschtResource getResource(PersonenGeloescht personenGeloescht) throws Exception {
       PropertyMap<PersonenGeloescht, PersonenGeloeschtResource> propertyMap = new PropertyMap<PersonenGeloescht, PersonenGeloeschtResource>() {
            @Override
            protected void configure() {
                map().setPersonennummer(source.getPersonennummer());
                map().setAktenzeichen(source.getAktenzeichen());
                map().setAzrnummer(source.getAzrnummer());
            }
        };
        if (this.mapper.getTypeMap(PersonenGeloescht.class, PersonenGeloeschtResource.class) == null) {
            this.mapper.addMappings(propertyMap);
        }
        return this.mapper.map(personenGeloescht, PersonenGeloeschtResource.class);
    }
}
