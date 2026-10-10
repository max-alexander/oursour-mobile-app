package cl.oursour.receta.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.model.Ingrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

	List<Ingrediente> findAllByOrderByCategoriaAscNombreAsc();

	List<Ingrediente> findByCategoriaOrderByNombre(CategoriaIngrediente categoria);

	boolean existsByNombreIgnoreCase(String nombre);

}
