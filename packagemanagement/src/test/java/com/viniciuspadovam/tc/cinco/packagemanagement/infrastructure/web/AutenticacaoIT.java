package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AutenticacaoIT {

	private static final String SENHA_CARGA_INICIAL = "Senha@123";

	@Autowired
	private MockMvc mockMvc;

	private String login(String email, String senha) throws Exception {
		String resposta = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"%s\", \"senha\": \"%s\"}".formatted(email, senha)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return "Bearer " + JsonPath.read(resposta, "$.accessToken");
	}

	@Test
	void moradorDaCargaInicialFazLoginEConsultaSeusDados() throws Exception {
		String token = login("ana.souza@email.com", SENHA_CARGA_INICIAL);

		mockMvc.perform(get("/moradores/me").header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.nome").value("Ana Souza"));
	}

	@Test
	void porteiroDaCargaInicialFazLoginESoAcessaAreaDeFuncionario() throws Exception {
		String token = login("carlos.pereira@portaria.com", SENHA_CARGA_INICIAL);

		mockMvc.perform(get("/funcionarios/me").header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.perfil").value("PORTEIRO"));
		mockMvc.perform(get("/moradores/me").header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(status().isForbidden());
	}

	@Test
	void naoDeveAutenticarComSenhaErrada() throws Exception {
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"ana.souza@email.com\", \"senha\": \"errada123\"}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void deveRejeitarTokenInvalido() throws Exception {
		mockMvc.perform(get("/moradores/me").header(HttpHeaders.AUTHORIZATION, "Bearer token.invalido.aqui"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void novoMoradorSeCadastraFazLoginEAtualizaSeusDados() throws Exception {
		mockMvc.perform(post("/moradores").contentType(MediaType.APPLICATION_JSON).content("""
						{
							"nome": "Helena Martins",
							"email": "helena.martins@email.com",
							"senha": "MinhaSenha@1",
							"telefone": "(11) 97777-1234",
							"apartamento": "501"
						}
						"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.telefone").value("11977771234"));

		String token = login("helena.martins@email.com", "MinhaSenha@1");

		mockMvc.perform(put("/moradores/me").header(HttpHeaders.AUTHORIZATION, token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nome": "Helena Martins", "telefone": "11977771234", "apartamento": "502"}
								"""))
				.andExpect(status().isOk());
		mockMvc.perform(get("/moradores/me").header(HttpHeaders.AUTHORIZATION, token))
				.andExpect(jsonPath("$.apartamento").value("502"));
	}

	@Test
	void naoDevePermitirCadastroComEmailDeOutroUsuario() throws Exception {
		mockMvc.perform(post("/funcionarios").contentType(MediaType.APPLICATION_JSON).content("""
						{"nome": "Impostor", "email": "ana.souza@email.com", "senha": "Senha@123"}
						"""))
				.andExpect(status().isConflict());
	}
}
