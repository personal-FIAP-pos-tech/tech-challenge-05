package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
class CargaInicialTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@ParameterizedTest
	@ValueSource(strings = {"moradores", "funcionarios", "encomendas", "notificacoes"})
	void deveCarregarSeisRegistrosPorTabela(String tabela) {
		Integer total = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tabela, Integer.class);

		assertThat(total).isEqualTo(6);
	}

	@Test
	void encomendasDevemCobrirTodosOsStatus() {
		List<String> status = jdbcTemplate.queryForList("SELECT DISTINCT status FROM encomendas", String.class);

		assertThat(status).containsExactlyInAnyOrder("RECEBIDA", "NOTIFICADA", "CONFIRMADA", "RETIRADA");
	}

	@Test
	void notificacoesDevemCobrirOsStatusDeEnvio() {
		List<String> status = jdbcTemplate.queryForList("SELECT DISTINCT status FROM notificacoes", String.class);

		assertThat(status).containsExactlyInAnyOrder("FALHA", "ENVIADA", "CONFIRMADA");
	}

	@Test
	void novosRegistrosDevemContinuarAposACargaInicial() {
		jdbcTemplate.update("INSERT INTO funcionarios (nome, email, senha_hash) VALUES ('Teste', 't@t.com', 'h')");

		Long id = jdbcTemplate.queryForObject("SELECT id FROM funcionarios WHERE email = 't@t.com'", Long.class);

		assertThat(id).isEqualTo(7L);
	}
}
