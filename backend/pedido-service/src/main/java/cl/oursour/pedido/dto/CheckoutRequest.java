package cl.oursour.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
		@NotNull Long usuarioId,
		@NotNull @Valid DireccionDespachoDto direccion,
		@NotNull @Valid PagoRequest pago) {
}
