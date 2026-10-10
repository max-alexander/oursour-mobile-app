package cl.oursour.receta.dto;

import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.model.Ingrediente;

public record IngredienteResponse(Long id, String nombre, CategoriaIngrediente categoria, Long productoId) {

	public static IngredienteResponse from(Ingrediente ingrediente) {
		return new IngredienteResponse(ingrediente.getId(), ingrediente.getNombre(), ingrediente.getCategoria(),
				ingrediente.getProductoId());
	}

}
