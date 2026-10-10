package cl.oursour.receta.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.receta.dto.IngredienteRecetaResponse;
import cl.oursour.receta.dto.ItemRecetaRequest;
import cl.oursour.receta.dto.RecetaDetalleResponse;
import cl.oursour.receta.dto.RecetaRequest;
import cl.oursour.receta.dto.RecetaResumenResponse;
import cl.oursour.receta.exception.RecursoNoEncontradoException;
import cl.oursour.receta.exception.ReglaNegocioException;
import cl.oursour.receta.model.ConsultaReceta;
import cl.oursour.receta.model.Ingrediente;
import cl.oursour.receta.model.Receta;
import cl.oursour.receta.repository.ConsultaRecetaRepository;
import cl.oursour.receta.repository.IngredienteRepository;
import cl.oursour.receta.repository.RecetaRepository;

@Service
public class RecetaService {

	private final RecetaRepository recetaRepository;
	private final IngredienteRepository ingredienteRepository;
	private final ConsultaRecetaRepository consultaRecetaRepository;

	public RecetaService(RecetaRepository recetaRepository, IngredienteRepository ingredienteRepository,
			ConsultaRecetaRepository consultaRecetaRepository) {
		this.recetaRepository = recetaRepository;
		this.ingredienteRepository = ingredienteRepository;
		this.consultaRecetaRepository = consultaRecetaRepository;
	}

	@Transactional(readOnly = true)
	public List<RecetaResumenResponse> listar() {
		return recetaRepository.findAllByOrderByTitulo().stream().map(RecetaResumenResponse::from).toList();
	}

	@Transactional
	public RecetaDetalleResponse detalle(Long id, Set<Long> ingredientesDisponibles) {
		Receta receta = recetaRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Receta " + id + " no encontrada"));
		consultaRecetaRepository.save(new ConsultaReceta(receta));
		return toDetalle(receta, ingredientesDisponibles);
	}

	@Transactional
	public RecetaDetalleResponse crear(RecetaRequest request) {
		if (recetaRepository.existsByTituloIgnoreCase(request.titulo().trim())) {
			throw new ReglaNegocioException("Ya existe la receta " + request.titulo());
		}
		Set<Long> ids = request.ingredientes().stream().map(ItemRecetaRequest::ingredienteId)
				.collect(Collectors.toSet());
		if (ids.size() != request.ingredientes().size()) {
			throw new ReglaNegocioException("La receta tiene ingredientes repetidos");
		}
		Map<Long, Ingrediente> ingredientes = ingredienteRepository.findAllById(ids).stream()
				.collect(Collectors.toMap(Ingrediente::getId, Function.identity()));
		Set<Long> inexistentes = new HashSet<>(ids);
		inexistentes.removeAll(ingredientes.keySet());
		if (!inexistentes.isEmpty()) {
			throw new RecursoNoEncontradoException("Ingredientes no encontrados: " + inexistentes);
		}

		Receta receta = new Receta();
		receta.setTitulo(request.titulo().trim());
		receta.setDescripcion(request.descripcion());
		receta.setSinAlcohol(Boolean.TRUE.equals(request.sinAlcohol()));
		receta.setProductoId(request.productoId());
		receta.setPasos(request.pasos().stream().map(String::trim).collect(Collectors.toList()));
		request.ingredientes().forEach(item -> receta.agregarIngrediente(ingredientes.get(item.ingredienteId()),
				item.cantidad()));
		return toDetalle(recetaRepository.save(receta), null);
	}

	private RecetaDetalleResponse toDetalle(Receta receta, Set<Long> ingredientesDisponibles) {
		List<IngredienteRecetaResponse> ingredientes = receta.getIngredientes().stream()
				.map(item -> IngredienteRecetaResponse.from(item, ingredientesDisponibles == null ? null
						: ingredientesDisponibles.contains(item.getIngrediente().getId())))
				.toList();
		return new RecetaDetalleResponse(receta.getId(), receta.getTitulo(), receta.getDescripcion(),
				receta.getSinAlcohol(), receta.getProductoId(), ingredientes, List.copyOf(receta.getPasos()));
	}

}
