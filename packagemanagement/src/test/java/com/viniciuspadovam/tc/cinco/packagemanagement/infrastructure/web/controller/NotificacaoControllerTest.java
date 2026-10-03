package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.morador;
import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.porteiro;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ConfirmarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security.SecurityConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(NotificacaoController.class)
@Import(SecurityConfig.class)
class NotificacaoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ConfirmarNotificacaoUseCase confirmarNotificacaoUseCase;

	@Test
	void moradorDeveConfirmarRecebimentoDaNotificacao() throws Exception {
		LocalDateTime agora = LocalDateTime.of(2026, 10, 1, 18, 0);
		when(confirmarNotificacaoUseCase.executar(1L, 70L)).thenReturn(Notificacao.restaurar(70L, 30L, 1L,
				"ana@email.com", "Assunto", "Mensagem", StatusNotificacao.CONFIRMADA, agora.minusHours(2),
				agora.minusHours(2), agora));

		mockMvc.perform(patch("/notificacoes/70/confirmacao").with(morador(1L)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(70))
				.andExpect(jsonPath("$.encomendaId").value(30))
				.andExpect(jsonPath("$.status").value("CONFIRMADA"))
				.andExpect(jsonPath("$.dataConfirmacao").value("2026-10-01T18:00:00"));
	}

	@Test
	void deveRetornar404ParaNotificacaoDeOutroMorador() throws Exception {
		when(confirmarNotificacaoUseCase.executar(2L, 70L))
				.thenThrow(new RecursoNaoEncontradoException("Notificação", 70L));

		mockMvc.perform(patch("/notificacoes/70/confirmacao").with(morador(2L)))
				.andExpect(status().isNotFound());
	}

	@Test
	void deveRetornar422QuandoJaConfirmada() throws Exception {
		when(confirmarNotificacaoUseCase.executar(1L, 70L))
				.thenThrow(new RegraNegocioException("Esta notificação já foi confirmada."));

		mockMvc.perform(patch("/notificacoes/70/confirmacao").with(morador(1L)))
				.andExpect(status().isUnprocessableContent());
	}

	@Test
	void porteiroNaoPodeConfirmarNotificacao() throws Exception {
		mockMvc.perform(patch("/notificacoes/70/confirmacao").with(porteiro(5L)))
				.andExpect(status().isForbidden());
		verifyNoInteractions(confirmarNotificacaoUseCase);
	}

	@Test
	void deveExigirLogin() throws Exception {
		mockMvc.perform(patch("/notificacoes/70/confirmacao"))
				.andExpect(status().isUnauthorized());
	}
}
