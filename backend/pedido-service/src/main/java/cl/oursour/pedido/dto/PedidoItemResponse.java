package cl.oursour.pedido.dto;

import cl.oursour.pedido.model.PedidoItem;

public record PedidoItemResponse(Long productoId, String nombreProducto, int precioUnitario, int cantidad,
		int subtotal) {

	public static PedidoItemResponse from(PedidoItem item) {
		return new PedidoItemResponse(item.getProductoId(), item.getNombreProducto(), item.getPrecioUnitario(),
				item.getCantidad(), item.getSubtotal());
	}

}
