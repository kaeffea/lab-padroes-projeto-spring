package one.digitalinnovation.gof.validation;

import one.digitalinnovation.gof.model.Cliente;

/**
 * Elo da <b>Chain of Responsibility</b> de validação de {@link Cliente}. Cada
 * elo faz a sua checagem e, se ela passar, repassa o cliente ao próximo elo.
 * Para reprovar, o elo lança {@code ClienteInvalidoException}.
 */
public abstract class ValidadorCliente {

	private ValidadorCliente proximo;

	/**
	 * Define o próximo elo e o retorna, permitindo montar a cadeia de forma
	 * fluente: {@code a.proximo(b).proximo(c)}.
	 */
	public ValidadorCliente proximo(ValidadorCliente proximo) {
		this.proximo = proximo;
		return proximo;
	}

	public final void validar(Cliente cliente) {
		verificar(cliente);
		if (proximo != null) {
			proximo.validar(cliente);
		}
	}

	protected abstract void verificar(Cliente cliente);
}
