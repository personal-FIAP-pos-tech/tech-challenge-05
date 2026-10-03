package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarFalhaNotificacaoUseCase {

	private final NotificacaoGateway notificacaoGateway;

	public void executar(Long notificacaoId) {
		notificacaoGateway.buscarPorId(notificacaoId)
				.filter(notificacao -> notificacao.aguardandoEnvio())
				.ifPresent(notificacao -> {
					notificacao.marcarFalha();
					notificacaoGateway.salvar(notificacao);
				});
	}
}
