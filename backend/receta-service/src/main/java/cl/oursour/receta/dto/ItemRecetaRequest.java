package cl.oursour.receta.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemRecetaRequest(@NotNull Long ingredienteId, @Size(max = 60) String cantidad) {
}
