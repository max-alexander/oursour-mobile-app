package cl.oursour.catalogo.dto;

import cl.oursour.catalogo.model.Producto;

public record ProductoResponse(Long id, String nombre, String descripcion, Integer precio, Integer stock, boolean disponible) {

	public static ProductoResponse from(Producto producto) {
		return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getDescripcion(),
				producto.getPrecio(), producto.getStock(), producto.getActivo() && producto.getStock() > 0);
	}

}
