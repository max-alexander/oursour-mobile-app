package cl.oursour.pedido.dto;

import cl.oursour.pedido.model.CarroItem;

public record CarroItemResponse(Long productoId, String nombreProducto, int precioUnitario, int cantidad,
		int subtotal) {

	public static CarroItemResponse from(CarroItem item) {
		return new CarroItemResponse(item.getProductoId(), item.getNombreProducto(), item.getPrecioUnitario(),
				item.getCantidad(), item.subtotal());
	}

}
