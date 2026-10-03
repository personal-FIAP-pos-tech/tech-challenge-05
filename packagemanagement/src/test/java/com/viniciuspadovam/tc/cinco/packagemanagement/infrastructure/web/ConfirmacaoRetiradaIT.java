package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class ConfirmacaoRetiradaIT {

	@Autowired
	private MockMvc mockMvc;

	private String login(String email) throws Exception {
		String resposta = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"%s\", \"senha\": \"Senha@123\"}".formatted(email)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return "Bearer " + JsonPath.read(resposta, "$.accessToken");
	}

	@Test
	void retiradaSoEPermitidaDepoisDaConfirmacaoDoMorador() throws Exception {
		String porteiro = login("marcos.dias@portaria.com");
		String bruno = login("bruno.lima@email.com");

		mockMvc.perform(patch("/encomendas/2/retirada").header(HttpHeaders.AUTHORIZATION, porteiro))
				.andExpect(status().isUnprocessableContent());

		mockMvc.perform(get("/moradores/me/notificacoes").header(HttpHeaders.AUTHORIZATION, bruno))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(2))
				.andExpect(jsonPath("$[0].status").value("ENVIADA"));

		mockMvc.perform(patch("/notificacoes/2/confirmacao").header(HttpHeaders.AUTHORIZATION, bruno))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMADA"));

		mockMvc.perform(get("/moradores/me/encomendas").header(HttpHeaders.AUTHORIZATION, bruno))
				.andExpect(jsonPath("$[0].id").value(2))
				.andExpect(jsonPath("$[0].status").value("CONFIRMADA"));

		mockMvc.perform(patch("/encomendas/2/retirada").header(HttpHeaders.AUTHORIZATION, porteiro))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("RETIRADA"))
				.andExpect(jsonPath("$.porteiroRetiradaId").value(3))
				.andExpect(jsonPath("$.dataRetirada").exists());

		mockMvc.perform(patch("/encomendas/2/retirada").header(HttpHeaders.AUTHORIZATION, porteiro))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.detail").value("Esta encomenda já foi retirada."));
	}

	@Test
	void moradorNaoConsegueConfirmarNotificacaoDeOutroMorador() throws Exception {
		String ana = login("ana.souza@email.com");

		mockMvc.perform(patch("/notificacoes/3/confirmacao").header(HttpHeaders.AUTHORIZATION, ana))
				.andExpect(status().isNotFound());
	}

	@Test
	void porteiroConsultaEncomendasPorStatus() throws Exception {
		String porteiro = login("patricia.nunes@portaria.com");

		mockMvc.perform(get("/encomendas").header(HttpHeaders.AUTHORIZATION, porteiro)
						.param("status", "RETIRADA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.conteudo[*].id", hasItem(5)))
				.andExpect(jsonPath("$.conteudo[*].status", everyItem(is("RETIRADA"))));

		mockMvc.perform(get("/encomendas/6").header(HttpHeaders.AUTHORIZATION, porteiro))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nomeDestinatario").value("Fábio Gonçalves"));
	}
}
