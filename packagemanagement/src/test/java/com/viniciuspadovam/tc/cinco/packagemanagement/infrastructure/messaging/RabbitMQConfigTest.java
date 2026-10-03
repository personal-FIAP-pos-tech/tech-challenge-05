package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;

class RabbitMQConfigTest {

	private final RabbitMQConfig config = new RabbitMQConfig();

	@Test
	void filaDeEntradaDeveSerDuravelEEnviarFalhasParaADlq() {
		Queue fila = config.filaEncomendasEntrada();

		assertThat(fila.getName()).isEqualTo(RabbitMQConfig.FILA_ENCOMENDAS_ENTRADA);
		assertThat(fila.isDurable()).isTrue();
		assertThat(fila.getArguments())
				.containsEntry("x-dead-letter-exchange", RabbitMQConfig.DLX)
				.containsEntry("x-dead-letter-routing-key", RabbitMQConfig.FILA_ENCOMENDAS_ENTRADA_DLQ);
	}

	@Test
	void deveLigarFilasAosExchanges() {
		Binding entrada = config.bindingEncomendasEntrada(config.filaEncomendasEntrada(), config.exchange());
		Binding dlq = config.bindingEncomendasEntradaDlq(config.filaEncomendasEntradaDlq(), config.deadLetterExchange());

		assertThat(entrada.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE);
		assertThat(entrada.getRoutingKey()).isEqualTo(RabbitMQConfig.ROTA_ENCOMENDA_RECEBIDA);
		assertThat(dlq.getExchange()).isEqualTo(RabbitMQConfig.DLX);
		assertThat(dlq.getDestination()).isEqualTo(RabbitMQConfig.FILA_ENCOMENDAS_ENTRADA_DLQ);
	}
}
