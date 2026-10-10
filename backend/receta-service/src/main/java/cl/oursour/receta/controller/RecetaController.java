package cl.oursour.receta.controller;

import java.util.HashSet;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.receta.dto.RecetaDetalleResponse;
import cl.oursour.receta.dto.RecetaRequest;
import cl.oursour.receta.dto.RecetaResumenResponse;
import cl.oursour.receta.dto.RecomendacionRequest;
import cl.oursour.receta.dto.RecomendacionResponse;
import cl.oursour.receta.service.RecetaService;
import cl.oursour.receta.service.RecomendacionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

	private final RecetaService recetaService;
	private final RecomendacionService recomendacionService;

	public RecetaController(RecetaService recetaService, RecomendacionService recomendacionService) {
		this.recetaService = recetaService;
		this.recomendacionService = recomendacionService;
	}

	@GetMapping
	public List<RecetaResumenResponse> listar() {
		return recetaService.listar();
	}

	@GetMapping("/{id}")
	public RecetaDetalleResponse detalle(@PathVariable Long id,
			@RequestParam(name = "ingredientes", required = false) List<Long> ingredientesDisponibles) {
		return recetaService.detalle(id, ingredientesDisponibles == null ? null : new HashSet<>(ingredientesDisponibles));
	}

	// TODO: restringir a rol ADMIN
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RecetaDetalleResponse crear(@Valid @RequestBody RecetaRequest request) {
		return recetaService.crear(request);
	}

	@PostMapping("/recomendaciones")
	public List<RecomendacionResponse> recomendar(@Valid @RequestBody RecomendacionRequest request) {
		return recomendacionService.recomendar(request);
	}

}
