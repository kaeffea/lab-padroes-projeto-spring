package one.digitalinnovation.gof.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import one.digitalinnovation.gof.exception.ApiExceptionHandler;
import one.digitalinnovation.gof.exception.ClienteInvalidoException;
import one.digitalinnovation.gof.exception.ClienteNaoEncontradoException;
import one.digitalinnovation.gof.model.Cliente;
import one.digitalinnovation.gof.service.ClienteService;

@ExtendWith(MockitoExtension.class)
class ClienteRestControllerTest {

	@Mock
	private ClienteService clienteService;
	@InjectMocks
	private ClienteRestController controller;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new ApiExceptionHandler()).build();
	}

	@Test
	void buscarPorIdInexistenteRetorna404() throws Exception {
		when(clienteService.buscarPorId(99L)).thenThrow(new ClienteNaoEncontradoException(99L));

		mockMvc.perform(get("/clientes/99")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.erro").value("Cliente não encontrado: 99"));
	}

	@Test
	void inserirInvalidoRetorna400() throws Exception {
		doThrow(new ClienteInvalidoException("O nome do cliente é obrigatório")).when(clienteService)
				.inserir(org.mockito.ArgumentMatchers.any(Cliente.class));

		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"\",\"endereco\":{\"cep\":\"57240000\"}}")).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erro").value("O nome do cliente é obrigatório"));
	}

	@Test
	void inserirValidoRetorna200() throws Exception {
		mockMvc.perform(post("/clientes").contentType(MediaType.APPLICATION_JSON)
				.content("{\"nome\":\"Maria\",\"endereco\":{\"cep\":\"57240000\"}}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Maria"));
	}
}
