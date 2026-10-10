package cl.oursour.pedido.dto;

import cl.oursour.pedido.model.DireccionDespacho;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DireccionDespachoDto(
		@NotBlank @Size(max = 150) String calle,
		@NotBlank @Size(max = 20) String numero,
		@NotBlank @Size(max = 80) String comuna,
		@NotBlank @Size(max = 80) String ciudad,
		@Size(max = 200) String referencia) {

	public static DireccionDespachoDto from(DireccionDespacho direccion) {
		return new DireccionDespachoDto(direccion.getCalle(), direccion.getNumero(), direccion.getComuna(),
				direccion.getCiudad(), direccion.getReferencia());
	}

	public DireccionDespacho toModel() {
		return new DireccionDespacho(calle.trim(), numero.trim(), comuna.trim(), ciudad.trim(), referencia);
	}

}
