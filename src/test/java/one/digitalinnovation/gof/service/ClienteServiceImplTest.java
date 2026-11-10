package one.digitalinnovation.gof.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.exception.ClienteNaoEncontradoException;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.impl.ClienteServiceImpl;
import one.digitalinnovation.gof.validation.ValidacaoConfig;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

	@Mock
	private ClienteRepository clienteRepository;
	@Mock
	private EnderecoRepository enderecoRepository;
	@Mock
	private ViaCepService viaCepService;

	private ClienteServiceImpl service;

	@BeforeEach
	void setUp() {
		service = new ClienteServiceImpl(clienteRepository, enderecoRepository, viaCepService,
				new ValidacaoConfig().cadeiaDeValidacaoCliente());
	}

	private Cliente cliente(String nome, String cep) {
		Endereco endereco = new Endereco();
		endereco.setCep(cep);
		Cliente cliente = new Cliente();
		cliente.setNome(nome);
		cliente.setEndereco(endereco);
		return cliente;
	}

	private Endereco enderecoViaCep(String cepComHifen) {
		Endereco endereco = new Endereco();
		endereco.setCep(cepComHifen);
		endereco.setLocalidade("São Miguel dos Campos");
		return endereco;
	}

	@Test
	void buscarPorIdInexistenteLancaNaoEncontrado() {
		when(clienteRepository.findById(99L)).thenReturn(Optional.empty());
		assertThrows(ClienteNaoEncontradoException.class, () -> service.buscarPorId(99L));
	}

	@Test
	void inserirConsultaViaCepQuandoEnderecoNaoExiste() {
		Endereco doViaCep = enderecoViaCep("57240-000");
		when(enderecoRepository.findById("57240-000")).thenReturn(Optional.empty());
		when(viaCepService.consultarCep("57240000")).thenReturn(doViaCep);

		Cliente novo = cliente("Maria", "57240000");
		service.inserir(novo);

		verify(enderecoRepository).save(doViaCep);
		verify(clienteRepository).save(novo);
		assertSame(doViaCep, novo.getEndereco());
	}

	@Test
	void inserirReaproveitaEnderecoJaSalvoSemChamarViaCep() {
		Endereco salvo = enderecoViaCep("57240-000");
		when(enderecoRepository.findById("57240-000")).thenReturn(Optional.of(salvo));

		Cliente novo = cliente("Maria", "57240000");
		service.inserir(novo);

		verifyNoInteractions(viaCepService);
		verify(clienteRepository).save(novo);
		assertSame(salvo, novo.getEndereco());
	}

	@Test
	void inserirComCepInexistenteLancaClienteInvalido() {
		when(enderecoRepository.findById("00000-000")).thenReturn(Optional.empty());
		// O ViaCEP responde {"erro": true} e o Endereco chega sem cep.
		when(viaCepService.consultarCep("00000000")).thenReturn(new Endereco());

		assertThrows(ClienteInvalidoException.class, () -> service.inserir(cliente("Maria", "00000000")));
		verify(clienteRepository, never()).save(any());
	}

	@Test
	void inserirInvalidoNemChegaAoViaCep() {
		assertThrows(ClienteInvalidoException.class, () -> service.inserir(cliente("", "57240000")));
		verifyNoInteractions(viaCepService);
		verify(clienteRepository, never()).save(any());
	}

	@Test
	void atualizarMantemOIdDaUrl() {
		when(clienteRepository.existsById(7L)).thenReturn(true);
		when(enderecoRepository.findById("57240-000")).thenReturn(Optional.of(enderecoViaCep("57240-000")));

		service.atualizar(7L, cliente("Maria", "57240000"));

		ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
		verify(clienteRepository).save(captor.capture());
		assertEquals(Long.valueOf(7L), captor.getValue().getId());
	}

	@Test
	void atualizarInexistenteLancaNaoEncontrado() {
		when(clienteRepository.existsById(7L)).thenReturn(false);
		assertThrows(ClienteNaoEncontradoException.class, () -> service.atualizar(7L, cliente("Maria", "57240000")));
		verify(clienteRepository, never()).save(any());
	}

	@Test
	void deletarInexistenteLancaNaoEncontrado() {
		when(clienteRepository.existsById(7L)).thenReturn(false);
		assertThrows(ClienteNaoEncontradoException.class, () -> service.deletar(7L));
		verify(clienteRepository, never()).deleteById(any());
	}
}
