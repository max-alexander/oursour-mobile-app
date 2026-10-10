package cl.oursour.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PagoSimuladoServiceTest {

	private final PagoSimuladoService pagoService = new PagoSimuladoService();

	@Test
	void tarjetaValida_apruebaConCodigoSandbox() {
		var resultado = pagoService.procesar("4111111111111111", 15000);

		assertThat(resultado.aprobado()).isTrue();
		assertThat(resultado.codigoAutorizacion()).startsWith("SBX-");
	}

	@Test
	void tarjetaTerminadaEn0000_esRechazada() {
		var resultado = pagoService.procesar("4111111111110000", 15000);

		assertThat(resultado.aprobado()).isFalse();
		assertThat(resultado.codigoAutorizacion()).isNull();
	}

	@Test
	void montoCero_esRechazado() {
		assertThat(pagoService.procesar("4111111111111111", 0).aprobado()).isFalse();
	}

}
