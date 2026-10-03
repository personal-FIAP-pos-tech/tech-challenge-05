package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.CredenciaisInvalidasException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.AutenticarUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.TokenGerado;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AutenticarUseCase autenticarUseCase;

	@Test
	void deveRetornarTokenQuandoCredenciaisSaoValidas() throws Exception {
		when(autenticarUseCase.executar("ana@email.com", "Senha@123")).thenReturn(new TokenGerado("jwt-token", 3600));

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email": "ana@email.com", "senha": "Senha@123"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.accessToken").value("jwt-token"))
				.andExpect(jsonPath("$.tokenType").value("Bearer"))
				.andExpect(jsonPath("$.expiresIn").value(3600));
	}

	@Test
	void deveRetornar401QuandoCredenciaisSaoInvalidas() throws Exception {
		when(autenticarUseCase.executar("ana@email.com", "errada")).thenThrow(new CredenciaisInvalidasException());

		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email": "ana@email.com", "senha": "errada"}
								"""))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.detail").value("E-mail ou senha inválidos."));
	}

	@Test
	void deveRetornar400QuandoFaltamCampos() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros.email").exists())
				.andExpect(jsonPath("$.erros.senha").exists());
	}

	@Test
	void deveRetornar400QuandoCorpoEInvalido() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{ invalido"))
				.andExpect(status().isBadRequest());
	}
}
