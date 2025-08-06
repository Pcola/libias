package sk.atos.mre.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sk.atos.mre.entity.AktenGesperrt;

@Repository
public interface AktenGesperrtRepository extends JpaRepository<AktenGesperrt, String> {

}
