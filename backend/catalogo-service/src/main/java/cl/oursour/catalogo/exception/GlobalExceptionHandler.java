package cl.oursour.catalogo.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex) {
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(ReglaNegocioException.class)
	public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException ex) {
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
		Map<String, String> detalles = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> detalles.putIfAbsent(error.getField(), error.getDefaultMessage()));
		ErrorResponse body = new ErrorResponse(LocalDateTime.now(), 400, "Bad Request", "Datos inválidos", detalles);
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> cuerpoInvalido(HttpMessageNotReadableException ex) {
		return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud no es válido");
	}

	private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), status.getReasonPhrase(), mensaje));
	}

}
