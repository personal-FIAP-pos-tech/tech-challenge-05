package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CadastrarFuncionarioUseCase {

	private final FuncionarioGateway funcionarioGateway;
	private final MoradorGateway moradorGateway;
	private final SenhaEncoder senhaEncoder;

	public Funcionario executar(CadastrarFuncionarioCommand command) {
		String senha = Validacoes.senha(command.senha());
		String email = Validacoes.email(command.email());
		if (funcionarioGateway.existePorEmail(email) || moradorGateway.existePorEmail(email)) {
			throw new ConflitoException("Já existe um usuário cadastrado com este e-mail.");
		}
		Funcionario funcionario = Funcionario.novo(command.nome(), email, senhaEncoder.codificar(senha));
		return funcionarioGateway.salvar(funcionario);
	}
}
