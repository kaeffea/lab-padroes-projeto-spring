package one.digitalinnovation.gof.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.Endereco;

class CadeiaDeValidacaoTest {

	private final ValidadorCliente cadeia = new ValidacaoConfig().cadeiaDeValidacaoCliente();

	private Cliente cliente(String nome, String cep) {
		Cliente cliente = new Cliente();
		cliente.setNome(nome);
		if (cep != null) {
			Endereco endereco = new Endereco();
			endereco.setCep(cep);
			cliente.setEndereco(endereco);
		}
		return cliente;
	}

	@Test
	void aceitaClienteValido() {
		assertDoesNotThrow(() -> cadeia.validar(cliente("Maria", "57240000")));
	}

	@Test
	void rejeitaNomeVazio() {
		ClienteInvalidoException e = assertThrows(ClienteInvalidoException.class,
				() -> cadeia.validar(cliente("  ", "57240000")));
		assertEquals("O nome do cliente é obrigatório", e.getMessage());
	}

	@Test
	void rejeitaClienteSemEndereco() {
		assertThrows(ClienteInvalidoException.class, () -> cadeia.validar(cliente("Maria", null)));
	}

	@Test
	void rejeitaCepComHifen() {
		assertThrows(ClienteInvalidoException.class, () -> cadeia.validar(cliente("Maria", "57240-000")));
	}

	@Test
	void rejeitaCepCurto() {
		assertThrows(ClienteInvalidoException.class, () -> cadeia.validar(cliente("Maria", "123")));
	}
}
