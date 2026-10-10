package cl.oursour.receta.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record RecetaRequest(
		@NotBlank @Size(max = 120) String titulo,
		@Size(max = 500) String descripcion,
		Boolean sinAlcohol,
		Long productoId,
		@NotEmpty List<@NotBlank @Size(max = 500) String> pasos,
		@NotEmpty List<@Valid ItemRecetaRequest> ingredientes) {
}
