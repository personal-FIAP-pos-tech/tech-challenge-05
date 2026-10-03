package com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class NomeNormalizadoTest {

	@Test
	void deveIgnorarMaiusculasAcentosEEspacosExtras() {
		NomeNormalizado nome = NomeNormalizado.de("  João   DA Silva  Conceição ");

		assertThat(nome.valor()).isEqualTo("joao da silva conceicao");
	}

	@Test
	void nomesEquivalentesDevemSerIguais() {
		assertThat(NomeNormalizado.de("MARIA José"))
				.isEqualTo(NomeNormalizado.de("maria   jose"));
	}

	@Test
	void nomesDiferentesNaoDevemSerIguais() {
		assertThat(NomeNormalizado.de("Maria Jose"))
				.isNotEqualTo(NomeNormalizado.de("Maria Joseane"));
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   ", "\t"})
	void naoDeveAceitarNomeVazio(String nome) {
		assertThatThrownBy(() -> NomeNormalizado.de(nome))
				.isInstanceOf(DadosInvalidosException.class);
	}
}
