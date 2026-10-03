package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.EnviarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ProcessarEncomendaRecebidaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {"spring.mail.host=localhost", "spring.mail.port=3025"})
class NotificacaoEmailIT {

	@RegisterExtension
	static final GreenMailExtension SMTP = new GreenMailExtension(ServerSetupTest.SMTP);

	@Autowired
	private EncomendaGateway encomendaGateway;

	@Autowired
	private NotificacaoGateway notificacaoGateway;

	@Autowired
	private ProcessarEncomendaRecebidaUseCase processarEncomendaRecebidaUseCase;

	@Autowired
	private EnviarNotificacaoUseCase enviarNotificacaoUseCase;

	@MockitoBean
	private EncomendaRecebidaPublisher encomendaRecebidaPublisher;

	@MockitoBean
	private NotificacaoPublisher notificacaoPublisher;

	@Test
	void encomendaRecebidaGeraNotificacaoQueChegaPorEmailAoMorador() throws Exception {
		Encomenda encomenda = encomendaGateway.salvar(Encomenda.receber(4L, "Diego Rocha", "202",
				"Caixa de vinhos", 1L, LocalDateTime.of(2026, 10, 3, 8, 0)));

		Notificacao notificacao = processarEncomendaRecebidaUseCase.executar(encomenda.getId());
		verify(notificacaoPublisher).publicar(notificacao.getId());

		enviarNotificacaoUseCase.executar(notificacao.getId());

		MimeMessage[] emails = SMTP.getReceivedMessages();
		assertThat(emails).hasSize(1);
		assertThat(emails[0].getAllRecipients()[0].toString()).isEqualTo("diego.rocha@email.com");
		assertThat(emails[0].getSubject()).isEqualTo("Chegou uma encomenda para você na portaria");
		assertThat(GreenMailUtil.getBody(emails[0])).contains("Caixa de vinhos");

		assertThat(notificacaoGateway.buscarPorId(notificacao.getId()))
				.get().extracting(Notificacao::getStatus).isEqualTo(StatusNotificacao.ENVIADA);
		assertThat(encomendaGateway.buscarPorId(encomenda.getId()))
				.get().extracting(Encomenda::getStatus).isEqualTo(StatusEncomenda.NOTIFICADA);
	}
}
