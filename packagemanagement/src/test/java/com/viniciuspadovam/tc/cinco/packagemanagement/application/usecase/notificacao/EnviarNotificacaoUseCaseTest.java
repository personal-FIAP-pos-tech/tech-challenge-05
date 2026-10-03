package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EnvioEmailGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.UnidadeDeTrabalho;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacaoUseCaseTest {

	private static final Instant AGORA = Instant.parse("2026-10-01T09:02:00Z");
	private static final LocalDateTime AGORA_LOCAL = LocalDateTime.of(2026, 10, 1, 9, 2);

	@Mock
	private NotificacaoGateway notificacaoGateway;

	@Mock
	private EncomendaGateway encomendaGateway;

	@Mock
	private EnvioEmailGateway envioEmailGateway;

	@Mock
	private UnidadeDeTrabalho unidadeDeTrabalho;

	private EnviarNotificacaoUseCase useCase;

	private Notificacao notificacao;
	private Encomenda encomenda;

	@BeforeEach
	void configurar() {
		useCase = new EnviarNotificacaoUseCase(notificacaoGateway, encomendaGateway, envioEmailGateway,
				unidadeDeTrabalho, Clock.fixed(AGORA, ZoneOffset.UTC));
		notificacao = Notificacao.restaurar(70L, 30L, 1L, "ana@email.com", "Assunto", "Mensagem",
				StatusNotificacao.PENDENTE, AGORA_LOCAL.minusMinutes(1), null, null);
		encomenda = Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa", StatusEncomenda.RECEBIDA,
				AGORA_LOCAL.minusMinutes(2), 5L, null, null, null, null);
		lenient().doAnswer(invocacao -> {
			((Runnable) invocacao.getArgument(0)).run();
			return null;
		}).when(unidadeDeTrabalho).executar(any(Runnable.class));
	}

	@Test
	void deveEnviarEmailEMarcarNotificacaoEEncomenda() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));

		useCase.executar(70L);

		verify(envioEmailGateway).enviar("ana@email.com", "Assunto", "Mensagem");
		verify(notificacaoGateway).salvar(notificacao);
		verify(encomendaGateway).salvar(encomenda);
		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.ENVIADA);
		assertThat(notificacao.getDataEnvio()).isEqualTo(AGORA_LOCAL);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.NOTIFICADA);
		assertThat(encomenda.getDataNotificacao()).isEqualTo(AGORA_LOCAL);
	}

	@Test
	void naoDeveReenviarNotificacaoJaEnviada() {
		notificacao.marcarEnviada(AGORA_LOCAL);
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));

		useCase.executar(70L);

		verifyNoInteractions(envioEmailGateway, unidadeDeTrabalho, encomendaGateway);
	}

	@Test
	void devePropagarFalhaDoEnvioSemAlterarStatusParaPermitirNovaTentativa() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));
		doThrow(new IllegalStateException("SMTP indisponível"))
				.when(envioEmailGateway).enviar(anyString(), anyString(), anyString());

		assertThatThrownBy(() -> useCase.executar(70L)).isInstanceOf(IllegalStateException.class);

		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.PENDENTE);
		verify(notificacaoGateway, never()).salvar(any());
	}

	@Test
	void deveFalharQuandoNotificacaoNaoExiste() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(70L)).isInstanceOf(RecursoNaoEncontradoException.class);
	}

	@Test
	void deveFalharQuandoEncomendaDaNotificacaoNaoExiste() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(70L)).isInstanceOf(RecursoNaoEncontradoException.class);
	}
}
