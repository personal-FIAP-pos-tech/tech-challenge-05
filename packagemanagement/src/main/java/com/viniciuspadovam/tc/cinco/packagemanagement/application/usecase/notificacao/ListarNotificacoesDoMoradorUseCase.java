package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarNotificacoesDoMoradorUseCase {

	private final NotificacaoGateway notificacaoGateway;

	public List<Notificacao> executar(Long moradorId) {
		return notificacaoGateway.listarPorMorador(moradorId);
	}
}
