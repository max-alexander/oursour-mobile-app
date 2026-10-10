package cl.oursour.pedido.controller;

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

import cl.oursour.pedido.dto.AgregarItemRequest;
import cl.oursour.pedido.dto.CantidadRequest;
import cl.oursour.pedido.dto.CarroResponse;
import cl.oursour.pedido.service.CarroService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos/carro/{usuarioId}")
public class CarroController {

	private final CarroService carroService;

	public CarroController(CarroService carroService) {
		this.carroService = carroService;
	}

	@GetMapping
	public CarroResponse obtener(@PathVariable Long usuarioId) {
		return carroService.obtener(usuarioId);
	}

	@PostMapping("/items")
	public CarroResponse agregar(@PathVariable Long usuarioId, @Valid @RequestBody AgregarItemRequest request) {
		return carroService.agregar(usuarioId, request.productoId(), request.cantidad());
	}

	@PutMapping("/items/{productoId}")
	public CarroResponse cambiarCantidad(@PathVariable Long usuarioId, @PathVariable Long productoId,
			@Valid @RequestBody CantidadRequest request) {
		return carroService.cambiarCantidad(usuarioId, productoId, request.cantidad());
	}

	@DeleteMapping("/items/{productoId}")
	public CarroResponse eliminarItem(@PathVariable Long usuarioId, @PathVariable Long productoId) {
		return carroService.eliminarItem(usuarioId, productoId);
	}

	@DeleteMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void vaciar(@PathVariable Long usuarioId) {
		carroService.vaciar(usuarioId);
	}

}
