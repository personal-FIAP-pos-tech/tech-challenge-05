package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.EncomendaRecebidaMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitEncomendaRecebidaPublisher implements EncomendaRecebidaPublisher {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void publicar(Long encomendaId) {
		rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROTA_ENCOMENDA_RECEBIDA,
				new EncomendaRecebidaMessage(encomendaId));
	}
}
