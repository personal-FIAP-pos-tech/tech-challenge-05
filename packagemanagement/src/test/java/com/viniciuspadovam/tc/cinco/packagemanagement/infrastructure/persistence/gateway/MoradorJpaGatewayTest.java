package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(MoradorJpaGateway.class)
class MoradorJpaGatewayTest {

	@Autowired
	private MoradorJpaGateway gateway;

	@Test
	void deveSalvarNovoMoradorComIdAposACargaInicial() {
		Morador salvo = gateway.salvar(
				Morador.novo("Gustavo Prado", "gustavo@email.com", "hash", "11977770000", "401"));

		assertThat(salvo.getId()).isGreaterThan(6L);
		assertThat(gateway.buscarPorId(salvo.getId()))
				.get()
				.satisfies(morador -> {
					assertThat(morador.getNome()).isEqualTo("Gustavo Prado");
					assertThat(morador.getEmail()).isEqualTo("gustavo@email.com");
					assertThat(morador.getTelefone()).isEqualTo("11977770000");
					assertThat(morador.getApartamento()).isEqualTo("401");
					assertThat(morador.getSenhaHash()).isEqualTo("hash");
				});
	}

	@Test
	void deveAtualizarMoradorExistente() {
		Morador morador = gateway.buscarPorId(1L).orElseThrow();
		morador.atualizarDados("Ana Souza Lima", "1133334444", "105");

		gateway.salvar(morador);

		Morador atualizado = gateway.buscarPorId(1L).orElseThrow();
		assertThat(atualizado.getNome()).isEqualTo("Ana Souza Lima");
		assertThat(atualizado.getApartamento()).isEqualTo("105");
		assertThat(gateway.buscarPorApartamentoENome("105", "ana souza lima")).isPresent();
	}

	@Test
	void deveBuscarMoradorDaCargaInicialPorEmail() {
		assertThat(gateway.buscarPorEmail("ana.souza@email.com"))
				.get()
				.extracting(Morador::getId, Morador::getNome, Morador::getApartamento)
				.containsExactly(1L, "Ana Souza", "101");
	}

	@Test
	void deveInformarSeEmailExiste() {
		assertThat(gateway.existePorEmail("bruno.lima@email.com")).isTrue();
		assertThat(gateway.existePorEmail("ninguem@email.com")).isFalse();
	}

	@Test
	void deveBuscarPorApartamentoENomeNormalizado() {
		assertThat(gateway.buscarPorApartamentoENome("302", "fabio goncalves"))
				.get()
				.extracting(Morador::getNome)
				.isEqualTo("Fábio Gonçalves");
		assertThat(gateway.buscarPorApartamentoENome("301", "fabio goncalves")).isEmpty();
	}

	@Test
	void deveRetornarVazioQuandoIdNaoExiste() {
		assertThat(gateway.buscarPorId(999L)).isEmpty();
	}
}
