package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarFuncionarioUseCaseTest {

	@Mock
	private FuncionarioGateway funcionarioGateway;

	@InjectMocks
	private BuscarFuncionarioUseCase useCase;

	@Test
	void deveRetornarFuncionario() {
		Funcionario funcionario = Funcionario.restaurar(5L, "Carlos", "carlos@portaria.com", "hash");
		when(funcionarioGateway.buscarPorId(5L)).thenReturn(Optional.of(funcionario));

		assertThat(useCase.executar(5L)).isSameAs(funcionario);
	}

	@Test
	void deveFalharQuandoNaoExiste() {
		when(funcionarioGateway.buscarPorId(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(5L))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}
}
