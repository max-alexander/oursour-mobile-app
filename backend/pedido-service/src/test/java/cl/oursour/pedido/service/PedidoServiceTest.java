package cl.oursour.pedido.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
import cl.oursour.pedido.dto.CheckoutRequest;
import cl.oursour.pedido.dto.DireccionDespachoDto;
import cl.oursour.pedido.dto.PagoRequest;
import cl.oursour.pedido.exception.PagoRechazadoException;
import cl.oursour.pedido.exception.ReglaNegocioException;
import cl.oursour.pedido.model.Carro;
import cl.oursour.pedido.model.CarroItem;
import cl.oursour.pedido.model.EstadoPedido;
import cl.oursour.pedido.model.Pedido;
import cl.oursour.pedido.repository.CarroRepository;
import cl.oursour.pedido.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

	@Mock
	private PedidoRepository pedidoRepository;

	@Mock
	private CarroRepository carroRepository;

	@Mock
	private CatalogoClient catalogoClient;

	private PedidoService pedidoService;

	private final DireccionDespachoDto direccion = new DireccionDespachoDto("Los Leones", "100", "Providencia",
			"Santiago", null);

	@BeforeEach
	void setUp() {
		pedidoService = new PedidoService(pedidoRepository, carroRepository, catalogoClient,
				new CalculadoraDespacho(3990, 50000), new PagoSimuladoService());
	}

	private Carro carroCon(int cantidad) {
		Carro carro = new Carro(7L);
		CarroItem item = new CarroItem();
		item.setCarro(carro);
		item.setProductoId(1L);
		item.setNombreProducto("Our Sour Limón");
		item.setPrecioUnitario(9990);
		item.setCantidad(cantidad);
		carro.getItems().add(item);
		return carro;
	}

	@Test
	void checkout_pagoAprobado_creaPedidoDescuentaStockYVaciaCarro() {
		Carro carro = carroCon(6);
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.of(carro));
		when(catalogoClient.obtenerProducto(1L)).thenReturn(new ProductoCatalogo(1L, "Our Sour Limón", 9990, 10, true));
		when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

		var pedido = pedidoService.checkout(
				new CheckoutRequest(7L, direccion, new PagoRequest("4111111111111111", "Ana")));

		assertThat(pedido.estado()).isEqualTo(EstadoPedido.SOLICITADO);
		assertThat(pedido.subtotal()).isEqualTo(59940);
		assertThat(pedido.costoDespacho()).isZero();
		assertThat(pedido.total()).isEqualTo(59940);
		assertThat(pedido.codigoPago()).startsWith("SBX-");
		verify(catalogoClient).descontarStock(1L, 6);
		assertThat(carro.getItems()).isEmpty();
	}

	@Test
	void checkout_pagoRechazado_noDescuentaStockNiGuarda() {
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.of(carroCon(1)));
		when(catalogoClient.obtenerProducto(1L)).thenReturn(new ProductoCatalogo(1L, "Our Sour Limón", 9990, 10, true));

		assertThatThrownBy(() -> pedidoService.checkout(
				new CheckoutRequest(7L, direccion, new PagoRequest("4111111111110000", "Ana"))))
				.isInstanceOf(PagoRechazadoException.class);
		verify(catalogoClient, never()).descontarStock(anyLong(), anyInt());
		verify(pedidoRepository, never()).save(any());
	}

	@Test
	void checkout_carroVacio_lanzaExcepcion() {
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.of(new Carro(7L)));

		assertThatThrownBy(() -> pedidoService.checkout(
				new CheckoutRequest(7L, direccion, new PagoRequest("4111111111111111", "Ana"))))
				.isInstanceOf(ReglaNegocioException.class)
				.hasMessageContaining("vacío");
	}

	@Test
	void checkout_stockInsuficiente_noCobra() {
		when(carroRepository.findByUsuarioId(7L)).thenReturn(Optional.of(carroCon(5)));
		when(catalogoClient.obtenerProducto(1L)).thenReturn(new ProductoCatalogo(1L, "Our Sour Limón", 9990, 3, true));

		assertThatThrownBy(() -> pedidoService.checkout(
				new CheckoutRequest(7L, direccion, new PagoRequest("4111111111111111", "Ana"))))
				.isInstanceOf(ReglaNegocioException.class)
				.hasMessageContaining("Stock insuficiente");
		verify(pedidoRepository, never()).save(any());
	}

	@Test
	void cambiarEstado_haciaAtras_lanzaExcepcion() {
		Pedido pedido = new Pedido();
		pedido.setEstado(EstadoPedido.DESPACHADO);
		when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

		assertThatThrownBy(() -> pedidoService.cambiarEstado(1L, EstadoPedido.EN_PREPARACION))
				.isInstanceOf(ReglaNegocioException.class);
	}

}
