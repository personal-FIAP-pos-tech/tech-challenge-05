package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.listener;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.RegistrarFalhaNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.RabbitMQConfig;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.NotificacaoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoFalhaListener {

	private final RegistrarFalhaNotificacaoUseCase registrarFalhaNotificacaoUseCase;

	@RabbitListener(queues = RabbitMQConfig.FILA_NOTIFICACOES_SAIDA_DLQ)
	public void receber(NotificacaoMessage mensagem) {
		log.warn("Notificação {} esgotou as tentativas de envio", mensagem.notificacaoId());
		registrarFalhaNotificacaoUseCase.executar(mensagem.notificacaoId());
	}
}
