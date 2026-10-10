package cl.oursour.usuario.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.oursour.usuario.dto.DireccionRequest;
import cl.oursour.usuario.dto.DireccionResponse;
import cl.oursour.usuario.dto.UsuarioResponse;
import cl.oursour.usuario.exception.RecursoNoEncontradoException;
import cl.oursour.usuario.model.Direccion;
import cl.oursour.usuario.model.Usuario;
import cl.oursour.usuario.repository.DireccionRepository;
import cl.oursour.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

	private final UsuarioRepository usuarioRepository;
	private final DireccionRepository direccionRepository;

	public UsuarioService(UsuarioRepository usuarioRepository, DireccionRepository direccionRepository) {
		this.usuarioRepository = usuarioRepository;
		this.direccionRepository = direccionRepository;
	}

	@Transactional(readOnly = true)
	public List<UsuarioResponse> listar() {
		return usuarioRepository.findAll().stream().map(UsuarioResponse::from).toList();
	}

	@Transactional(readOnly = true)
	public UsuarioResponse obtener(Long id) {
		return UsuarioResponse.from(buscarUsuario(id));
	}

	@Transactional(readOnly = true)
	public List<DireccionResponse> listarDirecciones(Long usuarioId) {
		buscarUsuario(usuarioId);
		return direccionRepository.findByUsuarioIdOrderById(usuarioId).stream().map(DireccionResponse::from).toList();
	}

	@Transactional
	public DireccionResponse agregarDireccion(Long usuarioId, DireccionRequest request) {
		Direccion direccion = new Direccion();
		direccion.setUsuario(buscarUsuario(usuarioId));
		direccion.setCalle(request.calle().trim());
		direccion.setNumero(request.numero().trim());
		direccion.setComuna(request.comuna().trim());
		direccion.setCiudad(request.ciudad().trim());
		direccion.setReferencia(request.referencia());
		return DireccionResponse.from(direccionRepository.save(direccion));
	}

	@Transactional
	public void eliminarDireccion(Long usuarioId, Long direccionId) {
		Direccion direccion = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Dirección " + direccionId + " no encontrada"));
		direccionRepository.delete(direccion);
	}

	private Usuario buscarUsuario(Long id) {
		return usuarioRepository.findById(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario " + id + " no encontrado"));
	}

}
