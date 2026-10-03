package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.morador;
import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.porteiro;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.AtualizarFuncionarioCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.AtualizarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.BuscarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.CadastrarFuncionarioCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.CadastrarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FuncionarioController.class)
@Import(SecurityConfig.class)
class FuncionarioControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CadastrarFuncionarioUseCase cadastrarFuncionarioUseCase;

	@MockitoBean
	private AtualizarFuncionarioUseCase atualizarFuncionarioUseCase;

	@MockitoBean
	private BuscarFuncionarioUseCase buscarFuncionarioUseCase;

	private final Funcionario carlos = Funcionario.restaurar(5L, "Carlos Lima", "carlos@portaria.com", "hash");

	@Test
	void deveCadastrarFuncionarioSemEstarLogado() throws Exception {
		when(cadastrarFuncionarioUseCase.executar(
				new CadastrarFuncionarioCommand("Carlos Lima", "carlos@portaria.com", "Senha@123")))
				.thenReturn(carlos);

		mockMvc.perform(post("/funcionarios").contentType(MediaType.APPLICATION_JSON).content("""
						{"nome": "Carlos Lima", "email": "carlos@portaria.com", "senha": "Senha@123"}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(5))
				.andExpect(jsonPath("$.perfil").value("PORTEIRO"))
				.andExpect(jsonPath("$.senhaHash").doesNotExist());
	}

	@Test
	void deveValidarCamposObrigatoriosNoCadastro() throws Exception {
		mockMvc.perform(post("/funcionarios").contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros.nome").exists())
				.andExpect(jsonPath("$.erros.email").exists())
				.andExpect(jsonPath("$.erros.senha").exists());
		verifyNoInteractions(cadastrarFuncionarioUseCase);
	}

	@Test
	void deveRetornarDadosDoFuncionarioLogado() throws Exception {
		when(buscarFuncionarioUseCase.executar(5L)).thenReturn(carlos);

		mockMvc.perform(get("/funcionarios/me").with(porteiro(5L)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value("carlos@portaria.com"));
	}

	@Test
	void moradorNaoPodeAcessarAreaDoFuncionario() throws Exception {
		mockMvc.perform(get("/funcionarios/me").with(morador(1L)))
				.andExpect(status().isForbidden());
	}

	@Test
	void deveExigirLoginParaConsultarDados() throws Exception {
		mockMvc.perform(get("/funcionarios/me"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void deveAtualizarDadosDoFuncionarioLogado() throws Exception {
		Funcionario atualizado = Funcionario.restaurar(5L, "Carlos Eduardo", "carlos@portaria.com", "hash");
		when(atualizarFuncionarioUseCase.executar(eq(5L),
				eq(new AtualizarFuncionarioCommand("Carlos Eduardo", "NovaSenha@1")))).thenReturn(atualizado);

		mockMvc.perform(put("/funcionarios/me").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nome": "Carlos Eduardo", "novaSenha": "NovaSenha@1"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Carlos Eduardo"));
	}
}
