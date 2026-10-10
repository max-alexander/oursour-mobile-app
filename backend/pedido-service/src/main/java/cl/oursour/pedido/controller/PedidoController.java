package cl.oursour.pedido.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.pedido.dto.CambioEstadoRequest;
import cl.oursour.pedido.dto.CheckoutRequest;
import cl.oursour.pedido.dto.PedidoResponse;
import cl.oursour.pedido.service.PedidoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@PostMapping("/checkout")
	@ResponseStatus(HttpStatus.CREATED)
	public PedidoResponse checkout(@Valid @RequestBody CheckoutRequest request) {
		return pedidoService.checkout(request);
	}

	@GetMapping
	public List<PedidoResponse> listar(@RequestParam(required = false) Long usuarioId) {
		return pedidoService.listar(usuarioId);
	}

	@GetMapping("/{id}")
	public PedidoResponse obtener(@PathVariable Long id) {
		return pedidoService.obtener(id);
	}

	// TODO: restringir a rol ADMIN
	@PatchMapping("/{id}/estado")
	public PedidoResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest request) {
		return pedidoService.cambiarEstado(id, request.estado());
	}

}
