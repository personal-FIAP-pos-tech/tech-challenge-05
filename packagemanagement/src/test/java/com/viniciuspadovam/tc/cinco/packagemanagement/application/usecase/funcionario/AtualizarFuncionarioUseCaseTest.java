package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtualizarFuncionarioUseCaseTest {

	@Mock
	private FuncionarioGateway funcionarioGateway;

	@Mock
	private SenhaEncoder senhaEncoder;

	@InjectMocks
	private AtualizarFuncionarioUseCase useCase;

	private Funcionario funcionarioAtual() {
		return Funcionario.restaurar(5L, "Carlos", "carlos@portaria.com", "hash-antigo");
	}

	@Test
	void deveAtualizarNomeSemTrocarSenha() {
		when(funcionarioGateway.buscarPorId(5L)).thenReturn(Optional.of(funcionarioAtual()));
		when(funcionarioGateway.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

		Funcionario funcionario = useCase.executar(5L, new AtualizarFuncionarioCommand("Carlos Eduardo", null));

		assertThat(funcionario.getNome()).isEqualTo("Carlos Eduardo");
		assertThat(funcionario.getSenhaHash()).isEqualTo("hash-antigo");
		verifyNoInteractions(senhaEncoder);
	}

	@Test
	void deveTrocarSenhaQuandoInformada() {
		when(funcionarioGateway.buscarPorId(5L)).thenReturn(Optional.of(funcionarioAtual()));
		when(funcionarioGateway.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
		when(senhaEncoder.codificar("NovaSenha@1")).thenReturn("hash-novo");

		Funcionario funcionario = useCase.executar(5L, new AtualizarFuncionarioCommand("Carlos", "NovaSenha@1"));

		assertThat(funcionario.getSenhaHash()).isEqualTo("hash-novo");
	}

	@Test
	void deveFalharQuandoFuncionarioNaoExiste() {
		when(funcionarioGateway.buscarPorId(5L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(5L, new AtualizarFuncionarioCommand("Carlos", null)))
				.isInstanceOf(RecursoNaoEncontradoException.class)
				.hasMessageContaining("Funcionário");
	}
}
