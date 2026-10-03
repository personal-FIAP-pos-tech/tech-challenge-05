package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessarEncomendaRecebidaUseCaseTest {

	private static final Instant AGORA = Instant.parse("2026-10-01T09:01:00Z");

	@Mock
	private EncomendaGateway encomendaGateway;

	@Mock
	private MoradorGateway moradorGateway;

	@Mock
	private NotificacaoGateway notificacaoGateway;

	@Mock
	private NotificacaoPublisher notificacaoPublisher;

	private ProcessarEncomendaRecebidaUseCase useCase;

	private final Encomenda encomenda = Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa Amazon",
			StatusEncomenda.RECEBIDA, LocalDateTime.of(2026, 10, 1, 9, 0), 5L, null, null, null, null);
	private final Morador ana = Morador.restaurar(1L, "Ana Souza", "ana@email.com", "hash", "11988887777", "101");

	@BeforeEach
	void configurar() {
		useCase = new ProcessarEncomendaRecebidaUseCase(encomendaGateway, moradorGateway, notificacaoGateway,
				notificacaoPublisher, Clock.fixed(AGORA, ZoneOffset.UTC));
	}

	@Test
	void deveCriarNotificacaoPendenteEEnviarParaOCanalDeSaida() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));
		when(notificacaoGateway.buscarPorEncomendaId(30L)).thenReturn(Optional.empty());
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.of(ana));
		when(notificacaoGateway.salvar(any())).thenAnswer(invocacao -> comId(invocacao.getArgument(0), 70L));

		Notificacao notificacao = useCase.executar(30L);

		ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
		verify(notificacaoGateway).salvar(captor.capture());
		assertThat(captor.getValue().getStatus()).isEqualTo(StatusNotificacao.PENDENTE);
		assertThat(captor.getValue().getDestinatario()).isEqualTo("ana@email.com");
		assertThat(captor.getValue().getDataCriacao()).isEqualTo(LocalDateTime.of(2026, 10, 1, 9, 1));
		verify(notificacaoPublisher).publicar(70L);
		assertThat(notificacao.getId()).isEqualTo(70L);
	}

	@Test
	void naoDeveDuplicarNotificacaoQuandoMensagemEReentregue() {
		Notificacao existente = comId(Notificacao.paraEncomenda(encomenda, ana, LocalDateTime.now()), 70L);
		existente.marcarEnviada(LocalDateTime.now());
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));
		when(notificacaoGateway.buscarPorEncomendaId(30L)).thenReturn(Optional.of(existente));

		Notificacao notificacao = useCase.executar(30L);

		assertThat(notificacao).isSameAs(existente);
		verify(notificacaoGateway, never()).salvar(any());
		verify(notificacaoPublisher, never()).publicar(anyLong());
	}

	@Test
	void deveReenviarParaOCanalDeSaidaQuandoNotificacaoExistenteAindaEstaPendente() {
		Notificacao pendente = comId(Notificacao.paraEncomenda(encomenda, ana, LocalDateTime.now()), 70L);
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));
		when(notificacaoGateway.buscarPorEncomendaId(30L)).thenReturn(Optional.of(pendente));

		useCase.executar(30L);

		verify(notificacaoGateway, never()).salvar(any());
		verify(notificacaoPublisher).publicar(70L);
	}

	@Test
	void deveFalharQuandoEncomendaNaoExiste() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(30L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
		verify(notificacaoPublisher, never()).publicar(anyLong());
	}

	@Test
	void deveFalharQuandoMoradorNaoExiste() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));
		when(notificacaoGateway.buscarPorEncomendaId(30L)).thenReturn(Optional.empty());
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(30L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}

	static Notificacao comId(Notificacao n, Long id) {
		return Notificacao.restaurar(id, n.getEncomendaId(), n.getMoradorId(), n.getDestinatario(), n.getAssunto(),
				n.getMensagem(), n.getStatus(), n.getDataCriacao(), n.getDataEnvio(), n.getDataConfirmacao());
	}
}
