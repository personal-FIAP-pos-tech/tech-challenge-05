package com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import org.junit.jupiter.api.Test;

class FuncionarioTest {

	private static final String HASH = "$2a$10$hash";

	@Test
	void deveCriarFuncionarioComoPorteiro() {
		Funcionario funcionario = Funcionario.novo(" Carlos Lima ", "Carlos@Portaria.com", HASH);

		assertThat(funcionario.getId()).isNull();
		assertThat(funcionario.getNome()).isEqualTo("Carlos Lima");
		assertThat(funcionario.getEmail()).isEqualTo("carlos@portaria.com");
		assertThat(funcionario.getSenhaHash()).isEqualTo(HASH);
		assertThat(funcionario.getPerfil()).isEqualTo(Perfil.PORTEIRO);
	}

	@Test
	void deveRestaurarFuncionarioComId() {
		Funcionario funcionario = Funcionario.restaurar(3L, "Carlos", "carlos@portaria.com", HASH);

		assertThat(funcionario.getId()).isEqualTo(3L);
	}

	@Test
	void naoDeveAceitarEmailInvalido() {
		assertThatThrownBy(() -> Funcionario.novo("Carlos", "carlos", HASH))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("e-mail");
	}

	@Test
	void naoDeveAceitarNomeVazio() {
		assertThatThrownBy(() -> Funcionario.novo("", "carlos@portaria.com", HASH))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("nome");
	}

	@Test
	void deveAtualizarNomeESenha() {
		Funcionario funcionario = Funcionario.restaurar(3L, "Carlos", "carlos@portaria.com", HASH);

		funcionario.atualizarNome("Carlos Eduardo");
		funcionario.alterarSenha("$2a$10$novo");

		assertThat(funcionario.getNome()).isEqualTo("Carlos Eduardo");
		assertThat(funcionario.getSenhaHash()).isEqualTo("$2a$10$novo");
	}
}
