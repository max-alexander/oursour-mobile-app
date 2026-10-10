package cl.oursour.usuario.dto;

public record AuthResponse(String token, String tipo, long expiraEnSegundos, UsuarioResponse usuario) {
}
