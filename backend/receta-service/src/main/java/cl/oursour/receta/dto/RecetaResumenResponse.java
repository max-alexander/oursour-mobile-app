package cl.oursour.receta.dto;

import cl.oursour.receta.model.Receta;

public record RecetaResumenResponse(Long id, String titulo, String descripcion, boolean sinAlcohol, Long productoId,
		int cantidadIngredientes) {

	public static RecetaResumenResponse from(Receta receta) {
		return new RecetaResumenResponse(receta.getId(), receta.getTitulo(), receta.getDescripcion(),
				receta.getSinAlcohol(), receta.getProductoId(), receta.getIngredientes().size());
	}

}
