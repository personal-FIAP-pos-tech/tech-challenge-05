package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarFuncionarioUseCase {

	private final FuncionarioGateway funcionarioGateway;
	private final SenhaEncoder senhaEncoder;

	public Funcionario executar(Long funcionarioId, AtualizarFuncionarioCommand command) {
		Funcionario funcionario = funcionarioGateway.buscarPorId(funcionarioId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Funcionário", funcionarioId));

		funcionario.atualizarNome(command.nome());
		if (command.novaSenha() != null && !command.novaSenha().isEmpty()) {
			funcionario.alterarSenha(senhaEncoder.codificar(Validacoes.senha(command.novaSenha())));
		}
		return funcionarioGateway.salvar(funcionario);
	}
}
