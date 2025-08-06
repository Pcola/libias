package sk.atos.mre.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sk.atos.mre.entity.Aktenreferenz;

public interface AktenreferenzRepository extends JpaRepository<Aktenreferenz, Long>{

	@Query("select a from Aktenreferenz a where ((a.aktenzeicherA = :aktenzeichenA) and (a.aktenzeichenB = :aktenzeichenB)) or ((a.aktenzeicherA = :aktenzeichenB) and (a.aktenzeichenB = :aktenzeichenA))")
	public List<Aktenreferenz> findAllByAktenzeichen(@Param("aktenzeichenA") String aktenzeichenA, @Param("aktenzeichenB") String aktenzeichenB);
	
}
