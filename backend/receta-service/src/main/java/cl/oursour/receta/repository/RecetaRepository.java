package cl.oursour.receta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.receta.model.Receta;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

	@EntityGraph(attributePaths = { "ingredientes", "ingredientes.ingrediente" })
	List<Receta> findAllByOrderByTitulo();

	boolean existsByTituloIgnoreCase(String titulo);

}
