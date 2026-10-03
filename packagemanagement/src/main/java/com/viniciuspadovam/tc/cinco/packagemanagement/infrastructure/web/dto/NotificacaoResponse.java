package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import java.time.LocalDateTime;

public record NotificacaoResponse(
		Long id,
		Long encomendaId,
		String destinatario,
		String assunto,
		String mensagem,
		StatusNotificacao status,
		LocalDateTime dataCriacao,
		LocalDateTime dataEnvio,
		LocalDateTime dataConfirmacao) {

	public static NotificacaoResponse de(Notificacao notificacao) {
		return new NotificacaoResponse(notificacao.getId(), notificacao.getEncomendaId(),
				notificacao.getDestinatario(), notificacao.getAssunto(), notificacao.getMensagem(),
				notificacao.getStatus(), notificacao.getDataCriacao(), notificacao.getDataEnvio(),
				notificacao.getDataConfirmacao());
	}
}
