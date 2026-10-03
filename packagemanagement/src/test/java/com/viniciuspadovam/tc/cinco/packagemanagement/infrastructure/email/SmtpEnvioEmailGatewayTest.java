package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class SmtpEnvioEmailGatewayTest {

	@Mock
	private JavaMailSender mailSender;

	@Test
	void deveEnviarEmailComRemetenteDaPortaria() {
		SmtpEnvioEmailGateway gateway = new SmtpEnvioEmailGateway(mailSender, "portaria@condominio.com");

		gateway.enviar("ana@email.com", "Chegou uma encomenda", "Olá, Ana!");

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());
		SimpleMailMessage email = captor.getValue();
		assertThat(email.getFrom()).isEqualTo("portaria@condominio.com");
		assertThat(email.getTo()).containsExactly("ana@email.com");
		assertThat(email.getSubject()).isEqualTo("Chegou uma encomenda");
		assertThat(email.getText()).isEqualTo("Olá, Ana!");
	}
}
