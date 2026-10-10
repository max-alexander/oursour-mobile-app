package cl.oursour.usuario.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cl.oursour.usuario.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey clave;
	private final Duration expiracion;

	public JwtService(@Value("${jwt.secret}") String secreto,
			@Value("${jwt.expiracion-minutos:120}") long expiracionMinutos) {
		this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
		this.expiracion = Duration.ofMinutes(expiracionMinutos);
	}

	public String generarToken(Usuario usuario) {
		Instant ahora = Instant.now();
		return Jwts.builder()
				.subject(usuario.getEmail())
				.claim("id", usuario.getId())
				.claim("nombre", usuario.getNombre())
				.claim("rol", usuario.getRol().name())
				.issuedAt(Date.from(ahora))
				.expiration(Date.from(ahora.plus(expiracion)))
				.signWith(clave)
				.compact();
	}

	public Claims validarToken(String token) {
		return Jwts.parser().verifyWith(clave).build().parseSignedClaims(token).getPayload();
	}

	public long getExpiracionEnSegundos() {
		return expiracion.toSeconds();
	}

}
