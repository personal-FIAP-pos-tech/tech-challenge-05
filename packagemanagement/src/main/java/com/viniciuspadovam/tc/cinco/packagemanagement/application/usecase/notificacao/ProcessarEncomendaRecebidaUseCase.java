package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProcessarEncomendaRecebidaUseCase {

	private final EncomendaGateway encomendaGateway;
	private final MoradorGateway moradorGateway;
	private final NotificacaoGateway notificacaoGateway;
	private final NotificacaoPublisher notificacaoPublisher;
	private final Clock clock;

	public Notificacao executar(Long encomendaId) {
		Encomenda encomenda = encomendaGateway.buscarPorId(encomendaId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Encomenda", encomendaId));

		Optional<Notificacao> existente = notificacaoGateway.buscarPorEncomendaId(encomendaId);
		if (existente.isPresent()) {
			Notificacao notificacao = existente.get();
			if (notificacao.aguardandoEnvio()) {
				notificacaoPublisher.publicar(notificacao.getId());
			}
			return notificacao;
		}

		Morador morador = moradorGateway.buscarPorId(encomenda.getMoradorId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Morador", encomenda.getMoradorId()));
		Notificacao notificacao = notificacaoGateway.salvar(
				Notificacao.paraEncomenda(encomenda, morador, LocalDateTime.now(clock)));
		notificacaoPublisher.publicar(notificacao.getId());
		return notificacao;
	}
}
