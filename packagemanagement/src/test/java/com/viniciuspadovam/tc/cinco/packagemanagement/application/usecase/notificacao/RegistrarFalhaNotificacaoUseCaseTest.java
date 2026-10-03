package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarFalhaNotificacaoUseCaseTest {

	@Mock
	private NotificacaoGateway notificacaoGateway;

	@InjectMocks
	private RegistrarFalhaNotificacaoUseCase useCase;

	private Notificacao notificacao(StatusNotificacao status) {
		return Notificacao.restaurar(70L, 30L, 1L, "ana@email.com", "Assunto", "Mensagem", status,
				LocalDateTime.of(2026, 10, 1, 9, 0), null, null);
	}

	@Test
	void deveMarcarNotificacaoPendenteComoFalha() {
		Notificacao pendente = notificacao(StatusNotificacao.PENDENTE);
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(pendente));

		useCase.executar(70L);

		assertThat(pendente.getStatus()).isEqualTo(StatusNotificacao.FALHA);
		verify(notificacaoGateway).salvar(pendente);
	}

	@Test
	void deveIgnorarNotificacaoQueNaoEstaMaisPendente() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.of(notificacao(StatusNotificacao.ENVIADA)));

		useCase.executar(70L);

		verify(notificacaoGateway, never()).salvar(any());
	}

	@Test
	void deveIgnorarNotificacaoInexistente() {
		when(notificacaoGateway.buscarPorId(70L)).thenReturn(Optional.empty());

		useCase.executar(70L);

		verify(notificacaoGateway, never()).salvar(any());
	}
}
