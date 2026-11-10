package one.digitalinnovation.gof.exception;

/**
 * Exceção de domínio lançada quando um {@code Cliente} não existe. É traduzida
 * para HTTP 404 pelo {@link ApiExceptionHandler}.
 */
public class ClienteNaoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClienteNaoEncontradoException(Long id) {
		super("Cliente não encontrado: " + id);
	}
}
