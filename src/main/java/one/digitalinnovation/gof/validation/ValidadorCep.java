package one.digitalinnovation.gof.validation;

import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.model.Cliente;

/**
 * Garante que o CEP tenha 8 dígitos, formato aceito pelo ViaCEP. Assim evitamos
 * uma chamada HTTP que já sabemos que falharia.
 */
public class ValidadorCep extends ValidadorCliente {

	@Override
	protected void verificar(Cliente cliente) {
		if (!cliente.getEndereco().getCep().matches("\\d{8}")) {
			throw new ClienteInvalidoException("O CEP deve conter 8 dígitos, sem hífen");
		}
	}
}
