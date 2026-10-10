package cl.oursour.pedido.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CalculadoraDespacho {

	private final int costoDespacho;
	private final int envioGratisDesde;

	public CalculadoraDespacho(@Value("${despacho.costo:3990}") int costoDespacho,
			@Value("${despacho.envio-gratis-desde:50000}") int envioGratisDesde) {
		this.costoDespacho = costoDespacho;
		this.envioGratisDesde = envioGratisDesde;
	}

	public int calcular(int subtotal) {
		if (subtotal <= 0 || subtotal >= envioGratisDesde) {
			return 0;
		}
		return costoDespacho;
	}

	public int faltaParaEnvioGratis(int subtotal) {
		return Math.max(0, envioGratisDesde - subtotal);
	}

}
