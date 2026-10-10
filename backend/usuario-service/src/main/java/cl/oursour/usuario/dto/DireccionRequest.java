package cl.oursour.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionRequest(
		@NotBlank @Size(max = 150) String calle,
		@NotBlank @Size(max = 20) String numero,
		@NotBlank @Size(max = 80) String comuna,
		@NotBlank @Size(max = 80) String ciudad,
		@Size(max = 200) String referencia) {
}
