package cl.oursour.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.oursour.pedido.client.CatalogoClient;
import cl.oursour.pedido.client.ProductoCatalogo;
import cl.oursour.pedido.exception.ReglaNegocioException;
import cl.oursour.pedido.model.Carro;
import cl.oursour.pedido.repository.CarroRepository;

@ExtendWith(MockitoExtension.class)
class CarroServiceTest {

	@Mock
	private CarroRepository carroRepository;

	@Mock
	private CatalogoClient catalogoClient;

	private CarroService carroService;

	@BeforeEach
	void setUp() {
		carroService = new CarroService(carroRepository, catalogoClient, new CalculadoraDespacho(3990, 50000));
	}

	@Test
	void agregar_productoNuevo_calculaSubtotalDespachoYTotal() {
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.empty());
		when(catalogoClient.obtenerProducto(1L)).thenReturn(new ProductoCatalogo(1L, "Our Sour Limón", 9990, 10, true));
		when(carroRepository.save(any(Carro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

		var carro = carroService.agregar(7L, 1L, 2);

		assertThat(carro.items()).hasSize(1);
		assertThat(carro.subtotal()).isEqualTo(19980);
		assertThat(carro.costoDespacho()).isEqualTo(3990);
		assertThat(carro.total()).isEqualTo(23970);
	}

	@Test
	void agregar_productoExistente_sumaCantidades() {
		Carro existente = new Carro(7L);
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.of(existente));
		when(catalogoClient.obtenerProducto(1L)).thenReturn(new ProductoCatalogo(1L, "Our Sour Limón", 9990, 10, true));
		when(carroRepository.save(any(Carro.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

		carroService.agregar(7L, 1L, 2);
		var carro = carroService.agregar(7L, 1L, 3);

		assertThat(carro.items()).hasSize(1);
		assertThat(carro.items().getFirst().cantidad()).isEqualTo(5);
	}

	@Test
	void agregar_superandoElStock_lanzaExcepcion() {
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.empty());
		when(catalogoClient.obtenerProducto(1L)).thenReturn(new ProductoCatalogo(1L, "Our Sour Limón", 9990, 2, true));

		assertThatThrownBy(() -> carroService.agregar(7L, 1L, 3)).isInstanceOf(ReglaNegocioException.class);
		verify(carroRepository, never()).save(any());
	}

	@Test
	void obtener_sinCarro_devuelveCarroVacio() {
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.empty());

		var carro = carroService.obtener(7L);

		assertThat(carro.items()).isEmpty();
		assertThat(carro.total()).isZero();
	}

}
