package one.digitalinnovation.gof.exception;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centraliza a tradução de exceções de domínio em respostas HTTP, assim a
 * <b>Facade</b> ({@code ClienteRestController}) continua simples.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ClienteNaoEncontradoException.class)
	public ResponseEntity<Map<String, String>> naoEncontrado(ClienteNaoEncontradoException e) {
		return resposta(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(ClienteInvalidoException.class)
	public ResponseEntity<Map<String, String>> invalido(ClienteInvalidoException e) {
		return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
		return ResponseEntity.status(status).body(Collections.singletonMap("erro", mensagem));
	}
}
