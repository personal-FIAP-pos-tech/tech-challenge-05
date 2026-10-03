package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RecebimentoEncomendaIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EncomendaGateway encomendaGateway;

	@MockitoBean
	private EncomendaRecebidaPublisher publisher;

	private String login(String email) throws Exception {
		String resposta = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\": \"%s\", \"senha\": \"Senha@123\"}".formatted(email)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return "Bearer " + JsonPath.read(resposta, "$.accessToken");
	}

	@Test
	void porteiroRegistraEncomendaQueEPersistidaEEnviadaParaAFila() throws Exception {
		String token = login("joana.alves@portaria.com");

		String resposta = mockMvc.perform(post("/encomendas").header(HttpHeaders.AUTHORIZATION, token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nomeDestinatario": "carla MENDES", "apartamento": "201", "descricao": "Caixa de sapatos"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.moradorId").value(3))
				.andExpect(jsonPath("$.porteiroRecebimentoId").value(2))
				.andReturn().getResponse().getContentAsString();

		Long id = ((Number) JsonPath.read(resposta, "$.id")).longValue();
		assertThat(encomendaGateway.buscarPorId(id))
				.get()
				.extracting("status", "descricao")
				.containsExactly(StatusEncomenda.RECEBIDA, "Caixa de sapatos");
		verify(publisher).publicar(id);
	}

	@Test
	void naoRegistraEncomendaParaNomeQueNaoMoraNoApartamento() throws Exception {
		String token = login("joana.alves@portaria.com");

		mockMvc.perform(post("/encomendas").header(HttpHeaders.AUTHORIZATION, token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nomeDestinatario": "Carla Mendes", "apartamento": "101", "descricao": "Caixa"}
								"""))
				.andExpect(status().isUnprocessableContent());
		verify(publisher, never()).publicar(anyLong());
	}

	@Test
	void moradorNaoPodeRegistrarEncomenda() throws Exception {
		String token = login("ana.souza@email.com");

		mockMvc.perform(post("/encomendas").header(HttpHeaders.AUTHORIZATION, token)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nomeDestinatario": "Ana Souza", "apartamento": "101", "descricao": "Caixa"}
								"""))
				.andExpect(status().isForbidden());
	}
}
