package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.email;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EnvioEmailGateway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEnvioEmailGateway implements EnvioEmailGateway {

	private final JavaMailSender mailSender;
	private final String remetente;

	public SmtpEnvioEmailGateway(JavaMailSender mailSender,
			@Value("${app.notificacao.remetente}") String remetente) {
		this.mailSender = mailSender;
		this.remetente = remetente;
	}

	@Override
	public void enviar(String destinatario, String assunto, String mensagem) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setFrom(remetente);
		email.setTo(destinatario);
		email.setSubject(assunto);
		email.setText(mensagem);
		mailSender.send(email);
	}
}
