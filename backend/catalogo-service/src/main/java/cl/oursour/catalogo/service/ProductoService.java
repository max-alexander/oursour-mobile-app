package cl.oursour.catalogo.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.catalogo.dto.ProductoRequest;
import cl.oursour.catalogo.dto.ProductoResponse;
import cl.oursour.catalogo.exception.RecursoNoEncontradoException;
import cl.oursour.catalogo.exception.ReglaNegocioException;
import cl.oursour.catalogo.model.Producto;
import cl.oursour.catalogo.repository.ProductoRepository;

@Service
public class ProductoService {

	private final ProductoRepository productoRepository;

	public ProductoService(ProductoRepository productoRepository) {
		this.productoRepository = productoRepository;
	}

	@Transactional(readOnly = true)
	public List<ProductoResponse> listar() {
		return productoRepository.findByActivoTrueOrderByNombre().stream().map(ProductoResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public ProductoResponse obtener(Long id) {
		return ProductoResponse.from(buscar(id));
	}

	@Transactional
	public ProductoResponse crear(ProductoRequest request) {
		if (productoRepository.existsByNombreIgnoreCase(request.nombre())) {
			throw new ReglaNegocioException("Ya existe un producto con el nombre " + request.nombre());
		}
		Producto producto = new Producto();
		copiar(request, producto);
		return ProductoResponse.from(productoRepository.save(producto));
	}

	@Transactional
	public ProductoResponse actualizar(Long id, ProductoRequest request) {
		Producto producto = buscar(id);
		if (productoRepository.existsByNombreIgnoreCaseAndIdNot(request.nombre(), id)) {
			throw new ReglaNegocioException("Ya existe un producto con el nombre " + request.nombre());
		}
		copiar(request, producto);
		return ProductoResponse.from(producto);
	}

	@Transactional
	public void eliminar(Long id) {
		buscar(id).setActivo(false);
	}

	@Transactional
	public ProductoResponse descontarStock(Long id, int cantidad) {
		Producto producto = buscar(id);
		if (producto.getStock() < cantidad) {
			throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre()
					+ " (disponible: " + producto.getStock() + ")");
		}
		producto.setStock(producto.getStock() - cantidad);
		return ProductoResponse.from(producto);
	}

	private Producto buscar(Long id) {
		return productoRepository.findById(id)
				.filter(Producto::getActivo)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto " + id + " no encontrado"));
	}

	private void copiar(ProductoRequest request, Producto producto) {
		producto.setNombre(request.nombre().trim());
		producto.setDescripcion(request.descripcion());
		producto.setPrecio(request.precio());
		producto.setStock(request.stock());
	}

}
