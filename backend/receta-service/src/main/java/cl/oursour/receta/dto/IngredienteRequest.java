package cl.oursour.receta.dto;

import cl.oursour.receta.model.CategoriaIngrediente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IngredienteRequest(
		@NotBlank @Size(max = 80) String nombre,
		@NotNull CategoriaIngrediente categoria,
		Long productoId) {
}
