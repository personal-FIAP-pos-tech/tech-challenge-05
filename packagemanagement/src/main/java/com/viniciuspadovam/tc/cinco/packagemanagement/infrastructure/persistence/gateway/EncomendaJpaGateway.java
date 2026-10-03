package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.EncomendaEntity;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository.EncomendaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EncomendaJpaGateway implements EncomendaGateway {

	private static final Sort MAIS_RECENTES_PRIMEIRO = Sort.by(Sort.Direction.DESC, "dataRecebimento", "id");

	private final EncomendaRepository repository;

	@Override
	public Encomenda salvar(Encomenda encomenda) {
		return paraDominio(repository.save(paraEntidade(encomenda)));
	}

	@Override
	public Optional<Encomenda> buscarPorId(Long id) {
		return repository.findById(id).map(EncomendaJpaGateway::paraDominio);
	}

	@Override
	public Pagina<Encomenda> listar(StatusEncomenda status, String apartamento, int pagina, int tamanho) {
		EncomendaEntity filtro = new EncomendaEntity();
		filtro.setStatus(status);
		filtro.setApartamento(apartamento);
		Page<EncomendaEntity> resultado = repository.findAll(Example.of(filtro),
				PageRequest.of(pagina, tamanho, MAIS_RECENTES_PRIMEIRO));
		return new Pagina<>(resultado.map(EncomendaJpaGateway::paraDominio).getContent(), pagina, tamanho,
				resultado.getTotalElements());
	}

	@Override
	public List<Encomenda> listarPorMorador(Long moradorId) {
		return repository.findByMoradorIdOrderByDataRecebimentoDesc(moradorId).stream()
				.map(EncomendaJpaGateway::paraDominio)
				.toList();
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
