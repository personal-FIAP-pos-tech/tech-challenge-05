package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.messaging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EnvioEmailGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.rabbitmq.RabbitMQContainer;

@SpringBootTest(properties = {
	"spring.rabbitmq.listener.simple.auto-startup=true",
	"spring.rabbitmq.listener.simple.retry.initial-interval=100ms"
})
@Testcontainers(disabledWithoutDocker = true)
class FalhaEnvioNotificacaoIT {

	@Container
	@ServiceConnection
	static final RabbitMQContainer RABBITMQ = new RabbitMQContainer("rabbitmq:4-management-alpine");

	@MockitoBean
	private EnvioEmailGateway envioEmailGateway;

	@Autowired
	private RegistrarEncomendaUseCase registrarEncomendaUseCase;

	@Autowired
	private NotificacaoGateway notificacaoGateway;

	@Autowired
	private EncomendaGateway encomendaGateway;

	@Test
	void notificacaoQueNaoConsegueSerEnviadaVaiParaADlqEFicaComoFalha() {
		doThrow(new IllegalStateException("SMTP fora do ar"))
				.when(envioEmailGateway).enviar(anyString(), anyString(), anyString());

		Encomenda encomenda = registrarEncomendaUseCase.executar(2L,
				new RegistrarEncomendaCommand("Elisa Ferreira", "301", "Caixa de som"));

		await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
				assertThat(notificacaoGateway.buscarPorEncomendaId(encomenda.getId()))
						.get()
						.extracting(Notificacao::getStatus)
						.isEqualTo(StatusNotificacao.FALHA));
		verify(envioEmailGateway, atLeast(3)).enviar(anyString(), anyString(), anyString());
		assertThat(encomendaGateway.buscarPorId(encomenda.getId()))
				.get()
				.extracting(Encomenda::getStatus)
				.isEqualTo(StatusEncomenda.RECEBIDA);
	}
}
