package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@ExtendWith(MockitoExtension.class)
class ConsultasEncomendaUseCaseTest {

	@Mock
	private EncomendaGateway encomendaGateway;

	private final Encomenda encomenda = Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa",
			StatusEncomenda.RECEBIDA, LocalDateTime.of(2026, 10, 1, 9, 0), 5L, null, null, null, null);

	@Test
	void deveListarEncomendasComFiltros() {
		Pagina<Encomenda> pagina = new Pagina<>(List.of(encomenda), 0, 20, 1);
		when(encomendaGateway.listar(StatusEncomenda.RECEBIDA, "101", 0, 20)).thenReturn(pagina);

		Pagina<Encomenda> resultado = new ListarEncomendasUseCase(encomendaGateway)
				.executar(StatusEncomenda.RECEBIDA, " 101 ", 0, 20);

		assertThat(resultado).isSameAs(pagina);
		assertThat(resultado.totalPaginas()).isEqualTo(1);
	}

	@Test
	void deveListarSemFiltros() {
		Pagina<Encomenda> pagina = new Pagina<>(List.of(), 1, 10, 15);
		when(encomendaGateway.listar(null, null, 1, 10)).thenReturn(pagina);

		Pagina<Encomenda> resultado = new ListarEncomendasUseCase(encomendaGateway).executar(null, " ", 1, 10);

		assertThat(resultado.totalPaginas()).isEqualTo(2);
	}

	@ParameterizedTest
	@CsvSource({"-1, 20", "0, 0", "0, 101"})
	void naoDeveAceitarPaginacaoInvalida(int pagina, int tamanho) {
		assertThatThrownBy(() -> new ListarEncomendasUseCase(encomendaGateway).executar(null, null, pagina, tamanho))
				.isInstanceOf(DadosInvalidosException.class);
		verifyNoInteractions(encomendaGateway);
	}

	@Test
	void deveBuscarEncomendaPorId() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.of(encomenda));

		assertThat(new BuscarEncomendaUseCase(encomendaGateway).executar(30L)).isSameAs(encomenda);
	}

	@Test
	void deveFalharAoBuscarEncomendaInexistente() {
		when(encomendaGateway.buscarPorId(30L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> new BuscarEncomendaUseCase(encomendaGateway).executar(30L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}

	@Test
	void deveListarEncomendasDoMorador() {
		when(encomendaGateway.listarPorMorador(1L)).thenReturn(List.of(encomenda));

		assertThat(new ListarEncomendasDoMoradorUseCase(encomendaGateway).executar(1L)).containsExactly(encomenda);
	}
}
