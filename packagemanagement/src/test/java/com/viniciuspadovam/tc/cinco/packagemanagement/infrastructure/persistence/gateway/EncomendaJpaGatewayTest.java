package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(EncomendaJpaGateway.class)
class EncomendaJpaGatewayTest {

	private static final LocalDateTime RECEBIMENTO = LocalDateTime.of(2026, 10, 2, 8, 30);

	@Autowired
	private EncomendaJpaGateway gateway;

	@Test
	void deveSalvarNovaEncomenda() {
		Encomenda salva = gateway.salvar(
				Encomenda.receber(2L, "Bruno Lima", "102", "Caixa de livros", 3L, RECEBIMENTO));

		assertThat(salva.getId()).isGreaterThan(6L);
		assertThat(gateway.buscarPorId(salva.getId()))
				.get()
				.satisfies(encomenda -> {
					assertThat(encomenda.getMoradorId()).isEqualTo(2L);
					assertThat(encomenda.getNomeDestinatario()).isEqualTo("Bruno Lima");
					assertThat(encomenda.getApartamento()).isEqualTo("102");
					assertThat(encomenda.getDescricao()).isEqualTo("Caixa de livros");
					assertThat(encomenda.getStatus()).isEqualTo(StatusEncomenda.RECEBIDA);
					assertThat(encomenda.getDataRecebimento()).isEqualTo(RECEBIMENTO);
					assertThat(encomenda.getPorteiroRecebimentoId()).isEqualTo(3L);
					assertThat(encomenda.getDataNotificacao()).isNull();
				});
	}

	@Test
	void deveAtualizarStatusDeEncomendaExistente() {
		Encomenda encomenda = gateway.buscarPorId(4L).orElseThrow();
		LocalDateTime retirada = LocalDateTime.of(2026, 10, 2, 19, 0);
		encomenda.registrarRetirada(2L, retirada);

		gateway.salvar(encomenda);

		Encomenda atualizada = gateway.buscarPorId(4L).orElseThrow();
		assertThat(atualizada.getStatus()).isEqualTo(StatusEncomenda.RETIRADA);
		assertThat(atualizada.getDataRetirada()).isEqualTo(retirada);
		assertThat(atualizada.getPorteiroRetiradaId()).isEqualTo(2L);
		assertThat(atualizada.getDataConfirmacao()).isEqualTo(LocalDateTime.of(2026, 9, 29, 18, 0));
	}

	@Test
	void deveRetornarVazioQuandoEncomendaNaoExiste() {
		assertThat(gateway.buscarPorId(999L)).isEmpty();
	}

	@Test
	void deveListarTodasAsEncomendasDaMaisRecenteParaAMaisAntiga() {
		Pagina<Encomenda> pagina = gateway.listar(null, null, 0, 4);

		assertThat(pagina.totalElementos()).isEqualTo(6);
		assertThat(pagina.totalPaginas()).isEqualTo(2);
		assertThat(pagina.conteudo()).extracting(Encomenda::getId).containsExactly(6L, 5L, 4L, 3L);
	}

	@Test
	void deveFiltrarPorStatus() {
		Pagina<Encomenda> pagina = gateway.listar(StatusEncomenda.RETIRADA, null, 0, 20);

		assertThat(pagina.conteudo()).extracting(Encomenda::getId).containsExactly(6L, 5L);
	}

	@Test
	void deveFiltrarPorStatusEApartamento() {
		assertThat(gateway.listar(StatusEncomenda.NOTIFICADA, "201", 0, 20).conteudo())
				.extracting(Encomenda::getId).containsExactly(3L);
		assertThat(gateway.listar(null, "101", 0, 20).conteudo())
				.extracting(Encomenda::getId).containsExactly(1L);
	}

	@Test
	void deveListarEncomendasDoMorador() {
		gateway.salvar(Encomenda.receber(2L, "Bruno Lima", "102", "Outra caixa", 3L, RECEBIMENTO));

		assertThat(gateway.listarPorMorador(2L))
				.extracting(Encomenda::getDescricao)
				.containsExactly("Outra caixa", "Envelope - Correios");
	}
}
