package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListarNotificacoesDoMoradorUseCaseTest {

	@Mock
	private NotificacaoGateway notificacaoGateway;

	@InjectMocks
	private ListarNotificacoesDoMoradorUseCase useCase;

	@Test
	void deveListarNotificacoesDoMorador() {
		Notificacao notificacao = Notificacao.restaurar(70L, 30L, 1L, "ana@email.com", "Assunto", "Mensagem",
				StatusNotificacao.ENVIADA, LocalDateTime.of(2026, 10, 1, 9, 0), null, null);
		when(notificacaoGateway.listarPorMorador(1L)).thenReturn(List.of(notificacao));

		assertThat(useCase.executar(1L)).containsExactly(notificacao);
	}
}
