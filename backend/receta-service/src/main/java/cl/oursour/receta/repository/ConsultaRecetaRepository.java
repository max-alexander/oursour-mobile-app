package cl.oursour.receta.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.oursour.receta.dto.RecetaMetrica;
import cl.oursour.receta.model.ConsultaReceta;

public interface ConsultaRecetaRepository extends JpaRepository<ConsultaReceta, Long> {

	@Query("""
			select new cl.oursour.receta.dto.RecetaMetrica(r.id, r.titulo, count(c.id))
			from ConsultaReceta c join c.receta r
			group by r.id, r.titulo
			order by count(c.id) desc, r.titulo
			""")
	List<RecetaMetrica> recetasMasConsultadas(Pageable pageable);

}
