package sk.atos.mre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import sk.atos.mre.entity.Antragsteller;

public interface AntragstellerRepository extends JpaRepository<Antragsteller, Long>{

	@Query("select a from Antragsteller a where a.changed = 1")
	public List<Antragsteller> findAllUpdatedAntragstellers();
	
}
