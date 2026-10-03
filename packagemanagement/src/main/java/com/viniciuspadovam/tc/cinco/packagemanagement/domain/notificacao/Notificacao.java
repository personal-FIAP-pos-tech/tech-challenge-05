package com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Notificacao {

	private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final String ASSUNTO = "Chegou uma encomenda para você na portaria";

	private final Long id;
	private final Long encomendaId;
	private final Long moradorId;
	private final String destinatario;
	private final String assunto;
	private final String mensagem;
	private StatusNotificacao status;
	private final LocalDateTime dataCriacao;
	private LocalDateTime dataEnvio;
	private LocalDateTime dataConfirmacao;

	public static Notificacao paraEncomenda(Encomenda encomenda, Morador morador, LocalDateTime agora) {
		String mensagem = String.join("\n",
				"Olá, " + morador.getNome() + "!",
				"",
				"Chegou uma encomenda para você na portaria.",
				"Descrição: " + encomenda.getDescricao(),
				"Recebida em: " + FORMATO_DATA.format(encomenda.getDataRecebimento()),
				"",
				"Acesse o sistema com seu e-mail e senha para confirmar o recebimento desta notificação.",
				"Depois da confirmação, a encomenda pode ser retirada na portaria.");
		return new Notificacao(null, encomenda.getId(), morador.getId(), morador.getEmail(), ASSUNTO, mensagem,
				StatusNotificacao.PENDENTE, agora, null, null);
	}

	public static Notificacao restaurar(Long id, Long encomendaId, Long moradorId, String destinatario,
			String assunto, String mensagem, StatusNotificacao status, LocalDateTime dataCriacao,
			LocalDateTime dataEnvio, LocalDateTime dataConfirmacao) {
		return new Notificacao(id, encomendaId, moradorId, destinatario, assunto, mensagem, status, dataCriacao,
				dataEnvio, dataConfirmacao);
	}

	public boolean aguardandoEnvio() {
		return status == StatusNotificacao.PENDENTE;
	}

	public boolean pertenceAo(Long idMorador) {
		return Objects.equals(moradorId, idMorador);
	}

	public void marcarEnviada(LocalDateTime agora) {
		exigirPendente();
		this.status = StatusNotificacao.ENVIADA;
		this.dataEnvio = agora;
	}

	public void marcarFalha() {
		exigirPendente();
		this.status = StatusNotificacao.FALHA;
	}

	public void confirmar(LocalDateTime agora) {
		if (status == StatusNotificacao.CONFIRMADA) {
			throw new RegraNegocioException("Esta notificação já foi confirmada.");
		}
		if (status != StatusNotificacao.ENVIADA) {
			throw new RegraNegocioException(
					"Só é possível confirmar uma notificação que já foi enviada. Status atual: " + status + ".");
		}
		this.status = StatusNotificacao.CONFIRMADA;
		this.dataConfirmacao = agora;
	}

	private void exigirPendente() {
		if (status != StatusNotificacao.PENDENTE) {
			throw new RegraNegocioException("A notificação não está pendente de envio. Status atual: " + status + ".");
		}
	}
}
