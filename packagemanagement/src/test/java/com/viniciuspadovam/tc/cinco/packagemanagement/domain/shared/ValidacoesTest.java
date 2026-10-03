package com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidacoesTest {

	@Test
	void deveAceitarSenhaComOitoOuMaisCaracteres() {
		assertThat(Validacoes.senha("Senha@123")).isEqualTo("Senha@123");
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"1234567", "        "})
	void naoDeveAceitarSenhaCurtaOuVazia(String senha) {
		assertThatThrownBy(() -> Validacoes.senha(senha))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("senha");
	}

	@Test
	void naoDeveAceitarSenhaMaiorQueOLimiteDoBcrypt() {
		assertThatThrownBy(() -> Validacoes.senha("a".repeat(73)))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("72");
	}

	@Test
	void naoDeveAceitarIdNulo() {
		assertThatThrownBy(() -> Validacoes.idObrigatorio(null, "morador"))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("morador");
	}
}
