package cl.oursour.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.usuario.dto.DireccionRequest;
import cl.oursour.usuario.dto.DireccionResponse;
import cl.oursour.usuario.dto.UsuarioResponse;
import cl.oursour.usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;

	public UsuarioController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	// TODO: restringir a rol ADMIN
	@GetMapping
	public List<UsuarioResponse> listar() {
		return usuarioService.listar();
	}

	@GetMapping("/{id}")
	public UsuarioResponse obtener(@PathVariable Long id) {
		return usuarioService.obtener(id);
	}

	@GetMapping("/{id}/direcciones")
	public List<DireccionResponse> listarDirecciones(@PathVariable Long id) {
		return usuarioService.listarDirecciones(id);
	}

	@PostMapping("/{id}/direcciones")
	@ResponseStatus(HttpStatus.CREATED)
	public DireccionResponse agregarDireccion(@PathVariable Long id, @Valid @RequestBody DireccionRequest request) {
		return usuarioService.agregarDireccion(id, request);
	}

	@DeleteMapping("/{id}/direcciones/{direccionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void eliminarDireccion(@PathVariable Long id, @PathVariable Long direccionId) {
		usuarioService.eliminarDireccion(id, direccionId);
	}

}
