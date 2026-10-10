package cl.oursour.usuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.usuario.model.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {

	List<Direccion> findByUsuarioIdOrderById(Long usuarioId);

	Optional<Direccion> findByIdAndUsuarioId(Long id, Long usuarioId);

}
