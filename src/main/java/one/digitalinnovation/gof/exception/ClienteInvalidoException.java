package one.digitalinnovation.gof.exception;

/**
 * Exceção de domínio lançada quando os dados de um {@code Cliente} não passam
 * pelas validações. É traduzida para HTTP 400 pelo {@link ApiExceptionHandler}.
 */
public class ClienteInvalidoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ClienteInvalidoException(String mensagem) {
		super(mensagem);
	}
}
