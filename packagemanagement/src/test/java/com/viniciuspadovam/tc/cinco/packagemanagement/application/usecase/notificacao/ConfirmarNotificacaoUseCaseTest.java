package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.UnidadeDeTrabalho;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
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
class ConfirmarNotificacaoUseCaseTest {

	private static final Instant AGORA = Instant.parse("2026-10-01T18:00:00Z");
	private static final LocalDateTime AGORA_LOCAL = LocalDateTime.of(2026, 10, 1, 18, 0);

	@Mock
	private NotificacaoGateway notificacaoGateway;

	@Mock
	private EncomendaGateway encomendaGateway;

	@Mock
	private UnidadeDeTrabalho unidadeDeTrabalho;

	private ConfirmarNotificacaoUseCase useCase;
	private Notificacao notificacao;
	private Encomenda encomenda;

	@BeforeEach
	void configurar() {
		useCase = new ConfirmarNotificacaoUseCase(notificacaoGateway, encomendaGateway, unidadeDeTrabalho,
				Clock.fixed(AGORA, ZoneOffset.UTC));
		notificacao = Notificacao.restaurar(70L, 30L, 1L, "ana@email.com", "Assunto", "Mensagem",
				StatusNotificacao.ENVIADA, AGORA_LOCAL.minusHours(2), AGORA_LOCAL.minusHours(2), null);
		encomenda = Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa", StatusEncomenda.NOTIFICADA,
				AGORA_LOCAL.minusHours(3), 5L, AGORA_LOCAL.minusHours(2), null, null, null);
	}

	private void executarUnidadeDeTrabalho() {
		doAnswer(invocacao -> {
			((Runnable) invocacao.getArgument(0)).run();
			return null;
		}).when(unidadeDeTrabalho).executar(any(Runnable.class));
	}

	@Test
	void moradorDeveConfirmarSuaNotificacao() {
		executarUnidadeDeTrabalho();
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));

		Notificacao confirmada = useCase.executar(1L, 70L);

		assertThat(confirmada.getStatus()).isEqualTo(StatusNotificacao.CONFIRMADA);
		assertThat(confirmada.getDataConfirmacao()).isEqualTo(AGORA_LOCAL);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.CONFIRMADA);
		assertThat(encomenda.getDataConfirmacao()).isEqualTo(AGORA_LOCAL);
		verify(notificacaoGateway).salvar(notificacao);
		verify(encomendaGateway).salvar(encomenda);
	}

	@Test
	void moradorNaoPodeConfirmarNotificacaoDeOutroMorador() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));

		assertThatThrownBy(() -> useCase.executar(2L, 70L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
		verify(notificacaoGateway, never()).salvar(any());
	}

	@Test
	void deveFalharQuandoNotificacaoNaoExiste() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(1L, 70L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}

	@Test
	void naoDeveConfirmarDuasVezes() {
		executarUnidadeDeTrabalho();
		notificacao.confirmar(AGORA_LOCAL.minusHours(1));
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));

		assertThatThrownBy(() -> useCase.executar(1L, 70L))
				.isInstanceOf(RegraNegocioException.class);
		verify(notificacaoGateway, never()).salvar(any());
		verify(encomendaGateway, never()).salvar(any());
	}

	@Test
	void deveFalharQuandoEncomendaDaNotificacaoNaoExiste() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao));
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(1L, 70L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}
}
