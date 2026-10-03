package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RegistrarRetiradaUseCase {

	private final EncomendaGateway encomendaGateway;
	private final Clock clock;

	public Encomenda executar(Long porteiroId, Long encomendaId) {
		Encomenda encomenda = encomendaGateway.buscarPorId(encomendaId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Encomenda", encomendaId));
		encomenda.registrarRetirada(porteiroId, LocalDateTime.now(clock));
		return encomendaGateway.salvar(encomenda);
	}
}
