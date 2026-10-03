package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import static org.mockito.Mockito.verify;

import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.EncomendaRecebidaMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
class RabbitEncomendaRecebidaPublisherTest {

	@Mock
	private RabbitTemplate rabbitTemplate;

	@InjectMocks
	private RabbitEncomendaRecebidaPublisher publisher;

	@Test
	void devePublicarEncomendaNoCanalDeEntrada() {
		publisher.publicar(10L);

		verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.ROTA_ENCOMENDA_RECEBIDA,
				new EncomendaRecebidaMessage(10L));
	}
}
