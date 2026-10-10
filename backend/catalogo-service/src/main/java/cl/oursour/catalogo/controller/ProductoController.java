package cl.oursour.catalogo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.catalogo.dto.ProductoRequest;
import cl.oursour.catalogo.dto.ProductoResponse;
import cl.oursour.catalogo.dto.StockRequest;
import cl.oursour.catalogo.service.ProductoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/catalogo/productos")
public class ProductoController {

	private final ProductoService productoService;

	public ProductoController(ProductoService productoService) {
		this.productoService = productoService;
	}

	@GetMapping
	public List<ProductoResponse> listar() {
		return productoService.listar();
	}

	@GetMapping("/{id}")
	public ProductoResponse obtener(@PathVariable Long id) {
		return productoService.obtener(id);
	}

	// TODO: restringir a rol ADMIN
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponse crear(@Valid @RequestBody ProductoRequest request) {
		return productoService.crear(request);
	}

	// TODO: restringir a rol ADMIN
	@PutMapping("/{id}")
	public ProductoResponse actualizar(@PathVariable Long id, @Valid @RequestBody ProductoRequest request) {
		return productoService.actualizar(id, request);
	}

	// TODO: restringir a rol ADMIN
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminar(@PathVariable Long id) {
		productoService.eliminar(id);
	}

	@PostMapping("/{id}/descontar-stock")
	public ProductoResponse descontarStock(@PathVariable Long id, @Valid @RequestBody StockRequest request) {
		return productoService.descontarStock(id, request.cantidad());
	}

}
