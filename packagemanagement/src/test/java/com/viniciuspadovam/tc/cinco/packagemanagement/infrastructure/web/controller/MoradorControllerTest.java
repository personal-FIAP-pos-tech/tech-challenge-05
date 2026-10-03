package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.AtualizarMoradorCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.AtualizarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.BuscarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.CadastrarMoradorCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.CadastrarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MoradorController.class)
@Import(SecurityConfig.class)
class MoradorControllerTest {

	private static final String CADASTRO = """
			{
				"nome": "Ana Souza",
				"email": "ana@email.com",
				"senha": "Senha@123",
				"telefone": "11988887777",
				"apartamento": "101"
			}
			""";

	private static final String ATUALIZACAO = """
			{
				"nome": "Ana Maria",
				"telefone": "1122223333",
				"apartamento": "202"
			}
			""";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CadastrarMoradorUseCase cadastrarMoradorUseCase;

	@MockitoBean
	private AtualizarMoradorUseCase atualizarMoradorUseCase;

	@MockitoBean
	private BuscarMoradorUseCase buscarMoradorUseCase;

	private final Morador ana = Morador.restaurar(1L, "Ana Souza", "ana@email.com", "hash", "11988887777", "101");

	static JwtRequestPostProcessor morador(long id) {
		return jwt().jwt(token -> token.subject(String.valueOf(id)))
				.authorities(new SimpleGrantedAuthority("ROLE_MORADOR"));
	}

	static JwtRequestPostProcessor porteiro(long id) {
		return jwt().jwt(token -> token.subject(String.valueOf(id)))
				.authorities(new SimpleGrantedAuthority("ROLE_PORTEIRO"));
	}

	@Test
	void deveCadastrarMoradorSemEstarLogado() throws Exception {
		when(cadastrarMoradorUseCase.executar(
				new CadastrarMoradorCommand("Ana Souza", "ana@email.com", "Senha@123", "11988887777", "101")))
				.thenReturn(ana);

		mockMvc.perform(post("/moradores").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nome").value("Ana Souza"))
				.andExpect(jsonPath("$.email").value("ana@email.com"))
				.andExpect(jsonPath("$.apartamento").value("101"))
				.andExpect(jsonPath("$.senha").doesNotExist())
				.andExpect(jsonPath("$.senhaHash").doesNotExist());
	}

	@Test
	void deveValidarCamposObrigatoriosNoCadastro() throws Exception {
		mockMvc.perform(post("/moradores").contentType(MediaType.APPLICATION_JSON).content("""
						{"nome": "", "email": "invalido", "senha": "123"}
						"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros.nome").exists())
				.andExpect(jsonPath("$.erros.email").exists())
				.andExpect(jsonPath("$.erros.senha").exists())
				.andExpect(jsonPath("$.erros.telefone").exists())
				.andExpect(jsonPath("$.erros.apartamento").exists());
		verifyNoInteractions(cadastrarMoradorUseCase);
	}

	@Test
	void deveRetornar409QuandoEmailJaExiste() throws Exception {
		when(cadastrarMoradorUseCase.executar(any()))
				.thenThrow(new ConflitoException("Já existe um usuário cadastrado com este e-mail."));

		mockMvc.perform(post("/moradores").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.detail").value("Já existe um usuário cadastrado com este e-mail."));
	}

	@Test
	void deveRetornar400QuandoDominioRejeitaDados() throws Exception {
		when(cadastrarMoradorUseCase.executar(any()))
				.thenThrow(new DadosInvalidosException("O telefone deve conter DDD e número, com 10 ou 11 dígitos."));

		mockMvc.perform(post("/moradores").contentType(MediaType.APPLICATION_JSON).content(CADASTRO))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detail").value("O telefone deve conter DDD e número, com 10 ou 11 dígitos."));
	}

	@Test
	void deveRetornarDadosDoMoradorLogado() throws Exception {
		when(buscarMoradorUseCase.executar(1L)).thenReturn(ana);

		mockMvc.perform(get("/moradores/me").with(morador(1L)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.telefone").value("11988887777"));
	}

	@Test
	void deveRetornar404QuandoMoradorDoTokenNaoExisteMais() throws Exception {
		when(buscarMoradorUseCase.executar(1L)).thenThrow(new RecursoNaoEncontradoException("Morador", 1L));

		mockMvc.perform(get("/moradores/me").with(morador(1L)))
				.andExpect(status().isNotFound());
	}

	@Test
	void deveExigirLoginParaConsultarDados() throws Exception {
		mockMvc.perform(get("/moradores/me"))
				.andExpect(status().isUnauthorized());
		verifyNoInteractions(buscarMoradorUseCase);
	}

	@Test
	void porteiroNaoPodeAcessarAreaDoMorador() throws Exception {
		mockMvc.perform(get("/moradores/me").with(porteiro(5L)))
				.andExpect(status().isForbidden());
		verifyNoInteractions(buscarMoradorUseCase);
	}

	@Test
	void deveAtualizarDadosDoMoradorLogado() throws Exception {
		Morador atualizado = Morador.restaurar(1L, "Ana Maria", "ana@email.com", "hash", "1122223333", "202");
		when(atualizarMoradorUseCase.executar(eq(1L), any())).thenReturn(atualizado);

		mockMvc.perform(put("/moradores/me").with(morador(1L))
						.contentType(MediaType.APPLICATION_JSON).content(ATUALIZACAO))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("Ana Maria"))
				.andExpect(jsonPath("$.apartamento").value("202"));
		verify(atualizarMoradorUseCase).executar(1L, new AtualizarMoradorCommand("Ana Maria", "1122223333", "202", null));
	}

	@Test
	void deveExigirLoginParaAtualizarDados() throws Exception {
		mockMvc.perform(put("/moradores/me").contentType(MediaType.APPLICATION_JSON).content(ATUALIZACAO))
				.andExpect(status().isUnauthorized());
		verifyNoInteractions(atualizarMoradorUseCase);
	}
}
