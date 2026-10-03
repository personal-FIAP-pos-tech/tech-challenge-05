package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.MoradorEntity;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository.MoradorRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MoradorJpaGateway implements MoradorGateway {

	private final MoradorRepository repository;

	@Override
	public Morador salvar(Morador morador) {
		return paraDominio(repository.save(paraEntidade(morador)));
	}

	@Override
	public Optional<Morador> buscarPorId(Long id) {
		return repository.findById(id).map(MoradorJpaGateway::paraDominio);
	}

	@Override
	public Optional<Morador> buscarPorEmail(String email) {
		return repository.findByEmail(email).map(MoradorJpaGateway::paraDominio);
	}

	@Override
	public boolean existePorEmail(String email) {
		return repository.existsByEmail(email);
	}

	@Override
	public Optional<Morador> buscarPorApartamentoENome(String apartamento, String nomeNormalizado) {
		return repository.findByApartamentoAndNomeNormalizado(apartamento, nomeNormalizado)
				.map(MoradorJpaGateway::paraDominio);
	}

	private static MoradorEntity paraEntidade(Morador morador) {
		return new MoradorEntity(morador.getId(), morador.getNome(), morador.getNomeNormalizado(),
				morador.getEmail(), morador.getSenhaHash(), morador.getTelefone(), morador.getApartamento());
	}

	private static Morador paraDominio(MoradorEntity entity) {
		return Morador.restaurar(entity.getId(), entity.getNome(), entity.getEmail(), entity.getSenhaHash(),
				entity.getTelefone(), entity.getApartamento());
	}
}
