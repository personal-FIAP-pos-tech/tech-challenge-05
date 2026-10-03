package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.NomeNormalizado;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CadastrarMoradorUseCase {

	private final MoradorGateway moradorGateway;
	private final FuncionarioGateway funcionarioGateway;
	private final SenhaEncoder senhaEncoder;

	public Morador executar(CadastrarMoradorCommand command) {
		String senha = Validacoes.senha(command.senha());
		String email = Validacoes.email(command.email());
		if (moradorGateway.existePorEmail(email) || funcionarioGateway.existePorEmail(email)) {
			throw new ConflitoException("Já existe um usuário cadastrado com este e-mail.");
		}
		String apartamento = Validacoes.apartamento(command.apartamento());
		String nomeNormalizado = NomeNormalizado.de(command.nome()).valor();
		if (moradorGateway.buscarPorApartamentoENome(apartamento, nomeNormalizado).isPresent()) {
			throw new ConflitoException("Já existe um morador com este nome neste apartamento.");
		}
		Morador morador = Morador.novo(command.nome(), email, senhaEncoder.codificar(senha), command.telefone(),
				apartamento);
		return moradorGateway.salvar(morador);
	}
}
