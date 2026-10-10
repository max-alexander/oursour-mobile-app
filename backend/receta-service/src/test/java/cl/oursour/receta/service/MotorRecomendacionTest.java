package cl.oursour.receta.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import cl.oursour.receta.model.CategoriaIngrediente;
import cl.oursour.receta.model.Ingrediente;
import cl.oursour.receta.model.Receta;

class MotorRecomendacionTest {

	private final MotorRecomendacion motor = new MotorRecomendacion();

	private final Ingrediente gin = ingrediente(1L, "Gin", CategoriaIngrediente.DESTILADO, null);
	private final Ingrediente tonica = ingrediente(2L, "Agua tónica", CategoriaIngrediente.MIXER, null);
	private final Ingrediente hielo = ingrediente(3L, "Hielo", CategoriaIngrediente.OTRO, null);
	private final Ingrediente ourSourLimon = ingrediente(4L, "Our Sour Limón", CategoriaIngrediente.OUR_SOUR, 1L);
	private final Ingrediente pisco = ingrediente(5L, "Pisco", CategoriaIngrediente.DESTILADO, null);

	private static Ingrediente ingrediente(Long id, String nombre, CategoriaIngrediente categoria, Long productoId) {
		Ingrediente ingrediente = new Ingrediente();
		ingrediente.setId(id);
		ingrediente.setNombre(nombre);
		ingrediente.setCategoria(categoria);
		ingrediente.setProductoId(productoId);
		return ingrediente;
	}

	private static Receta receta(String titulo, boolean sinAlcohol, Ingrediente... ingredientes) {
		Receta receta = new Receta();
		receta.setTitulo(titulo);
		receta.setSinAlcohol(sinAlcohol);
		for (Ingrediente ingrediente : ingredientes) {
			receta.agregarIngrediente(ingrediente, "1");
		}
		return receta;
	}

	@Test
	void recetaConTodosLosIngredientes_tiene100PorCiento() {
		Receta ginTonic = receta("Gin Tonic", false, gin, tonica, hielo);

		var resultado = motor.recomendar(List.of(ginTonic), Set.of(1L, 2L, 3L), false);

		assertThat(resultado).hasSize(1);
		assertThat(resultado.getFirst().porcentaje()).isEqualTo(100);
		assertThat(resultado.getFirst().faltantes()).isEmpty();
	}

	@Test
	void calculaPorcentajeEIdentificaFaltantes() {
		Receta ginSour = receta("Gin Sour", false, gin, ourSourLimon, hielo);

		var resultado = motor.recomendar(List.of(ginSour), Set.of(1L, 3L), false).getFirst();

		assertThat(resultado.porcentaje()).isEqualTo(67);
		assertThat(resultado.faltantes()).extracting(item -> item.getIngrediente().getNombre())
				.containsExactly("Our Sour Limón");
		assertThat(resultado.faltaProductoOurSour()).isTrue();
	}

	@Test
	void ordenaPorPorcentajeDeMayorAMenor() {
		Receta ginTonic = receta("Gin Tonic", false, gin, tonica, hielo);
		Receta ginSour = receta("Gin Sour", false, gin, ourSourLimon, hielo, tonica);
		Receta piscoSour = receta("Pisco Sour", false, pisco, ourSourLimon);

		var resultado = motor.recomendar(List.of(piscoSour, ginSour, ginTonic), Set.of(1L, 2L, 3L), false);

		assertThat(resultado).extracting(r -> r.receta().getTitulo()).containsExactly("Gin Tonic", "Gin Sour");
		assertThat(resultado).extracting(MotorRecomendacion.Resultado::porcentaje).containsExactly(100, 75);
	}

	@Test
	void excluyeRecetasSinNingunIngredienteDisponible() {
		Receta piscoSour = receta("Pisco Sour", false, pisco, ourSourLimon);

		assertThat(motor.recomendar(List.of(piscoSour), Set.of(1L, 2L), false)).isEmpty();
	}

	@Test
	void filtraSoloSinAlcohol() {
		Receta ginTonic = receta("Gin Tonic", false, gin, tonica, hielo);
		Receta tonicaLimon = receta("Tónica Limón", true, tonica, ourSourLimon, hielo);

		var resultado = motor.recomendar(List.of(ginTonic, tonicaLimon), Set.of(2L, 3L), true);

		assertThat(resultado).extracting(r -> r.receta().getTitulo()).containsExactly("Tónica Limón");
	}

	@Test
	void faltanteSinProductoAsociado_noMarcaFaltaOurSour() {
		Receta ginTonic = receta("Gin Tonic", false, gin, tonica, hielo);

		var resultado = motor.recomendar(List.of(ginTonic), Set.of(2L, 3L), false).getFirst();

		assertThat(resultado.faltaProductoOurSour()).isFalse();
	}

}
