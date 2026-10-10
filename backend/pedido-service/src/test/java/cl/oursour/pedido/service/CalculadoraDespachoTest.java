package cl.oursour.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CalculadoraDespachoTest {

	private final CalculadoraDespacho calculadora = new CalculadoraDespacho(3990, 50000);

	@Test
	void bajoElUmbral_cobraDespacho() {
		assertThat(calculadora.calcular(49999)).isEqualTo(3990);
	}

	@Test
	void desdeElUmbral_despachoGratis() {
		assertThat(calculadora.calcular(50000)).isZero();
		assertThat(calculadora.calcular(80000)).isZero();
	}

	@Test
	void carroVacio_noCobraDespacho() {
		assertThat(calculadora.calcular(0)).isZero();
	}

	@Test
	void calculaCuantoFaltaParaEnvioGratis() {
		assertThat(calculadora.faltaParaEnvioGratis(30000)).isEqualTo(20000);
		assertThat(calculadora.faltaParaEnvioGratis(60000)).isZero();
	}

}
