package cl.oursour.usuario.dto;

import cl.oursour.usuario.model.Direccion;

public record DireccionResponse(Long id, String calle, String numero, String comuna, String ciudad,
		String referencia) {

	public static DireccionResponse from(Direccion direccion) {
		return new DireccionResponse(direccion.getId(), direccion.getCalle(), direccion.getNumero(),
				direccion.getComuna(), direccion.getCiudad(), direccion.getReferencia());
	}

}
