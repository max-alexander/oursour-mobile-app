package cl.oursour.catalogo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductoRequest(
		@NotBlank @Size(max = 120) String nombre,
		@Size(max = 500) String descripcion,
		@NotNull @Min(1) Integer precio,
		@NotNull @Min(0) Integer stock) {
}
