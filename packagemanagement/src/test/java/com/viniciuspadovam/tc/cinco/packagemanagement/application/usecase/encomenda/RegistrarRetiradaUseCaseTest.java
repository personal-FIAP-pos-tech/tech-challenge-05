package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrarRetiradaUseCaseTest {

	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 1, 19, 30);

	@Mock
	private EncomendaGateway encomendaGateway;

	private RegistrarRetiradaUseCase useCase;

	@BeforeEach
	void configurar() {
		useCase = new RegistrarRetiradaUseCase(encomendaGateway,
				Clock.fixed(Instant.parse("2026-10-01T19:30:00Z"), ZoneOffset.UTC));
	}

	private Encomenda encomenda(StatusEncomenda status) {
		return Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa", status, AGORA.minusHours(10), 5L,
				AGORA.minusHours(9), status == StatusEncomenda.CONFIRMADA ? AGORA.minusHours(1) : null, null, null);
	}

	@Test
	void porteiroDeveDarBaixaEmEncomendaConfirmada() {
		Encomenda encomenda = encomenda(StatusEncomenda.CONFIRMADA);
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));
		when(encomendaGateway.salvar(encomenda)).thenReturn(encomenda);

		Encomenda retirada = useCase.executar(6L, 30L);

		assertThat(retirada.getStatus()).isEqualTo(StatusEncomenda.RETIRADA);
		assertThat(retirada.getDataRetirada()).isEqualTo(AGORA);
		assertThat(retirada.getPorteiroRetiradaId()).isEqualTo(6L);
	}

	@Test
	void naoDeveDarBaixaSemConfirmacaoDoMorador() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda(StatusEncomenda.NOTIFICADA)));

		assertThatThrownBy(() -> useCase.executar(6L, 30L))
				.isInstanceOf(RegraNegocioException.class)
				.hasMessageContaining("confirmar");
		verify(encomendaGateway, never()).salvar(any());
	}

	@Test
	void deveFalharQuandoEncomendaNaoExiste() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(6L, 30L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}
}
