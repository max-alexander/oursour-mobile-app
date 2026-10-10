package cl.oursour.receta.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.oursour.receta.dto.RecomendacionRequest;
import cl.oursour.receta.exception.RecursoNoEncontradoException;
import cl.oursour.receta.model.Busqueda;
import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.model.Ingrediente;
import cl.oursour.receta.model.Receta;
import cl.oursour.receta.repository.BusquedaRepository;
import cl.oursour.receta.repository.IngredienteRepository;
import cl.oursour.receta.repository.RecetaRepository;

@ExtendWith(MockitoExtension.class)
class RecomendacionServiceTest {

	@Mock
	private RecetaRepository recetaRepository;

	@Mock
	private IngredienteRepository ingredienteRepository;

	@Mock
	private BusquedaRepository busquedaRepository;

	@Spy
	private MotorRecomendacion motor;

	@InjectMocks
	private RecomendacionService recomendacionService;

	private static Ingrediente ingrediente(Long id, String nombre) {
		Ingrediente ingrediente = new Ingrediente();
		ingrediente.setId(id);
		ingrediente.setNombre(nombre);
		ingrediente.setCategoria(CategoriaIngrediente.OTRO);
		return ingrediente;
	}

	@Test
	void recomendar_registraLaBusquedaConSusIngredientes() {
		Ingrediente gin = ingrediente(1L, "Gin");
		Ingrediente tonica = ingrediente(2L, "Agua tónica");
		Receta ginTonic = new Receta();
		ginTonic.setId(10L);
		ginTonic.setTitulo("Gin Tonic");
		ginTonic.agregarIngrediente(gin, "50 ml");
		ginTonic.agregarIngrediente(tonica, "150 ml");
		when(ingredienteRepository.findAllById(anyIterable())).thenReturn(List.of(gin));
		when(recetaRepository.findAllByOrderByTitulo()).thenReturn(List.of(ginTonic));

		var resultado = recomendacionService.recomendar(new RecomendacionRequest(List.of(1L), false));

		assertThat(resultado).hasSize(1);
		assertThat(resultado.getFirst().porcentajeCoincidencia()).isEqualTo(50);
		ArgumentCaptor<Busqueda> busqueda = ArgumentCaptor.forClass(Busqueda.class);
		verify(busquedaRepository).save(busqueda.capture());
		assertThat(busqueda.getValue().getIngredientes()).containsExactly(gin);
		assertThat(busqueda.getValue().getResultados()).isEqualTo(1);
	}

	@Test
	void recomendar_conIngredienteInexistente_lanzaExcepcionYNoRegistra() {
		when(ingredienteRepository.findAllById(anyIterable())).thenReturn(List.of(ingrediente(1L, "Gin")));

		assertThatThrownBy(() -> recomendacionService.recomendar(new RecomendacionRequest(List.of(1L, 99L), false)))
				.isInstanceOf(RecursoNoEncontradoException.class)
				.hasMessageContaining("99");
		verify(busquedaRepository, never()).save(any());
	}

}
