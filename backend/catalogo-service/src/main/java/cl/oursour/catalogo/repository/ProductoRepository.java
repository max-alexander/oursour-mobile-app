package cl.oursour.catalogo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.catalogo.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

	List<Producto> findByActivoTrueOrderByNombre();

	boolean existsByNombreIgnoreCase(String nombre);

	boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

}
