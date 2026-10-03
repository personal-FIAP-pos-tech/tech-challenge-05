package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarFuncionarioUseCase {

	private final FuncionarioGateway funcionarioGateway;

	public Funcionario executar(Long funcionarioId) {
		return funcionarioGateway.buscarPorId(funcionarioId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário", funcionarioId));
	}
}
