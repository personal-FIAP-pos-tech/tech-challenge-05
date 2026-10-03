package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.morador;
import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.porteiro;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security.SecurityConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EncomendaController.class)
@Import(SecurityConfig.class)
class EncomendaControllerTest {

	private static final String CORPO = """
			{"nomeDestinatario": "Ana Souza", "apartamento": "101", "descricao": "Caixa Amazon"}
			""";

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegistrarEncomendaUseCase registrarEncomendaUseCase;

	@Test
	void porteiroDeveRegistrarEncomenda() throws Exception {
		Encomenda encomenda = Encomenda.receber(1L, "Ana Souza", "101", "Caixa Amazon", 5L,
				LocalDateTime.of(2026, 10, 1, 9, 0));
		Encomenda salva = Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa Amazon", encomenda.getStatus(),
				encomenda.getDataRecebimento(), 5L, null, null, null, null);
		when(registrarEncomendaUseCase.executar(eq(5L),
				eq(new RegistrarEncomendaCommand("Ana Souza", "101", "Caixa Amazon")))).thenReturn(salva);

		mockMvc.perform(post("/encomendas").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(30))
				.andExpect(jsonPath("$.moradorId").value(1))
				.andExpect(jsonPath("$.status").value("RECEBIDA"))
				.andExpect(jsonPath("$.dataRecebimento").value("2026-10-01T09:00:00"))
				.andExpect(jsonPath("$.porteiroRecebimentoId").value(5));
	}

	@Test
	void deveRetornar422QuandoMoradorNaoEEncontrado() throws Exception {
		when(registrarEncomendaUseCase.executar(eq(5L), any()))
				.thenThrow(new RegraNegocioException("Nenhum morador encontrado."));

		mockMvc.perform(post("/encomendas").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.detail").value("Nenhum morador encontrado."));
	}

	@Test
	void deveValidarCamposObrigatorios() throws Exception {
		mockMvc.perform(post("/encomendas").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros.nomeDestinatario").exists())
				.andExpect(jsonPath("$.erros.apartamento").exists())
				.andExpect(jsonPath("$.erros.descricao").exists());
		verifyNoInteractions(registrarEncomendaUseCase);
	}

	@Test
	void moradorNaoPodeRegistrarEncomenda() throws Exception {
		mockMvc.perform(post("/encomendas").with(morador(1L))
						.contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isForbidden());
		verifyNoInteractions(registrarEncomendaUseCase);
	}

	@Test
	void deveExigirLogin() throws Exception {
		mockMvc.perform(post("/encomendas").contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isUnauthorized());
	}
}
