package cl.oursour.pedido.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.pedido.model.Carro;

public interface CarroRepository extends JpaRepository<Carro, Long> {

	Optional<Carro> findByUsuarioId(Long usuarioId);

}
