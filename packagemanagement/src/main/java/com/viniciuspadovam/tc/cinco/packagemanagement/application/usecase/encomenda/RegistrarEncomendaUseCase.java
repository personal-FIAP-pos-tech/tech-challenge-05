package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.NomeNormalizado;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarEncomendaUseCase {

	private final MoradorGateway moradorGateway;
	private final EncomendaGateway encomendaGateway;
	private final EncomendaRecebidaPublisher encomendaRecebidaPublisher;
	private final Clock clock;

	public Encomenda executar(Long porteiroId, RegistrarEncomendaCommand command) {
		String apartamento = Validacoes.apartamento(command.apartamento());
		String nomeNormalizado = NomeNormalizado.de(command.nomeDestinatario()).valor();
		Morador morador = moradorGateway.buscarPorApartamentoENome(apartamento, nomeNormalizado)
				.orElseThrow(() -> moradorNaoEncontrado(command.nomeDestinatario(), apartamento));

		Encomenda encomenda = Encomenda.receber(morador.getId(), command.nomeDestinatario(), apartamento,
				command.descricao(), porteiroId, LocalDateTime.now(clock));
		Encomenda salva = encomendaGateway.salvar(encomenda);
		encomendaRecebidaPublisher.publicar(salva.getId());
		return salva;
	}

	private static RegraNegocioException moradorNaoEncontrado(String nomeDestinatario, String apartamento) {
		return new RegraNegocioException("Nenhum morador chamado '" + nomeDestinatario.strip()
				+ "' foi encontrado no apartamento " + apartamento + ".");
	}
}
