package cl.oursour.pedido.dto;

import cl.oursour.pedido.model.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull EstadoPedido estado) {
}
