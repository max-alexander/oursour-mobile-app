package cl.oursour.pedido.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PagoRequest(
		@NotBlank @Pattern(regexp = "\\d{16}", message = "debe tener 16 dígitos") String numeroTarjeta,
		@NotBlank @Size(max = 100) String titular) {
}
