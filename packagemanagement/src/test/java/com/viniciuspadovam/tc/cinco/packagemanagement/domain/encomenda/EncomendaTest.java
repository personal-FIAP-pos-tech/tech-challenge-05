package com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class EncomendaTest {

	private static final LocalDateTime RECEBIMENTO = LocalDateTime.of(2026, 10, 1, 9, 0);
	private static final LocalDateTime NOTIFICACAO = RECEBIMENTO.plusMinutes(1);
	private static final LocalDateTime CONFIRMACAO = RECEBIMENTO.plusHours(2);
	private static final LocalDateTime RETIRADA = RECEBIMENTO.plusHours(5);
	private static final Long PORTEIRO_RECEBIMENTO = 10L;
	private static final Long PORTEIRO_RETIRADA = 11L;

	private Encomenda novaEncomenda() {
		return Encomenda.receber(1L, " Ana Souza ", " 101 ", " Caixa média Amazon ", PORTEIRO_RECEBIMENTO, RECEBIMENTO);
	}

	@Test
	void deveReceberEncomendaComStatusRecebida() {
		Encomenda encomenda = novaEncomenda();

		assertThat(encomenda.getId()).isNull();
		assertThat(encomenda.getMoradorId()).isEqualTo(1L);
		assertThat(encomenda.getNomeDestinatario()).isEqualTo("Ana Souza");
		assertThat(encomenda.getApartamento()).isEqualTo("101");
		assertThat(encomenda.getDescricao()).isEqualTo("Caixa média Amazon");
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.RECEBIDA);
		assertThat(encomenda.getDataRecebimento()).isEqualTo(RECEBIMENTO);
		assertThat(encomenda.getPorteiroRecebimentoId()).isEqualTo(PORTEIRO_RECEBIMENTO);
		assertThat(encomenda.getDataNotificacao()).isNull();
		assertThat(encomenda.getDataConfirmacao()).isNull();
		assertThat(encomenda.getDataRetirada()).isNull();
		assertThat(encomenda.getPorteiroRetiradaId()).isNull();
	}

	@Test
	void naoDeveReceberSemDescricao() {
		assertThatThrownBy(() -> Encomenda.receber(1L, "Ana", "101", " ", PORTEIRO_RECEBIMENTO, RECEBIMENTO))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("descrição");
	}

	@Test
	void naoDeveReceberComDescricaoMuitoLonga() {
		String descricao = "x".repeat(256);

		assertThatThrownBy(() -> Encomenda.receber(1L, "Ana", "101", descricao, PORTEIRO_RECEBIMENTO, RECEBIMENTO))
				.isInstanceOf(DadosInvalidosException.class)
				.hasMessageContaining("descrição");
	}

	@Test
	void naoDeveReceberSemMoradorOuPorteiro() {
		assertThatThrownBy(() -> Encomenda.receber(null, "Ana", "101", "Caixa", PORTEIRO_RECEBIMENTO, RECEBIMENTO))
				.isInstanceOf(DadosInvalidosException.class);
		assertThatThrownBy(() -> Encomenda.receber(1L, "Ana", "101", "Caixa", null, RECEBIMENTO))
				.isInstanceOf(DadosInvalidosException.class);
	}

	@Test
	void devePercorrerOCicloCompleto() {
		Encomenda encomenda = novaEncomenda();

		encomenda.marcarNotificada(NOTIFICACAO);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.NOTIFICADA);
		assertThat(encomenda.getDataNotificacao()).isEqualTo(NOTIFICACAO);

		encomenda.confirmarRecebimento(CONFIRMACAO);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.CONFIRMADA);
		assertThat(encomenda.getDataConfirmacao()).isEqualTo(CONFIRMACAO);

		encomenda.registrarRetirada(PORTEIRO_RETIRADA, RETIRADA);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.RETIRADA);
		assertThat(encomenda.getDataRetirada()).isEqualTo(RETIRADA);
		assertThat(encomenda.getPorteiroRetiradaId()).isEqualTo(PORTEIRO_RETIRADA);
	}

	@Test
	void naoDeveConfirmarAntesDeNotificar() {
		Encomenda encomenda = novaEncomenda();

		assertThatThrownBy(() -> encomenda.confirmarRecebimento(CONFIRMACAO))
				.isInstanceOf(RegraNegocioException.class);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.RECEBIDA);
	}

	@Test
	void naoDeveRegistrarRetiradaSemConfirmacaoDoMorador() {
		Encomenda encomenda = novaEncomenda();
		encomenda.marcarNotificada(NOTIFICACAO);

		assertThatThrownBy(() -> encomenda.registrarRetirada(PORTEIRO_RETIRADA, RETIRADA))
				.isInstanceOf(RegraNegocioException.class)
				.hasMessageContaining("confirmar");
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.NOTIFICADA);
		assertThat(encomenda.getDataRetirada()).isNull();
	}

	@Test
	void naoDeveRegistrarRetiradaDuasVezes() {
		Encomenda encomenda = novaEncomenda();
		encomenda.marcarNotificada(NOTIFICACAO);
		encomenda.confirmarRecebimento(CONFIRMACAO);
		encomenda.registrarRetirada(PORTEIRO_RETIRADA, RETIRADA);

		assertThatThrownBy(() -> encomenda.registrarRetirada(PORTEIRO_RETIRADA, RETIRADA.plusHours(1)))
				.isInstanceOf(RegraNegocioException.class)
				.hasMessageContaining("já foi retirada");
	}

	@Test
	void naoDeveNotificarDuasVezes() {
		Encomenda encomenda = novaEncomenda();
		encomenda.marcarNotificada(NOTIFICACAO);

		assertThatThrownBy(() -> encomenda.marcarNotificada(NOTIFICACAO))
				.isInstanceOf(RegraNegocioException.class);
	}

	@Test
	void naoDeveRegistrarRetiradaSemPorteiro() {
		Encomenda encomenda = novaEncomenda();
		encomenda.marcarNotificada(NOTIFICACAO);
		encomenda.confirmarRecebimento(CONFIRMACAO);

		assertThatThrownBy(() -> encomenda.registrarRetirada(null, RETIRADA))
				.isInstanceOf(DadosInvalidosException.class);
	}

	@Test
	void deveRestaurarEncomendaComTodosOsCampos() {
		Encomenda encomenda = Encomenda.restaurar(5L, 1L, "Ana", "101", "Caixa", StatusEncomenda.RETIRADA,
				RECEBIMENTO, PORTEIRO_RECEBIMENTO, NOTIFICACAO, CONFIRMACAO, RETIRADA, PORTEIRO_RETIRADA);

		assertThat(encomenda.getId()).isEqualTo(5L);
		assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.RETIRADA);
		assertThat(encomenda.getPorteiroRetiradaId()).isEqualTo(PORTEIRO_RETIRADA);
	}
}
