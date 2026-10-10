package cl.oursour.receta.dto;

import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.model.RecetaIngrediente;

public record IngredienteRecetaResponse(Long id, String nombre, CategoriaIngrediente categoria, String cantidad,
		Long productoId, Boolean disponible) {

	public static IngredienteRecetaResponse from(RecetaIngrediente item, Boolean disponible) {
		var ingrediente = item.getIngrediente();
		return new IngredienteRecetaResponse(ingrediente.getId(), ingrediente.getNombre(), ingrediente.getCategoria(),
				item.getCantidad(), ingrediente.getProductoId(), disponible);
	}

}
