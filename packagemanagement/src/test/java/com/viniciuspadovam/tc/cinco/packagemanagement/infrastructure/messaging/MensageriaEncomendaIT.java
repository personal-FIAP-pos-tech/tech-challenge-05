package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class MensageriaEncomendaIT {

	@Container
	@ServiceConnection
	static final RabbitMQContainer RABBITMQ = new RabbitMQContainer("rabbitmq:4-management-alpine");

	@Autowired
	private RabbitEncomendaRecebidaPublisher publisher;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Test
	void encomendaPublicadaDeveChegarNaFilaDeEntrada() {
		publisher.publicar(42L);

		Message mensagem = rabbitTemplate.receive(RabbitMQConfig.FILA_ENCOMENDAS_ENTRADA, 5000);

		assertThat(mensagem).isNotNull();
		assertThat(new String(mensagem.getBody(), StandardCharsets.UTF_8)).contains("\"encomendaId\":42");
	}
}
