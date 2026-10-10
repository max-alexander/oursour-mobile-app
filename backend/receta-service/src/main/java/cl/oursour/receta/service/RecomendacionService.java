package cl.oursour.receta.service;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.receta.dto.IngredienteRecetaResponse;
import cl.oursour.receta.dto.RecomendacionRequest;
import cl.oursour.receta.dto.RecomendacionResponse;
import cl.oursour.receta.exception.RecursoNoEncontradoException;
import cl.oursour.receta.model.Busqueda;
import cl.oursour.receta.model.Ingrediente;
import cl.oursour.receta.repository.BusquedaRepository;
import cl.oursour.receta.repository.IngredienteRepository;
import cl.oursour.receta.repository.RecetaRepository;

@Service
public class RecomendacionService {

	private final RecetaRepository recetaRepository;
	private final IngredienteRepository ingredienteRepository;
	private final BusquedaRepository busquedaRepository;
	private final MotorRecomendacion motor;

	public RecomendacionService(RecetaRepository recetaRepository, IngredienteRepository ingredienteRepository,
			BusquedaRepository busquedaRepository, MotorRecomendacion motor) {
		this.recetaRepository = recetaRepository;
		this.ingredienteRepository = ingredienteRepository;
		this.busquedaRepository = busquedaRepository;
		this.motor = motor;
	}

	@Transactional
	public List<RecomendacionResponse> recomendar(RecomendacionRequest request) {
		Set<Long> ids = new LinkedHashSet<>(request.ingredienteIds());
		List<Ingrediente> ingredientes = ingredienteRepository.findAllById(ids);
		if (ingredientes.size() != ids.size()) {
			Set<Long> inexistentes = new HashSet<>(ids);
			ingredientes.forEach(ingrediente -> inexistentes.remove(ingrediente.getId()));
			throw new RecursoNoEncontradoException("Ingredientes no encontrados: " + inexistentes);
		}

		List<RecomendacionResponse> recomendaciones = motor
				.recomendar(recetaRepository.findAllByOrderByTitulo(), ids, request.filtrarSinAlcohol()).stream()
				.map(this::toResponse)
				.toList();

		Busqueda busqueda = new Busqueda();
		busqueda.getIngredientes().addAll(ingredientes);
		busqueda.setResultados(recomendaciones.size());
		busquedaRepository.save(busqueda);

		return recomendaciones;
	}

	private RecomendacionResponse toResponse(MotorRecomendacion.Resultado resultado) {
		var receta = resultado.receta();
		return new RecomendacionResponse(receta.getId(), receta.getTitulo(), receta.getSinAlcohol(),
				resultado.porcentaje(), receta.getIngredientes().size(), receta.getProductoId(),
				resultado.faltaProductoOurSour(),
				resultado.disponibles().stream().map(item -> IngredienteRecetaResponse.from(item, true)).toList(),
				resultado.faltantes().stream().map(item -> IngredienteRecetaResponse.from(item, false)).toList());
	}

}
