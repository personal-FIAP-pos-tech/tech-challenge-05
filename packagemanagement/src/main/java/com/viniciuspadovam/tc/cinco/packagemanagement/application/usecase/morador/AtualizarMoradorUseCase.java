package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.NomeNormalizado;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AtualizarMoradorUseCase {

	private final MoradorGateway moradorGateway;
	private final SenhaEncoder senhaEncoder;

	public Morador executar(Long moradorId, AtualizarMoradorCommand command) {
		Morador morador = moradorGateway.buscarPorId(moradorId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Morador", moradorId));

		String apartamento = Validacoes.apartamento(command.apartamento());
		String nomeNormalizado = NomeNormalizado.de(command.nome()).valor();
		moradorGateway.buscarPorApartamentoENome(apartamento, nomeNormalizado)
				.filter(outro -> !outro.getId().equals(moradorId))
				.ifPresent(outro -> {
					throw new ConflitoException("Já existe um morador com este nome neste apartamento.");
				});

		morador.atualizarDados(command.nome(), command.telefone(), apartamento);
		if (command.novaSenha() != null && !command.novaSenha().isEmpty()) {
			morador.alterarSenha(senhaEncoder.codificar(Validacoes.senha(command.novaSenha())));
		}
		return moradorGateway.salvar(morador);
	}
}
