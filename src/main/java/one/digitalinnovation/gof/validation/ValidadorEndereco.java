package one.digitalinnovation.gof.validation;

import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.model.Cliente;

/** Garante que o cliente informe um endereço com CEP. */
public class ValidadorEndereco extends ValidadorCliente {

	@Override
	protected void verificar(Cliente cliente) {
		if (cliente.getEndereco() == null || cliente.getEndereco().getCep() == null) {
			throw new ClienteInvalidoException("O CEP do endereço é obrigatório");
		}
	}
}
