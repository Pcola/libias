package sk.atos.fri.dao.libias.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import sk.atos.fri.ws.maris.model.PersonResponse;
import sk.atos.fri.ws.maris.service.MarisWSClient;

@Repository
public class PersonService {

  @Autowired
  private MarisWSClient marisClient;

  /**
   *
   * @param oids - list of images IDs
   * @return list of person data for images from oids
   */
  public List<PersonResponse> getPersons(Long[] oids) {
    List<PersonResponse> persons = new ArrayList<>();
    for (Long oid : oids) {
      PersonResponse person = marisClient.getPerson(oid);
      if (person != null) {
        persons.add(person);
      }
    }

    return persons;
  }

}
