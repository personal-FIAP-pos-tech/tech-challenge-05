package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.listener;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.EnviarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.RabbitMQConfig;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.NotificacaoMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoListener {

	private final EnviarNotificacaoUseCase enviarNotificacaoUseCase;

	@RabbitListener(queues = RabbitMQConfig.FILA_NOTIFICACOES_SAIDA)
	public void receber(NotificacaoMessage mensagem) {
		log.info("Enviando notificação {}", mensagem.notificacaoId());
		enviarNotificacaoUseCase.executar(mensagem.notificacaoId());
	}
}
