package cl.oursour.pedido.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.pedido.client.CatalogoClient;
import cl.oursour.pedido.client.ProductoCatalogo;
import cl.oursour.pedido.dto.CheckoutRequest;
import cl.oursour.pedido.dto.PedidoResponse;
import cl.oursour.pedido.exception.PagoRechazadoException;
import cl.oursour.pedido.exception.RecursoNoEncontradoException;
import cl.oursour.pedido.exception.ReglaNegocioException;
import cl.oursour.pedido.model.Carro;
import cl.oursour.pedido.model.CarroItem;
import cl.oursour.pedido.model.EstadoPedido;
import cl.oursour.pedido.model.Pedido;
import cl.oursour.pedido.repository.CarroRepository;
import cl.oursour.pedido.repository.PedidoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final CarroRepository carroRepository;
	private final CatalogoClient catalogoClient;
	private final CalculadoraDespacho calculadoraDespacho;
	private final PagoSimuladoService pagoSimuladoService;

	public PedidoService(PedidoRepository pedidoRepository, CarroRepository carroRepository,
			CatalogoClient catalogoClient, CalculadoraDespacho calculadoraDespacho,
			PagoSimuladoService pagoSimuladoService) {
		this.pedidoRepository = pedidoRepository;
		this.carroRepository = carroRepository;
		this.catalogoClient = catalogoClient;
		this.calculadoraDespacho = calculadoraDespacho;
		this.pagoSimuladoService = pagoSimuladoService;
	}

	@Transactional
	public PedidoResponse checkout(CheckoutRequest request) {
		Carro carro = carroRepository.findByUsuarioId(request.usuarioId())
				.filter(encontrado -> !encontrado.getItems().isEmpty())
				.orElseThrow(() -> new ReglaNegocioException("El carro está vacío"));

		Pedido pedido = new Pedido();
		pedido.setUsuarioId(request.usuarioId());
		pedido.setDireccion(request.direccion().toModel());
		for (CarroItem item : carro.getItems()) {
			ProductoCatalogo producto = catalogoClient.obtenerProducto(item.getProductoId());
			if (!Boolean.TRUE.equals(producto.disponible()) || producto.stock() < item.getCantidad()) {
				throw new ReglaNegocioException("Stock insuficiente para " + producto.nombre()
						+ " (disponible: " + producto.stock() + ")");
			}
			pedido.agregarItem(producto.id(), producto.nombre(), producto.precio(), item.getCantidad());
		}

		int subtotal = pedido.getItems().stream().mapToInt(item -> item.getSubtotal()).sum();
		int despacho = calculadoraDespacho.calcular(subtotal);
		pedido.setSubtotal(subtotal);
		pedido.setCostoDespacho(despacho);
		pedido.setTotal(subtotal + despacho);

		var pago = pagoSimuladoService.procesar(request.pago().numeroTarjeta(), pedido.getTotal());
		if (!pago.aprobado()) {
			throw new PagoRechazadoException(pago.mensaje());
		}
		pedido.setCodigoPago(pago.codigoAutorizacion());

		pedido.getItems().forEach(item -> catalogoClient.descontarStock(item.getProductoId(), item.getCantidad()));
		carro.getItems().clear();
		carro.tocar();
		return PedidoResponse.from(pedidoRepository.save(pedido));
	}

	@Transactional(readOnly = true)
	public List<PedidoResponse> listar(Long usuarioId) {
		List<Pedido> pedidos = usuarioId == null
				? pedidoRepository.findAllByOrderByFechaDesc()
				: pedidoRepository.findByUsuarioIdOrderByFechaDesc(usuarioId);
		return pedidos.stream().map(PedidoResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public PedidoResponse obtener(Long id) {
		return PedidoResponse.from(buscar(id));
	}

	@Transactional
	public PedidoResponse cambiarEstado(Long id, EstadoPedido nuevoEstado) {
		Pedido pedido = buscar(id);
		if (nuevoEstado.ordinal() <= pedido.getEstado().ordinal()) {
			throw new ReglaNegocioException("No se puede pasar de " + pedido.getEstado() + " a " + nuevoEstado);
		}
		pedido.setEstado(nuevoEstado);
		return PedidoResponse.from(pedido);
	}

	private Pedido buscar(Long id) {
		return pedidoRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Pedido " + id + " no encontrado"));
	}

}
