package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarEncomendasDoMoradorUseCase {

	private final EncomendaGateway encomendaGateway;

	public List<Encomenda> executar(Long moradorId) {
		return encomendaGateway.listarPorMorador(moradorId);
	}
}
