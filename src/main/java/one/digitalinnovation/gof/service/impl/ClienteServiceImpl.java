package one.digitalinnovation.gof.service.impl;

import org.springframework.stereotype.Service;

import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.exception.ClienteNaoEncontradoException;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.model.ClienteRepository;
import one.digitalinnovation.gof.model.Endereco;
import one.digitalinnovation.gof.model.EnderecoRepository;
import one.digitalinnovation.gof.service.ClienteService;
import one.digitalinnovation.gof.service.ViaCepService;
import one.digitalinnovation.gof.validation.ValidadorCliente;

/**
 * Implementação da <b>Strategy</b> {@link ClienteService}, injetada pelo
 * Spring. Como essa classe é um {@link Service}, ela é tratada como um
 * <b>Singleton</b>.
 * 
 * Evoluções em relação ao projeto original: injeção via construtor, validação
 * com <b>Chain of Responsibility</b> e erros de domínio (404 e 400) no lugar
 * de falhas genéricas.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

	private final ClienteRepository clienteRepository;
	private final EnderecoRepository enderecoRepository;
	private final ViaCepService viaCepService;
	private final ValidadorCliente validadorCliente;

	// Singleton: o Spring injeta as dependências (únicas) pelo construtor.
	public ClienteServiceImpl(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository,
			ViaCepService viaCepService, ValidadorCliente validadorCliente) {
		this.clienteRepository = clienteRepository;
		this.enderecoRepository = enderecoRepository;
		this.viaCepService = viaCepService;
		this.validadorCliente = validadorCliente;
	}

	// Strategy: Implementar os métodos definidos na interface.
	// Facade: Abstrair integrações com subsistemas, provendo uma interface simples.

	@Override
	public Iterable<Cliente> buscarTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarPorId(Long id) {
		return clienteRepository.findById(id).orElseThrow(() -> new ClienteNaoEncontradoException(id));
	}

	@Override
	public void inserir(Cliente cliente) {
		validadorCliente.validar(cliente);
		salvarClienteComCep(cliente);
	}

	@Override
	public void atualizar(Long id, Cliente cliente) {
		if (!clienteRepository.existsById(id)) {
			throw new ClienteNaoEncontradoException(id);
		}
		validadorCliente.validar(cliente);
		// Mantém o id da URL, senão o save() criaria um novo cliente.
		cliente.setId(id);
		salvarClienteComCep(cliente);
	}

	@Override
	public void deletar(Long id) {
		if (!clienteRepository.existsById(id)) {
			throw new ClienteNaoEncontradoException(id);
		}
		clienteRepository.deleteById(id);
	}

	private void salvarClienteComCep(Cliente cliente) {
		// Verificar se o Endereco do Cliente já existe (pelo CEP). O ViaCEP devolve o
		// CEP com hífen (00000-000), e é assim que ele fica salvo; por isso a busca usa
		// o mesmo formato, senão nunca acharíamos o endereço já persistido.
		String cep = cliente.getEndereco().getCep();
		String cepFormatado = cep.substring(0, 5) + "-" + cep.substring(5);
		Endereco endereco = enderecoRepository.findById(cepFormatado).orElseGet(() -> {
			// Caso não exista, integrar com o ViaCEP e persistir o retorno.
			Endereco novoEndereco = viaCepService.consultarCep(cep);
			// Para um CEP inexistente o ViaCEP responde {"erro": true}, sem o campo cep.
			if (novoEndereco == null || novoEndereco.getCep() == null) {
				throw new ClienteInvalidoException("CEP não encontrado: " + cep);
			}
			enderecoRepository.save(novoEndereco);
			return novoEndereco;
		});
		cliente.setEndereco(endereco);
		// Inserir Cliente, vinculando o Endereco (novo ou existente).
		clienteRepository.save(cliente);
	}

}
