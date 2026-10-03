package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarEncomendaUseCase {

	private final EncomendaGateway encomendaGateway;

	public Encomenda executar(Long encomendaId) {
		return encomendaGateway.buscarPorId(encomendaId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Encomenda", encomendaId));
	}
}
