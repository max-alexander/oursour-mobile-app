package cl.oursour.receta.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.oursour.receta.dto.IngredienteMetrica;
import cl.oursour.receta.model.Busqueda;

public interface BusquedaRepository extends JpaRepository<Busqueda, Long> {

	@Query("""
			select new cl.oursour.receta.dto.IngredienteMetrica(i.id, i.nombre, i.categoria, count(b.id))
			from Busqueda b join b.ingredientes i
			group by i.id, i.nombre, i.categoria
			order by count(b.id) desc, i.nombre
			""")
	List<IngredienteMetrica> ingredientesMasSeleccionados(Pageable pageable);

}
