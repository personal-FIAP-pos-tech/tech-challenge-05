package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.listener;

import static org.mockito.Mockito.verify;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.EnviarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ProcessarEncomendaRecebidaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.RegistrarFalhaNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.EncomendaRecebidaMessage;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.NotificacaoMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListenersTest {

	@Mock
	private ProcessarEncomendaRecebidaUseCase processarEncomendaRecebidaUseCase;

	@Mock
	private EnviarNotificacaoUseCase enviarNotificacaoUseCase;

	@Mock
	private RegistrarFalhaNotificacaoUseCase registrarFalhaNotificacaoUseCase;

	@Test
	void encomendaRecebidaDeveSerProcessada() {
		new EncomendaRecebidaListener(processarEncomendaRecebidaUseCase).receber(new EncomendaRecebidaMessage(30L));

		verify(processarEncomendaRecebidaUseCase).executar(30L);
	}

	@Test
	void notificacaoDoCanalDeSaidaDeveSerEnviada() {
		new NotificacaoListener(enviarNotificacaoUseCase).receber(new NotificacaoMessage(70L));

		verify(enviarNotificacaoUseCase).executar(70L);
	}

	@Test
	void notificacaoQueEsgotouTentativasDeveSerMarcadaComoFalha() {
		new NotificacaoFalhaListener(registrarFalhaNotificacaoUseCase).receber(new NotificacaoMessage(70L));

		verify(registrarFalhaNotificacaoUseCase).executar(70L);
	}
}
