package one.digitalinnovation.gof.validation;

import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.model.Cliente;

/** Garante que o cliente tenha nome. */
public class ValidadorNome extends ValidadorCliente {

	@Override
	protected void verificar(Cliente cliente) {
		String nome = cliente.getNome();
		if (nome == null || nome.trim().isEmpty()) {
			throw new ClienteInvalidoException("O nome do cliente é obrigatório");
		}
	}
}
