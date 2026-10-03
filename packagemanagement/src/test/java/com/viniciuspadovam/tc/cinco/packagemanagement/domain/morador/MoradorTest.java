package com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoradorTest {

	private static final String HASH = "$2a$10$hash";

	@Test
	void deveCriarMoradorNormalizandoDados() {
		Morador morador = Morador.novo(" Ana Souza ", " Ana@Email.com ", HASH, "(11) 98888-7777", " 101 ");

		assertThat(morador.getId()).isNull();
		assertThat(morador.getNome()).isEqualTo("Ana Souza");
		assertThat(morador.getEmail()).isEqualTo("ana@email.com");
		assertThat(morador.getTelefone()).isEqualTo("11988887777");
		assertThat(morador.getApartamento()).isEqualTo("101");
		assertThat(morador.getSenhaHash()).isEqualTo(HASH);
		assertThat(morador.getNomeNormalizado()).isEqualTo("ana souza");
	}

	@Test
	void deveRestaurarMoradorComId() {
		Morador morador = Morador.restaurar(7L, "Ana Souza", "ana@email.com", HASH, "1133334444", "101");

		assertThat(morador.getId()).isEqualTo(7L);
		assertThat(morador.getTelefone()).isEqualTo("1133334444");
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "ana", "ana@", "@email.com", "ana email@x.com"})
	void naoDeveAceitarEmailInvalido(String email) {
		assertThatThrownBy(() -> Morador.novo("Ana", email, HASH, "11988887777", "101"))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("e-mail");
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "123", "119888877770", "abcdefghij"})
	void naoDeveAceitarTelefoneInvalido(String telefone) {
		assertThatThrownBy(() -> Morador.novo("Ana", "ana@email.com", HASH, telefone, "101"))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("telefone");
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "  ", "apto 101", "12345678901"})
	void naoDeveAceitarApartamentoInvalido(String apartamento) {
		assertThatThrownBy(() -> Morador.novo("Ana", "ana@email.com", HASH, "11988887777", apartamento))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("apartamento");
	}

	@Test
	void naoDeveAceitarNomeVazio() {
		assertThatThrownBy(() -> Morador.novo(" ", "ana@email.com", HASH, "11988887777", "101"))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("nome");
	}

	@Test
	void naoDeveAceitarSenhaVazia() {
		assertThatThrownBy(() -> Morador.novo("Ana", "ana@email.com", null, "11988887777", "101"))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("senha");
	}

	@Test
	void deveAtualizarDadosMantendoEmail() {
		Morador morador = Morador.restaurar(1L, "Ana", "ana@email.com", HASH, "11988887777", "101");

		morador.atualizarDados("Ana Maria", "1122223333", "202");

		assertThat(morador.getNome()).isEqualTo("Ana Maria");
		assertThat(morador.getNomeNormalizado()).isEqualTo("ana maria");
		assertThat(morador.getTelefone()).isEqualTo("1122223333");
		assertThat(morador.getApartamento()).isEqualTo("202");
		assertThat(morador.getEmail()).isEqualTo("ana@email.com");
	}

	@Test
	void naoDeveAtualizarComDadosInvalidos() {
		Morador morador = Morador.restaurar(1L, "Ana", "ana@email.com", HASH, "11988887777", "101");

		assertThatThrownBy(() -> morador.atualizarDados("Ana", "1", "101"))
				.isInstanceOf(DadosInvalidosException.class);
		assertThat(morador.getTelefone()).isEqualTo("11988887777");
	}

	@Test
	void deveAlterarSenha() {
		Morador morador = Morador.restaurar(1L, "Ana", "ana@email.com", HASH, "11988887777", "101");

		morador.alterarSenha("$2a$10$novo");

		assertThat(morador.getSenhaHash()).isEqualTo("$2a$10$novo");
	}
}
