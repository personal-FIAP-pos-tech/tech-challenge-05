package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import java.util.Optional;

public interface FuncionarioGateway {

	Funcionario salvar(Funcionario funcionario);

	Optional<Funcionario> buscarPorId(Long id);

	Optional<Funcionario> buscarPorEmail(String email);

	boolean existePorEmail(String email);
}
