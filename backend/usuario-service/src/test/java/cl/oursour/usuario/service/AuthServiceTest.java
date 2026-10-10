package cl.oursour.usuario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.oursour.usuario.dto.LoginRequest;
import cl.oursour.usuario.dto.RegistroRequest;
import cl.oursour.usuario.exception.CredencialesInvalidasException;
import cl.oursour.usuario.exception.ReglaNegocioException;
import cl.oursour.usuario.model.Rol;
import cl.oursour.usuario.model.Usuario;
import cl.oursour.usuario.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UsuarioRepository usuarioRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final JwtService jwtService = new JwtService("clave-de-pruebas-con-al-menos-32-caracteres", 60);
	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(usuarioRepository, passwordEncoder, jwtService);
	}

	private Usuario usuario(String password) {
		Usuario usuario = new Usuario();
		usuario.setId(5L);
		usuario.setNombre("Ana");
		usuario.setEmail("ana@correo.cl");
		usuario.setPassword(passwordEncoder.encode(password));
		return usuario;
	}

	@Test
	void registrar_guardaPasswordCifradaYRolCliente() {
		when(usuarioRepository.existsByEmail("ana@correo.cl")).thenReturn(false);
		when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> {
			Usuario guardado = invocacion.getArgument(0);
			guardado.setId(5L);
			return guardado;
		});

		var respuesta = authService.registrar(new RegistroRequest("Ana", "  Ana@Correo.cl ", "secreta123"));

		assertThat(respuesta.usuario().email()).isEqualTo("ana@correo.cl");
		assertThat(respuesta.usuario().rol()).isEqualTo(Rol.CLIENTE);
		assertThat(respuesta.token()).isNotBlank();
		verify(usuarioRepository).save(org.mockito.ArgumentMatchers.argThat(
				guardado -> !guardado.getPassword().equals("secreta123")
						&& passwordEncoder.matches("secreta123", guardado.getPassword())));
	}

	@Test
	void registrar_emailDuplicado_lanzaExcepcion() {
		when(usuarioRepository.existsByEmail("ana@correo.cl")).thenReturn(true);

		assertThatThrownBy(() -> authService.registrar(new RegistroRequest("Ana", "ana@correo.cl", "secreta123")))
				.isInstanceOf(ReglaNegocioException.class);
		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void login_credencialesCorrectas_devuelveTokenValido() {
		when(usuarioRepository.findByEmail("ana@correo.cl")).thenReturn(Optional.of(usuario("secreta123")));

		var respuesta = authService.login(new LoginRequest("ana@correo.cl", "secreta123"));

		var claims = jwtService.validarToken(respuesta.token());
		assertThat(claims.getSubject()).isEqualTo("ana@correo.cl");
		assertThat(claims.get("rol", String.class)).isEqualTo("CLIENTE");
		assertThat(claims.get("id", Long.class)).isEqualTo(5L);
	}

	@Test
	void login_passwordIncorrecta_lanzaCredencialesInvalidas() {
		when(usuarioRepository.findByEmail("ana@correo.cl")).thenReturn(Optional.of(usuario("secreta123")));

		assertThatThrownBy(() -> authService.login(new LoginRequest("ana@correo.cl", "otra")))
				.isInstanceOf(CredencialesInvalidasException.class);
	}

	@Test
	void login_usuarioInexistente_lanzaCredencialesInvalidas() {
		when(usuarioRepository.findByEmail("nadie@correo.cl")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.login(new LoginRequest("nadie@correo.cl", "secreta123")))
				.isInstanceOf(CredencialesInvalidasException.class);
	}

}
