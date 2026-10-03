package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.EncomendaEntity;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository.EncomendaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncomendaJpaGateway implements EncomendaGateway {

	private final EncomendaRepository repository;

	@Override
	public Encomenda salvar(Encomenda encomenda) {
		return paraDominio(repository.save(paraEntidade(encomenda)));
	}

	@Override
	public Optional<Encomenda> buscarPorId(Long id) {
		return repository.findById(id).map(EncomendaJpaGateway::paraDominio);
	}

	static EncomendaEntity paraEntidade(Encomenda encomenda) {
		return new EncomendaEntity(encomenda.getId(), encomenda.getMoradorId(), encomenda.getNomeDestinatario(),
				encomenda.getApartamento(), encomenda.getDescricao(), encomenda.getStatus(),
				encomenda.getDataRecebimento(), encomenda.getPorteiroRecebimentoId(), encomenda.getDataNotificacao(),
				encomenda.getDataConfirmacao(), encomenda.getDataRetirada(), encomenda.getPorteiroRetiradaId());
	}

	static Encomenda paraDominio(EncomendaEntity entity) {
		return Encomenda.restaurar(entity.getId(), entity.getMoradorId(), entity.getNomeDestinatario(),
				entity.getApartamento(), entity.getDescricao(), entity.getStatus(), entity.getDataRecebimento(),
				entity.getPorteiroRecebimentoId(), entity.getDataNotificacao(), entity.getDataConfirmacao(),
				entity.getDataRetirada(), entity.getPorteiroRetiradaId());
	}
}
