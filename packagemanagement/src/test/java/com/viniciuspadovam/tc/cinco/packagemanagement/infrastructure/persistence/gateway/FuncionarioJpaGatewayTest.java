package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(FuncionarioJpaGateway.class)
class FuncionarioJpaGatewayTest {

	@Autowired
	private FuncionarioJpaGateway gateway;

	@Test
	void deveSalvarNovoFuncionario() {
		Funcionario salvo = gateway.salvar(Funcionario.novo("Tiago Ramos", "tiago@portaria.com", "hash"));

		assertThat(salvo.getId()).isGreaterThan(6L);
		assertThat(gateway.buscarPorId(salvo.getId()))
				.get()
				.extracting(Funcionario::getNome, Funcionario::getEmail, Funcionario::getPerfil)
				.containsExactly("Tiago Ramos", "tiago@portaria.com", Perfil.PORTEIRO);
	}

	@Test
	void deveAtualizarFuncionarioExistente() {
		Funcionario funcionario = gateway.buscarPorId(1L).orElseThrow();
		funcionario.atualizarNome("Carlos Pereira Junior");
		funcionario.alterarSenha("novo-hash");

		gateway.salvar(funcionario);

		Funcionario atualizado = gateway.buscarPorId(1L).orElseThrow();
		assertThat(atualizado.getNome()).isEqualTo("Carlos Pereira Junior");
		assertThat(atualizado.getSenhaHash()).isEqualTo("novo-hash");
	}

	@Test
	void deveBuscarFuncionarioDaCargaInicialPorEmail() {
		assertThat(gateway.buscarPorEmail("joana.alves@portaria.com"))
				.get()
				.extracting(Funcionario::getId)
				.isEqualTo(2L);
	}

	@Test
	void deveInformarSeEmailExiste() {
		assertThat(gateway.existePorEmail("sandra.costa@portaria.com")).isTrue();
		assertThat(gateway.existePorEmail("ninguem@portaria.com")).isFalse();
	}
}
