package cl.oursour.catalogo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.oursour.catalogo.dto.ProductoRequest;
import cl.oursour.catalogo.exception.RecursoNoEncontradoException;
import cl.oursour.catalogo.exception.ReglaNegocioException;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

	@Mock
	private ProductoRepository productoRepository;

	@InjectMocks
	private ProductoService productoService;

	private Producto producto(long id, int stock) {
		Producto producto = new Producto();
		producto.setId(id);
		producto.setNombre("Our Sour Limón");
		producto.setPrecio(9990);
		producto.setStock(stock);
		return producto;
	}

	@Test
	void descontarStock_conStockSuficiente_restaLaCantidad() {
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1, 10)));

		var respuesta = productoService.descontarStock(1L, 3);

		assertThat(respuesta.stock()).isEqualTo(7);
	}

	@Test
	void descontarStock_sinStockSuficiente_lanzaExcepcion() {
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1, 2)));

		assertThatThrownBy(() -> productoService.descontarStock(1L, 3))
				.isInstanceOf(ReglaNegocioException.class)
				.hasMessageContaining("Stock insuficiente");
	}

	@Test
	void obtener_productoInactivo_lanzaNoEncontrado() {
		Producto inactivo = producto(1, 10);
		inactivo.setActivo(false);
		when(productoRepository.findById(1L)).thenReturn(Optional.of(inactivo));

		assertThatThrownBy(() -> productoService.obtener(1L)).isInstanceOf(RecursoNoEncontradoException.class);
	}

	@Test
	void crear_nombreDuplicado_noGuarda() {
		when(productoRepository.existsByNombreIgnoreCase("Our Sour Limón")).thenReturn(true);

		assertThatThrownBy(() -> productoService.crear(new ProductoRequest("Our Sour Limón", null, 9990, 5)))
				.isInstanceOf(ReglaNegocioException.class);
		verify(productoRepository, never()).save(any());
	}

	@Test
	void producto_sinStock_noApareceDisponible() {
		when(productoRepository.findById(1L)).thenReturn(Optional.of(producto(1, 0)));

		assertThat(productoService.obtener(1L).disponible()).isFalse();
	}

}
