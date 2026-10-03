package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import java.util.Optional;

public interface NotificacaoGateway {

	Notificacao salvar(Notificacao notificacao);

	Optional<Notificacao> buscarPorId(Long id);

	Optional<Notificacao> buscarPorEncomendaId(Long encomendaId);
}
