package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.UnidadeDeTrabalho;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfirmarNotificacaoUseCase {

	private final NotificacaoGateway notificacaoGateway;
	private final EncomendaGateway encomendaGateway;
	private final UnidadeDeTrabalho unidadeDeTrabalho;
	private final Clock clock;

	public Notificacao executar(Long moradorId, Long notificacaoId) {
		Notificacao notificacao = notificacaoGateway.buscarPorId(notificacaoId)
				.filter(encontrada -> encontrada.pertenceAo(moradorId))
				.orElseThrow(() -> new RecursoNaoEncontradoException("Notificação", notificacaoId));
		Encomenda encomenda = encomendaGateway.buscarPorId(notificacao.getEncomendaId())
				.orElseThrow(() -> new RecursoNaoEncontradoException("Encomenda", notificacao.getEncomendaId()));

		LocalDateTime agora = LocalDateTime.now(clock);
		unidadeDeTrabalho.executar(() -> {
			notificacao.confirmar(agora);
			encomenda.confirmarRecebimento(agora);
			notificacaoGateway.salvar(notificacao);
			encomendaGateway.salvar(encomenda);
		});
		return notificacao;
	}
}
