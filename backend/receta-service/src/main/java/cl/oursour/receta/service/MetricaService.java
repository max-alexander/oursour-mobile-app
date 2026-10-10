package cl.oursour.receta.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.receta.dto.IngredienteMetrica;
import cl.oursour.receta.dto.RecetaMetrica;
import cl.oursour.receta.repository.BusquedaRepository;
import cl.oursour.receta.repository.ConsultaRecetaRepository;

@Service
@Transactional(readOnly = true)
public class MetricaService {

	private final BusquedaRepository busquedaRepository;
	private final ConsultaRecetaRepository consultaRecetaRepository;

	public MetricaService(BusquedaRepository busquedaRepository, ConsultaRecetaRepository consultaRecetaRepository) {
		this.busquedaRepository = busquedaRepository;
		this.consultaRecetaRepository = consultaRecetaRepository;
	}

	public List<IngredienteMetrica> ingredientesMasSeleccionados(int limite) {
		return busquedaRepository.ingredientesMasSeleccionados(PageRequest.of(0, limite));
	}

	public List<RecetaMetrica> recetasMasConsultadas(int limite) {
		return consultaRecetaRepository.recetasMasConsultadas(PageRequest.of(0, limite));
	}

}
