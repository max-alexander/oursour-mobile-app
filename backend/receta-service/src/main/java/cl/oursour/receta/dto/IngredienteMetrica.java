package cl.oursour.receta.dto;

import cl.oursour.receta.model.CategoriaIngrediente;

public record IngredienteMetrica(Long id, String nombre, CategoriaIngrediente categoria, Long vecesSeleccionado) {
}
