package cl.oursour.receta.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import cl.oursour.receta.model.Receta;
import cl.oursour.receta.model.RecetaIngrediente;

@Component
public class MotorRecomendacion {

	public record Resultado(Receta receta, List<RecetaIngrediente> disponibles, List<RecetaIngrediente> faltantes,
			int porcentaje) {

		public boolean faltaProductoOurSour() {
			return faltantes.stream().anyMatch(item -> item.getIngrediente().getProductoId() != null);
		}

	}

	public List<Resultado> recomendar(Collection<Receta> recetas, Set<Long> ingredientesDisponibles,
			boolean soloSinAlcohol) {
		return recetas.stream()
				.filter(receta -> !receta.getIngredientes().isEmpty())
				.filter(receta -> !soloSinAlcohol || receta.getSinAlcohol())
				.map(receta -> evaluar(receta, ingredientesDisponibles))
				.filter(resultado -> resultado.porcentaje() > 0)
				.sorted(Comparator.comparingInt(Resultado::porcentaje).reversed()
						.thenComparingInt(resultado -> resultado.faltantes().size())
						.thenComparing(resultado -> resultado.receta().getTitulo()))
				.toList();
	}

	Resultado evaluar(Receta receta, Set<Long> ingredientesDisponibles) {
		List<RecetaIngrediente> disponibles = receta.getIngredientes().stream()
				.filter(item -> ingredientesDisponibles.contains(item.getIngrediente().getId()))
				.toList();
		List<RecetaIngrediente> faltantes = receta.getIngredientes().stream()
				.filter(item -> !ingredientesDisponibles.contains(item.getIngrediente().getId()))
				.toList();
		int porcentaje = Math.round(disponibles.size() * 100f / receta.getIngredientes().size());
		return new Resultado(receta, disponibles, faltantes, porcentaje);
	}

}
