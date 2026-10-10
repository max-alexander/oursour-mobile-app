package cl.oursour.usuario.service;

import java.util.Locale;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.usuario.dto.AuthResponse;
import cl.oursour.usuario.dto.LoginRequest;
import cl.oursour.usuario.dto.RegistroRequest;
import cl.oursour.usuario.dto.UsuarioResponse;
import cl.oursour.usuario.exception.CredencialesInvalidasException;
import cl.oursour.usuario.exception.ReglaNegocioException;
import cl.oursour.usuario.model.Rol;
import cl.oursour.usuario.model.Usuario;
import cl.oursour.usuario.repository.UsuarioRepository;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResponse registrar(RegistroRequest request) {
		String email = normalizarEmail(request.email());
		if (usuarioRepository.existsByEmail(email)) {
			throw new ReglaNegocioException("El email " + email + " ya está registrado");
		}
		Usuario usuario = new Usuario();
		usuario.setNombre(request.nombre().trim());
		usuario.setEmail(email);
		usuario.setPassword(passwordEncoder.encode(request.password()));
		usuario.setRol(Rol.CLIENTE);
		return respuesta(usuarioRepository.save(usuario));
	}

	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		Usuario usuario = usuarioRepository.findByEmail(normalizarEmail(request.email()))
				.filter(encontrado -> passwordEncoder.matches(request.password(), encontrado.getPassword()))
				.orElseThrow(CredencialesInvalidasException::new);
		return respuesta(usuario);
	}

	private AuthResponse respuesta(Usuario usuario) {
		return new AuthResponse(jwtService.generarToken(usuario), "Bearer", jwtService.getExpiracionEnSegundos(),
				UsuarioResponse.from(usuario));
	}

	private String normalizarEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}

}
