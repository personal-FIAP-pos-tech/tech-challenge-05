package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListarEncomendasUseCase {

	private static final int TAMANHO_MAXIMO_PAGINA = 100;

	private final EncomendaGateway encomendaGateway;

	public Pagina<Encomenda> executar(StatusEncomenda status, String apartamento, int pagina, int tamanho) {
		if (pagina < 0 || tamanho < 1 || tamanho > TAMANHO_MAXIMO_PAGINA) {
			throw new DadosInvalidosException("A página deve ser maior ou igual a 0 e o tamanho deve estar entre 1 e "
					+ TAMANHO_MAXIMO_PAGINA + ".");
		}
		String filtroApartamento = apartamento == null || apartamento.isBlank() ? null : apartamento.strip();
		return encomendaGateway.listar(status, filtroApartamento, pagina, tamanho);
	}
}
