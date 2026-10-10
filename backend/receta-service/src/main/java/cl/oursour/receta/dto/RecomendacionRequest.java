package cl.oursour.receta.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RecomendacionRequest(@NotEmpty List<@NotNull Long> ingredienteIds, Boolean soloSinAlcohol) {

	public boolean filtrarSinAlcohol() {
		return Boolean.TRUE.equals(soloSinAlcohol);
	}

}
