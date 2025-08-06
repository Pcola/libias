package sk.atos.mre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.atos.mre.entity.PersonenGeloescht;

@Repository
public interface PersonenGeloeschtRepository extends JpaRepository<PersonenGeloescht, Long> {

}
