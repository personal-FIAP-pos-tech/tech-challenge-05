package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity.FuncionarioEntity;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.repository.FuncionarioRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuncionarioJpaGateway implements FuncionarioGateway {

	private final FuncionarioRepository repository;

	@Override
	public Funcionario salvar(Funcionario funcionario) {
		return paraDominio(repository.save(paraEntidade(funcionario)));
	}

	@Override
	public Optional<Funcionario> buscarPorId(Long id) {
		return repository.findById(id).map(FuncionarioJpaGateway::paraDominio);
	}

	@Override
	public Optional<Funcionario> buscarPorEmail(String email) {
		return repository.findByEmail(email).map(FuncionarioJpaGateway::paraDominio);
	}

	@Override
	public boolean existePorEmail(String email) {
		return repository.existsByEmail(email);
	}

	private static FuncionarioEntity paraEntidade(Funcionario funcionario) {
		return new FuncionarioEntity(funcionario.getId(), funcionario.getNome(), funcionario.getEmail(),
				funcionario.getSenhaHash(), funcionario.getPerfil());
	}

	private static Funcionario paraDominio(FuncionarioEntity entity) {
		return Funcionario.restaurar(entity.getId(), entity.getNome(), entity.getEmail(), entity.getSenhaHash());
	}
}
