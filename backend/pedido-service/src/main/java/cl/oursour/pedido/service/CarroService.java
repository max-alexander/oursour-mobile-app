package cl.oursour.pedido.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.pedido.client.CatalogoClient;
import cl.oursour.pedido.client.ProductoCatalogo;
import cl.oursour.pedido.dto.CarroItemResponse;
import cl.oursour.pedido.dto.CarroResponse;
import cl.oursour.pedido.exception.RecursoNoEncontradoException;
import cl.oursour.pedido.exception.ReglaNegocioException;
import cl.oursour.pedido.model.Carro;
import cl.oursour.pedido.model.CarroItem;
import cl.oursour.pedido.repository.CarroRepository;

@Service
public class CarroService {

	private final CarroRepository carroRepository;
	private final CatalogoClient catalogoClient;
	private final CalculadoraDespacho calculadoraDespacho;

	public CarroService(CarroRepository carroRepository, CatalogoClient catalogoClient,
			CalculadoraDespacho calculadoraDespacho) {
		this.carroRepository = carroRepository;
		this.catalogoClient = catalogoClient;
		this.calculadoraDespacho = calculadoraDespacho;
	}

	@Transactional(readOnly = true)
	public CarroResponse obtener(Long usuarioId) {
		return carroRepository.findByUsuarioId(usuarioId)
				.map(this::toResponse)
				.orElseGet(() -> toResponse(new Carro(usuarioId)));
	}

	@Transactional
	public CarroResponse agregar(Long usuarioId, Long productoId, int cantidad) {
		Carro carro = carroRepository.findByUsuarioId(usuarioId).orElseGet(() -> new Carro(usuarioId));
		ProductoCatalogo producto = catalogoClient.obtenerProducto(productoId);
		CarroItem item = carro.buscarItem(productoId).orElseGet(() -> {
			CarroItem nuevo = new CarroItem();
			nuevo.setCarro(carro);
			nuevo.setProductoId(productoId);
			nuevo.setCantidad(0);
			carro.getItems().add(nuevo);
			return nuevo;
		});
		int nuevaCantidad = item.getCantidad() + cantidad;
		validarStock(producto, nuevaCantidad);
		item.setNombreProducto(producto.nombre());
		item.setPrecioUnitario(producto.precio());
		item.setCantidad(nuevaCantidad);
		carro.tocar();
		return toResponse(carroRepository.save(carro));
	}

	@Transactional
	public CarroResponse cambiarCantidad(Long usuarioId, Long productoId, int cantidad) {
		Carro carro = buscarCarro(usuarioId);
		CarroItem item = buscarItem(carro, productoId);
		ProductoCatalogo producto = catalogoClient.obtenerProducto(productoId);
		validarStock(producto, cantidad);
		item.setPrecioUnitario(producto.precio());
		item.setCantidad(cantidad);
		carro.tocar();
		return toResponse(carro);
	}

	@Transactional
	public CarroResponse eliminarItem(Long usuarioId, Long productoId) {
		Carro carro = buscarCarro(usuarioId);
		carro.getItems().remove(buscarItem(carro, productoId));
		carro.tocar();
		return toResponse(carro);
	}

	@Transactional
	public void vaciar(Long usuarioId) {
		carroRepository.findByUsuarioId(usuarioId).ifPresent(carro -> {
			carro.getItems().clear();
			carro.tocar();
		});
	}

	private void validarStock(ProductoCatalogo producto, int cantidad) {
		if (!Boolean.TRUE.equals(producto.disponible()) || producto.stock() < cantidad) {
			throw new ReglaNegocioException("Stock insuficiente para " + producto.nombre()
					+ " (disponible: " + producto.stock() + ")");
		}
	}

	private Carro buscarCarro(Long usuarioId) {
		return carroRepository.findByUsuarioId(usuarioId)
				.orElseThrow(() -> new RecursoNoEncontradoException("El usuario " + usuarioId + " no tiene carro"));
	}

	private CarroItem buscarItem(Carro carro, Long productoId) {
		return carro.buscarItem(productoId)
				.orElseThrow(() -> new RecursoNoEncontradoException("El producto " + productoId + " no está en el carro"));
	}

	CarroResponse toResponse(Carro carro) {
		int subtotal = carro.subtotal();
		int despacho = calculadoraDespacho.calcular(subtotal);
		return new CarroResponse(carro.getUsuarioId(),
				carro.getItems().stream().map(CarroItemResponse::from).toList(),
				carro.getItems().stream().mapToInt(CarroItem::getCantidad).sum(),
				subtotal, despacho, subtotal + despacho, calculadoraDespacho.faltaParaEnvioGratis(subtotal));
	}

}
