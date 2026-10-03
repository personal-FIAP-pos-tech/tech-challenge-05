package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarMoradorUseCaseTest {

	@Mock
	private MoradorGateway moradorGateway;

	@InjectMocks
	private BuscarMoradorUseCase useCase;

	@Test
	void deveRetornarMorador() {
		Morador morador = Morador.restaurar(1L, "Ana", "ana@email.com", "hash", "11988887777", "101");
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.of(morador));

		assertThat(useCase.executar(1L)).isSameAs(morador);
	}

	@Test
	void deveFalharQuandoNaoExiste() {
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(1L))
				.isInstanceOf(RecursoNaoEncontradoException.class)
				.hasMessageContaining("Morador");
	}
}
