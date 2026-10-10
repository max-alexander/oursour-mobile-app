package cl.oursour.pedido.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PagoSimuladoService {

	public static final String SUFIJO_TARJETA_RECHAZADA = "0000";

	public record ResultadoPago(boolean aprobado, String codigoAutorizacion, String mensaje) {
	}

	public ResultadoPago procesar(String numeroTarjeta, int monto) {
		if (monto <= 0) {
			return new ResultadoPago(false, null, "Monto inválido");
		}
		if (numeroTarjeta.endsWith(SUFIJO_TARJETA_RECHAZADA)) {
			return new ResultadoPago(false, null, "Pago rechazado por el emisor (simulado)");
		}
		String codigo = "SBX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		return new ResultadoPago(true, codigo, "Pago aprobado (simulado)");
	}

}
