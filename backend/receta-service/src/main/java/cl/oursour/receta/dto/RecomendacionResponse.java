package cl.oursour.receta.dto;

import java.util.List;

public record RecomendacionResponse(Long recetaId, String titulo, boolean sinAlcohol, int porcentajeCoincidencia,
		int totalIngredientes, Long productoId, boolean faltaProductoOurSour,
		List<IngredienteRecetaResponse> disponibles, List<IngredienteRecetaResponse> faltantes) {
}
