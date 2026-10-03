package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarEncomendaUseCaseTest {

	private static final Long PORTEIRO_ID = 5L;
	private static final Instant AGORA = Instant.parse("2026-10-01T12:00:00Z");

	@Mock
	private MoradorGateway moradorGateway;

	@Mock
	private EncomendaGateway encomendaGateway;

	@Mock
	private EncomendaRecebidaPublisher publisher;

	private RegistrarEncomendaUseCase useCase;

	private final Morador ana = Morador.restaurar(1L, "Ana Souza", "ana@email.com", "hash", "11988887777", "101");

	@BeforeEach
	void configurar() {
		useCase = new RegistrarEncomendaUseCase(moradorGateway, encomendaGateway, publisher,
				Clock.fixed(AGORA, ZoneOffset.UTC));
	}

	@Test
	void deveRegistrarEncomendaEColocarNaFilaDeProcessamento() {
		when(moradorGateway.buscarPorApartamentoENome("101", "ana souza")).thenReturn(Optional.of(ana));
		when(encomendaGateway.salvar(any())).thenAnswer(invocacao -> comId(invocacao.getArgument(0), 30L));

		Encomenda encomenda = useCase.executar(PORTEIRO_ID,
				new RegistrarEncomendaCommand("ANA  Souza", " 101 ", "Caixa Amazon"));

		ArgumentCaptor<Encomenda> captor = ArgumentCaptor.forClass(Encomenda.class);
		InOrder ordem = inOrder(encomendaGateway, publisher);
		ordem.verify(encomendaGateway).salvar(captor.capture());
		ordem.verify(publisher).publicar(30L);

		Encomenda salva = captor.getValue();
		assertThat(salva.getMoradorId()).isEqualTo(1L);
		assertThat(salva.getNomeDestinatario()).isEqualTo("ANA  Souza");
		assertThat(salva.getApartamento()).isEqualTo("101");
		assertThat(salva.getStatus()).isEqualTo(StatusEncomenda.RECEBIDA);
		assertThat(salva.getPorteiroRecebimentoId()).isEqualTo(PORTEIRO_ID);
		assertThat(salva.getDataRecebimento()).isEqualTo(LocalDateTime.of(2026, 10, 1, 12, 0));
		assertThat(encomenda.getId()).isEqualTo(30L);
	}

	@Test
	void naoDeveRegistrarQuandoNomeEApartamentoNaoCasamComMorador() {
		when(moradorGateway.buscarPorApartamentoENome("102", "ana souza")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(PORTEIRO_ID,
				new RegistrarEncomendaCommand("Ana Souza", "102", "Caixa Amazon")))
				.isInstanceOf(RegraNegocioException.class)
				.hasMessageContaining("Ana Souza")
				.hasMessageContaining("102");
		verify(encomendaGateway, never()).salvar(any());
		verify(publisher, never()).publicar(anyLong());
	}

	@Test
	void naoDeveRegistrarSemDescricao() {
		when(moradorGateway.buscarPorApartamentoENome("101", "ana souza")).thenReturn(Optional.of(ana));

		assertThatThrownBy(() -> useCase.executar(PORTEIRO_ID,
				new RegistrarEncomendaCommand("Ana Souza", "101", "")))
				.isInstanceOf(DadosInvalidosException.class);
		verify(publisher, never()).publicar(anyLong());
	}

	@Test
	void naoDeveRegistrarComApartamentoInvalido() {
		assertThatThrownBy(() -> useCase.executar(PORTEIRO_ID,
				new RegistrarEncomendaCommand("Ana Souza", "apto 101", "Caixa")))
				.isInstanceOf(DadosInvalidosException.class);
	}

	private static Encomenda comId(Encomenda e, Long id) {
		return Encomenda.restaurar(id, e.getMoradorId(), e.getNomeDestinatario(), e.getApartamento(),
				e.getDescricao(), e.getStatus(), e.getDataRecebimento(), e.getPorteiroRecebimentoId(),
				e.getDataNotificacao(), e.getDataConfirmacao(), e.getDataRetirada(), e.getPorteiroRetiradaId());
	}
}
