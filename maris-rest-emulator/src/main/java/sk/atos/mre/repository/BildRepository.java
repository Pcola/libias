package sk.atos.mre.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sk.atos.mre.entity.Bild;

public interface BildRepository extends JpaRepository<Bild, Long>{
	
}
