package com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StatusEncomendaTest {

	@ParameterizedTest
	@CsvSource({
		"RECEBIDA, NOTIFICADA, true",
		"NOTIFICADA, CONFIRMADA, true",
		"CONFIRMADA, RETIRADA, true",
		"RECEBIDA, CONFIRMADA, false",
		"RECEBIDA, RETIRADA, false",
		"NOTIFICADA, RETIRADA, false",
		"RETIRADA, RECEBIDA, false",
		"CONFIRMADA, NOTIFICADA, false"
	})
	void deveValidarTransicoesPermitidas(StatusEncomenda origem, StatusEncomenda destino, boolean permitida) {
		assertThat(origem.podeIrPara(destino)).isEqualTo(permitida);
	}
}
