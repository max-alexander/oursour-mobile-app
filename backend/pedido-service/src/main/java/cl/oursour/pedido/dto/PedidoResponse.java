package cl.oursour.pedido.dto;

import java.time.LocalDateTime;
import java.util.List;

import cl.oursour.pedido.model.EstadoPedido;
import cl.oursour.pedido.model.Pedido;

public record PedidoResponse(Long id, Long usuarioId, EstadoPedido estado, List<PedidoItemResponse> items,
		int subtotal, int costoDespacho, int total, DireccionDespachoDto direccion, String codigoPago,
		LocalDateTime fecha) {

	public static PedidoResponse from(Pedido pedido) {
		return new PedidoResponse(pedido.getId(), pedido.getUsuarioId(), pedido.getEstado(),
				pedido.getItems().stream().map(PedidoItemResponse::from).toList(), pedido.getSubtotal(),
				pedido.getCostoDespacho(), pedido.getTotal(), DireccionDespachoDto.from(pedido.getDireccion()),
				pedido.getCodigoPago(), pedido.getFecha());
	}

}
