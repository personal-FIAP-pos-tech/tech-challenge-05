package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.CredenciaisInvalidasException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.TokenProvider;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AutenticarUseCaseTest {

	private static final TokenGerado TOKEN = new TokenGerado("jwt", 3600);

	@Mock
	private MoradorGateway moradorGateway;

	@Mock
	private FuncionarioGateway funcionarioGateway;

	@Mock
	private SenhaEncoder senhaEncoder;

	@Mock
	private TokenProvider tokenProvider;

	@InjectMocks
	private AutenticarUseCase useCase;

	@Test
	void deveAutenticarMorador() {
		Morador morador = Morador.restaurar(1L, "Ana", "ana@email.com", "hash", "11988887777", "101");
		when(moradorGateway.buscarPorEmail("ana@email.com")).thenReturn(Optional.of(morador));
		when(senhaEncoder.confere("Senha@123", "hash")).thenReturn(true);
		when(tokenProvider.gerar(new UsuarioAutenticado(1L, "ana@email.com", Perfil.MORADOR))).thenReturn(TOKEN);

		TokenGerado token = useCase.executar(" Ana@Email.com ", "Senha@123");

		assertThat(token).isEqualTo(TOKEN);
	}

	@Test
	void deveAutenticarPorteiro() {
		Funcionario funcionario = Funcionario.restaurar(5L, "Carlos", "carlos@portaria.com", "hash");
		when(moradorGateway.buscarPorEmail("carlos@portaria.com")).thenReturn(Optional.empty());
		when(funcionarioGateway.buscarPorEmail("carlos@portaria.com")).thenReturn(Optional.of(funcionario));
		when(senhaEncoder.confere("Senha@123", "hash")).thenReturn(true);
		when(tokenProvider.gerar(new UsuarioAutenticado(5L, "carlos@portaria.com", Perfil.PORTEIRO)))
				.thenReturn(TOKEN);

		TokenGerado token = useCase.executar("carlos@portaria.com", "Senha@123");

		assertThat(token).isEqualTo(TOKEN);
	}

	@Test
	void naoDeveAutenticarComSenhaErrada() {
		Morador morador = Morador.restaurar(1L, "Ana", "ana@email.com", "hash", "11988887777", "101");
		when(moradorGateway.buscarPorEmail("ana@email.com")).thenReturn(Optional.of(morador));
		when(senhaEncoder.confere("errada", "hash")).thenReturn(false);

		assertThatThrownBy(() -> useCase.executar("ana@email.com", "errada"))
				.isInstanceOf(CredenciaisInvalidasException.class)
				.hasMessage("E-mail ou senha inválidos.");
		verify(tokenProvider, never()).gerar(any());
	}

	@Test
	void naoDeveAutenticarUsuarioInexistente() {
		when(moradorGateway.buscarPorEmail("ninguem@email.com")).thenReturn(Optional.empty());
		when(funcionarioGateway.buscarPorEmail("ninguem@email.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar("ninguem@email.com", "Senha@123"))
				.isInstanceOf(CredenciaisInvalidasException.class)
				.hasMessage("E-mail ou senha inválidos.");
	}

	@Test
	void naoDeveAutenticarSemEmailOuSenha() {
		assertThatThrownBy(() -> useCase.executar(null, "Senha@123"))
				.isInstanceOf(CredenciaisInvalidasException.class);
		assertThatThrownBy(() -> useCase.executar("ana@email.com", null))
				.isInstanceOf(CredenciaisInvalidasException.class);
	}
}
