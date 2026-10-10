package cl.oursour.receta.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.receta.dto.IngredienteRequest;
import cl.oursour.receta.dto.IngredienteResponse;
import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.service.IngredienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recetas/ingredientes")
public class IngredienteController {

	private final IngredienteService ingredienteService;

	public IngredienteController(IngredienteService ingredienteService) {
		this.ingredienteService = ingredienteService;
	}

	@GetMapping
	public List<IngredienteResponse> listar(@RequestParam(required = false) CategoriaIngrediente categoria) {
		return ingredienteService.listar(categoria);
	}

	// TODO: restringir a rol ADMIN
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public IngredienteResponse crear(@Valid @RequestBody IngredienteRequest request) {
		return ingredienteService.crear(request);
	}

}
