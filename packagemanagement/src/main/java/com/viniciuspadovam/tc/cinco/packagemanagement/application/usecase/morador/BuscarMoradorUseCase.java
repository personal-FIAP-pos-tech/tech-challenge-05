package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BuscarMoradorUseCase {

	private final MoradorGateway moradorGateway;

	public Morador executar(Long moradorId) {
		return moradorGateway.buscarPorId(moradorId)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Morador", moradorId));
	}
}
