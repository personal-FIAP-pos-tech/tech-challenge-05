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

	public static final String FILA_NOTIFICACOES_SAIDA = "notificacoes.saida";
	public static final String FILA_NOTIFICACOES_SAIDA_DLQ = "notificacoes.saida.dlq";
	public static final String ROTA_NOTIFICACAO = "notificacao.enviar";

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
		return filaComDlq(FILA_ENCOMENDAS_ENTRADA, FILA_ENCOMENDAS_ENTRADA_DLQ);
	}

	@Bean
	public Queue filaEncomendasEntradaDlq() {
		return QueueBuilder.durable(FILA_ENCOMENDAS_ENTRADA_DLQ).build();
	}

	@Bean
	public Queue filaNotificacoesSaida() {
		return filaComDlq(FILA_NOTIFICACOES_SAIDA, FILA_NOTIFICACOES_SAIDA_DLQ);
	}

	@Bean
	public Queue filaNotificacoesSaidaDlq() {
		return QueueBuilder.durable(FILA_NOTIFICACOES_SAIDA_DLQ).build();
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
	public Binding bindingNotificacoesSaida(Queue filaNotificacoesSaida, DirectExchange exchange) {
		return BindingBuilder.bind(filaNotificacoesSaida).to(exchange).with(ROTA_NOTIFICACAO);
	}

	@Bean
	public Binding bindingNotificacoesSaidaDlq(Queue filaNotificacoesSaidaDlq, DirectExchange deadLetterExchange) {
		return BindingBuilder.bind(filaNotificacoesSaidaDlq).to(deadLetterExchange).with(FILA_NOTIFICACOES_SAIDA_DLQ);
	}

	@Bean
	public MessageConverter messageConverter() {
		return new JacksonJsonMessageConverter();
	}

	private static Queue filaComDlq(String nome, String dlq) {
		return QueueBuilder.durable(nome)
				.deadLetterExchange(DLX)
				.deadLetterRoutingKey(dlq)
				.build();
	}
}
