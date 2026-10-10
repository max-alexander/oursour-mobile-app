package cl.oursour.pedido.dto;

import java.util.List;

public record CarroResponse(Long usuarioId, List<CarroItemResponse> items, int cantidadProductos, int subtotal,
		int costoDespacho, int total, int faltaParaEnvioGratis) {
}
