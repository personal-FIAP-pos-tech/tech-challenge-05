package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarFuncionarioUseCaseTest {

	@Mock
	private FuncionarioGateway funcionarioGateway;

	@Mock
	private MoradorGateway moradorGateway;

	@Mock
	private SenhaEncoder senhaEncoder;

	@InjectMocks
	private CadastrarFuncionarioUseCase useCase;

	private final CadastrarFuncionarioCommand command =
			new CadastrarFuncionarioCommand("Carlos Lima", "Carlos@Portaria.com", "Senha@123");

	@Test
	void deveCadastrarPorteiroComSenhaCodificada() {
		when(senhaEncoder.codificar("Senha@123")).thenReturn("hash");
		when(funcionarioGateway.salvar(any())).thenAnswer(invocacao -> {
			Funcionario f = invocacao.getArgument(0);
			return Funcionario.restaurar(5L, f.getNome(), f.getEmail(), f.getSenhaHash());
		});

		Funcionario funcionario = useCase.executar(command);

		assertThat(funcionario.getId()).isEqualTo(5L);
		assertThat(funcionario.getEmail()).isEqualTo("carlos@portaria.com");
		assertThat(funcionario.getSenhaHash()).isEqualTo("hash");
		assertThat(funcionario.getPerfil()).isEqualTo(Perfil.PORTEIRO);
	}

	@Test
	void naoDeveCadastrarComEmailJaUsadoPorFuncionario() {
		when(funcionarioGateway.existePorEmail("carlos@portaria.com")).thenReturn(true);

		assertThatThrownBy(() -> useCase.executar(command))
				.isInstanceOf(ConflitoException.class);
		verify(funcionarioGateway, never()).salvar(any());
	}

	@Test
	void naoDeveCadastrarComEmailJaUsadoPorMorador() {
		when(moradorGateway.existePorEmail("carlos@portaria.com")).thenReturn(true);

		assertThatThrownBy(() -> useCase.executar(command))
				.isInstanceOf(ConflitoException.class);
		verify(funcionarioGateway, never()).salvar(any());
	}

	@Test
	void naoDeveCadastrarComSenhaFraca() {
		assertThatThrownBy(() -> useCase.executar(
				new CadastrarFuncionarioCommand("Carlos", "carlos@portaria.com", "curta")))
				.isInstanceOf(DadosInvalidosException.class);
	}
}
