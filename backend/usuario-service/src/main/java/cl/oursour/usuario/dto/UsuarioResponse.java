package cl.oursour.usuario.dto;

import java.time.LocalDateTime;

import cl.oursour.usuario.model.Rol;
import cl.oursour.usuario.model.Usuario;

public record UsuarioResponse(Long id, String nombre, String email, Rol rol, LocalDateTime fechaRegistro) {

	public static UsuarioResponse from(Usuario usuario) {
		return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getRol(),
				usuario.getFechaRegistro());
	}

}
