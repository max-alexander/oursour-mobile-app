package cl.oursour.pedido.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.oursour.pedido.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

	List<Pedido> findAllByOrderByFechaDesc();

}
