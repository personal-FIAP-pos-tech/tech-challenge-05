package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.listener;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ProcessarEncomendaRecebidaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.RabbitMQConfig;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging.message.EncomendaRecebidaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncomendaRecebidaListener {

	private final ProcessarEncomendaRecebidaUseCase processarEncomendaRecebidaUseCase;

	@RabbitListener(queues = RabbitMQConfig.FILA_ENCOMENDAS_ENTRADA)
	public void receber(EncomendaRecebidaMessage mensagem) {
		log.info("Processando encomenda recebida {}", mensagem.encomendaId());
		processarEncomendaRecebidaUseCase.executar(mensagem.encomendaId());
	}
}
