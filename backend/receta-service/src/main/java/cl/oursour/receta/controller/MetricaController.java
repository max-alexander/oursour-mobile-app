package cl.oursour.receta.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.oursour.receta.dto.IngredienteMetrica;
import cl.oursour.receta.dto.RecetaMetrica;
import cl.oursour.receta.service.MetricaService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

// TODO: restringir a rol ADMIN
@RestController
@RequestMapping("/api/recetas/metricas")
public class MetricaController {

	private final MetricaService metricaService;

	public MetricaController(MetricaService metricaService) {
		this.metricaService = metricaService;
	}

	@GetMapping("/ingredientes")
	public List<IngredienteMetrica> ingredientesMasSeleccionados(
			@RequestParam(defaultValue = "10") @Min(1) @Max(50) int limite) {
		return metricaService.ingredientesMasSeleccionados(limite);
	}

	@GetMapping("/recetas")
	public List<RecetaMetrica> recetasMasConsultadas(@RequestParam(defaultValue = "10") @Min(1) @Max(50) int limite) {
		return metricaService.recetasMasConsultadas(limite);
	}

}
