package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.NotificacaoMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitNotificacaoPublisher implements NotificacaoPublisher {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void publicar(Long notificacaoId) {
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROTA_NOTIFICACAO,
				new NotificacaoMessage(notificacaoId));
	}
}
