package cl.oursour.receta.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.receta.dto.IngredienteRequest;
import cl.oursour.receta.dto.IngredienteResponse;
import cl.oursour.receta.exception.ReglaNegocioException;
import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.model.Ingrediente;
import cl.oursour.receta.repository.IngredienteRepository;

@Service
public class IngredienteService {

	private final IngredienteRepository ingredienteRepository;

	public IngredienteService(IngredienteRepository ingredienteRepository) {
		this.ingredienteRepository = ingredienteRepository;
	}

	@Transactional(readOnly = true)
	public List<IngredienteResponse> listar(CategoriaIngrediente categoria) {
		List<Ingrediente> ingredientes = categoria == null
				? ingredienteRepository.findAllByOrderByCategoriaAscNombreAsc()
				: ingredienteRepository.findByCategoriaOrderByNombre(categoria);
		return ingredientes.stream().map(IngredienteResponse::from).toList();
	}

	@Transactional
	public IngredienteResponse crear(IngredienteRequest request) {
		if (ingredienteRepository.existsByNombreIgnoreCase(request.nombre().trim())) {
			throw new ReglaNegocioException("Ya existe el ingrediente " + request.nombre());
		}
		if (request.categoria() == CategoriaIngrediente.OUR_SOUR && request.productoId() == null) {
			throw new ReglaNegocioException("Un ingrediente Our Sour debe indicar el producto asociado");
		}
		Ingrediente ingrediente = new Ingrediente();
		ingrediente.setNombre(request.nombre().trim());
		ingrediente.setCategoria(request.categoria());
		ingrediente.setProductoId(request.productoId());
		return IngredienteResponse.from(ingredienteRepository.save(ingrediente));
	}

}
