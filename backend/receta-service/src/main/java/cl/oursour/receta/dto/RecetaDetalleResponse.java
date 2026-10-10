package cl.oursour.receta.dto;

import java.util.List;

public record RecetaDetalleResponse(Long id, String titulo, String descripcion, boolean sinAlcohol, Long productoId,
		List<IngredienteRecetaResponse> ingredientes, List<String> pasos) {
}
