package com.viniciuspadovam.tc.cinco.packagemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.jayway.jsonpath.JsonPath;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@SpringBootTest(properties = {
	"spring.rabbitmq.listener.simple.auto-startup=true",
	"spring.mail.host=localhost",
	"spring.mail.port=3025"
})
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class FluxoCompletoEncomendaIT {

	@Container
	@ServiceConnection
	static final RabbitMQContainer RABBITMQ = new RabbitMQContainer("rabbitmq:4-management-alpine");

	@RegisterExtension
	static final GreenMailExtension SMTP = new GreenMailExtension(ServerSetupTest.SMTP);

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

	private String encomenda(String token, long id) throws Exception {
		return mockMvc.perform(get("/encomendas/" + id).header(HttpHeaders.AUTHORIZATION, token))
				.andReturn().getResponse().getContentAsString();
	}

	@Test
	void encomendaPercorreTodoOCicloDoRecebimentoARetirada() throws Exception {
		mockMvc.perform(post("/moradores").contentType(MediaType.APPLICATION_JSON).content("""
						{
							"nome": "Irene Barbosa",
							"email": "irene.barbosa@email.com",
							"senha": "Irene@2026",
							"telefone": "11966665555",
							"apartamento": "601"
						}
						"""))
				.andExpect(status().isCreated());
		String porteiro = login("roberto.silva@portaria.com", "Senha@123");
		String moradora = login("irene.barbosa@email.com", "Irene@2026");

		String registro = mockMvc.perform(post("/encomendas").header(HttpHeaders.AUTHORIZATION, porteiro)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nomeDestinatario": "IRENE barbosa", "apartamento": "601", "descricao": "Notebook"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.status").value("RECEBIDA"))
				.andReturn().getResponse().getContentAsString();
		long encomendaId = ((Number) JsonPath.read(registro, "$.id")).longValue();

		await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
			assertThat(SMTP.getReceivedMessages()).hasSize(1);
			assertThat((String) JsonPath.read(encomenda(porteiro, encomendaId), "$.status")).isEqualTo("NOTIFICADA");
		});
		MimeMessage email = SMTP.getReceivedMessages()[0];
		assertThat(email.getAllRecipients()[0].toString()).isEqualTo("irene.barbosa@email.com");
		assertThat(GreenMailUtil.getBody(email)).contains("Notebook");

		mockMvc.perform(patch("/encomendas/" + encomendaId + "/retirada").header(HttpHeaders.AUTHORIZATION, porteiro))
				.andExpect(status().isUnprocessableContent());

		String notificacoes = mockMvc.perform(get("/moradores/me/notificacoes")
						.header(HttpHeaders.AUTHORIZATION, moradora))
				.andExpect(jsonPath("$[0].encomendaId").value(encomendaId))
				.andExpect(jsonPath("$[0].status").value("ENVIADA"))
				.andReturn().getResponse().getContentAsString();
		long notificacaoId = ((Number) JsonPath.read(notificacoes, "$[0].id")).longValue();

		mockMvc.perform(patch("/notificacoes/" + notificacaoId + "/confirmacao")
						.header(HttpHeaders.AUTHORIZATION, moradora))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("CONFIRMADA"));

		mockMvc.perform(patch("/encomendas/" + encomendaId + "/retirada").header(HttpHeaders.AUTHORIZATION, porteiro))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("RETIRADA"))
				.andExpect(jsonPath("$.porteiroRecebimentoId").value(5))
				.andExpect(jsonPath("$.porteiroRetiradaId").value(5));
	}
}
