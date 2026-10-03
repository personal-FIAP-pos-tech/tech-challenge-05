package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.Notificacao;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

@DataJpaTest
@Import(NotificacaoJpaGateway.class)
class NotificacaoJpaGatewayTest {

	@Autowired
	private NotificacaoJpaGateway gateway;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	void deveSalvarNovaNotificacao() {
		jdbcTemplate.update("""
				INSERT INTO encomendas (id, morador_id, nome_destinatario, apartamento, descricao, status,
					data_recebimento, porteiro_recebimento_id)
				VALUES (50, 2, 'Bruno Lima', '102', 'Pacote', 'RECEBIDA', TIMESTAMP '2026-10-02 10:00:00', 1)
				""");
		LocalDateTime criacao = LocalDateTime.of(2026, 10, 2, 10, 1);

		Notificacao salva = gateway.salvar(Notificacao.restaurar(null, 50L, 2L, "bruno.lima@email.com", "Assunto",
				"Mensagem longa", StatusNotificacao.PENDENTE, criacao, null, null));

		assertThat(salva.getId()).isGreaterThan(6L);
		assertThat(gateway.buscarPorId(salva.getId()))
				.get()
				.satisfies(notificacao -> {
					assertThat(notificacao.getEncomendaId()).isEqualTo(50L);
					assertThat(notificacao.getMoradorId()).isEqualTo(2L);
					assertThat(notificacao.getDestinatario()).isEqualTo("bruno.lima@email.com");
					assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.PENDENTE);
					assertThat(notificacao.getDataCriacao()).isEqualTo(criacao);
				});
	}

	@Test
	void deveAtualizarStatusDaNotificacao() {
		Notificacao notificacao = gateway.buscarPorId(2L).orElseThrow();
		LocalDateTime confirmacao = LocalDateTime.of(2026, 10, 2, 12, 0);
		notificacao.confirmar(confirmacao);

		gateway.salvar(notificacao);

		Notificacao atualizada = gateway.buscarPorId(2L).orElseThrow();
		assertThat(atualizada.getStatus()).isEqualTo(StatusNotificacao.CONFIRMADA);
		assertThat(atualizada.getDataConfirmacao()).isEqualTo(confirmacao);
	}

	@Test
	void deveListarNotificacoesDoMorador() {
		assertThat(gateway.listarPorMorador(4L))
				.extracting(Notificacao::getId, Notificacao::getStatus)
				.containsExactly(tuple(4L, StatusNotificacao.CONFIRMADA));
		assertThat(gateway.listarPorMorador(999L)).isEmpty();
	}

	@Test
	void deveBuscarNotificacaoPelaEncomenda() {
		assertThat(gateway.buscarPorEncomendaId(3L))
				.get()
				.extracting(Notificacao::getId, Notificacao::getMoradorId)
				.containsExactly(3L, 3L);
		assertThat(gateway.buscarPorEncomendaId(999L)).isEmpty();
	}
}
