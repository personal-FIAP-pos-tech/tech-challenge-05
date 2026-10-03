package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	public static final String EXCHANGE = "portaria.exchange";
	public static final String DLX = "portaria.dlx";

	public static final String FILA_ENCOMENDAS_ENTRADA = "encomendas.entrada";
	public static final String FILA_ENCOMENDAS_ENTRADA_DLQ = "encomendas.entrada.dlq";
	public static final String ROTA_ENCOMENDA_RECEBIDA = "encomenda.recebida";

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(EXCHANGE);
	}

	@Bean
	public DirectExchange deadLetterExchange() {
		return new DirectExchange(DLX);
	}

	@Bean
	public Queue filaEncomendasEntrada() {
		return QueueBuilder.durable(FILA_ENCOMENDAS_ENTRADA)
				.deadLetterExchange(DLX)
				.deadLetterRoutingKey(FILA_ENCOMENDAS_ENTRADA_DLQ)
				.build();
	}

	@Bean
	public Queue filaEncomendasEntradaDlq() {
		return QueueBuilder.durable(FILA_ENCOMENDAS_ENTRADA_DLQ).build();
	}

	@Bean
	public Binding bindingEncomendasEntrada(Queue filaEncomendasEntrada, DirectExchange exchange) {
		return BindingBuilder.bind(filaEncomendasEntrada).to(exchange).with(ROTA_ENCOMENDA_RECEBIDA);
	}

	@Bean
	public Binding bindingEncomendasEntradaDlq(Queue filaEncomendasEntradaDlq, DirectExchange deadLetterExchange) {
		return BindingBuilder.bind(filaEncomendasEntradaDlq).to(deadLetterExchange).with(FILA_ENCOMENDAS_ENTRADA_DLQ);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new JacksonJsonMessageConverter();
	}
}
