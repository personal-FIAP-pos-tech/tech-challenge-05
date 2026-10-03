package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.NotificacaoEntity;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository.NotificacaoRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacaoJpaGateway implements NotificacaoGateway {

	private final NotificacaoRepository repository;

	@Override
	public Notificacao salvar(Notificacao notificacao) {
		return paraDominio(repository.save(paraEntidade(notificacao)));
	}

	@Override
	public Optional<Notificacao> buscarPorId(Long id) {
		return repository.findById(id).map(NotificacaoJpaGateway::paraDominio);
	}

	@Override
	public Optional<Notificacao> buscarPorEncomendaId(Long encomendaId) {
		return repository.findByEncomendaId(encomendaId).map(NotificacaoJpaGateway::paraDominio);
	}

	@Override
	public List<Notificacao> listarPorMorador(Long moradorId) {
		return repository.findByMoradorIdOrderByDataCriacaoDesc(moradorId).stream()
				.map(NotificacaoJpaGateway::paraDominio)
				.toList();
	}

	static NotificacaoEntity paraEntidade(Notificacao notificacao) {
		return new NotificacaoEntity(notificacao.getId(), notificacao.getEncomendaId(), notificacao.getMoradorId(),
				notificacao.getDestinatario(), notificacao.getAssunto(), notificacao.getMensagem(),
				notificacao.getStatus(), notificacao.getDataCriacao(), notificacao.getDataEnvio(),
				notificacao.getDataConfirmacao());
	}

	static Notificacao paraDominio(NotificacaoEntity entity) {
		return Notificacao.restaurar(entity.getId(), entity.getEncomendaId(), entity.getMoradorId(),
				entity.getDestinatario(), entity.getAssunto(), entity.getMensagem(), entity.getStatus(),
				entity.getDataCriacao(), entity.getDataEnvio(), entity.getDataConfirmacao());
	}
}
