package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import static org.mockito.Mockito.verify;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.NotificacaoMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RabbitNotificacaoPublisherTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private RabbitNotificacaoPublisher publisher;

	@Test
	void devePublicarNotificacaoNoCanalDeSaida() {
		publisher.publicar(70L);

		verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROTA_NOTIFICACAO,
				new NotificacaoMessage(70L));
	}
}
