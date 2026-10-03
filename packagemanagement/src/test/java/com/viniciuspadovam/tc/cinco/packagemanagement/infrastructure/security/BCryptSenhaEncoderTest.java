package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptSenhaEncoderTest {

	private final BCryptSenhaEncoder encoder = new BCryptSenhaEncoder(new BCryptPasswordEncoder());

	@Test
	void deveCodificarSenhaSemGuardarTextoPuro() {
		String hash = encoder.codificar("Senha@123");

		assertThat(hash).isNotEqualTo("Senha@123").startsWith("$2a$");
		assertThat(encoder.confere("Senha@123", hash)).isTrue();
		assertThat(encoder.confere("Outra@123", hash)).isFalse();
	}

	@Test
	void deveConferirSenhaDaCargaInicial() {
		String hashCargaInicial = "$2a$10$1sIzJdWMkly4c6LEER7UNuAy7wES9bXeutH7t5.Pjo3..9ZQAS9jW";

		assertThat(encoder.confere("Senha@123", hashCargaInicial)).isTrue();
	}
}
