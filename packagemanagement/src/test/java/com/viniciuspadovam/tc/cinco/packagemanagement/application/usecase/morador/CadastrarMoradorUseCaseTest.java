package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

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
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CadastrarMoradorUseCaseTest {

	@Mock
	private MoradorGateway moradorGateway;

	@Mock
	private FuncionarioGateway funcionarioGateway;

	@Mock
	private SenhaEncoder senhaEncoder;

	@InjectMocks
	private CadastrarMoradorUseCase useCase;

	private final CadastrarMoradorCommand command =
			new CadastrarMoradorCommand("Ana Souza", "Ana@Email.com", "Senha@123", "11988887777", "101");

	@Test
	void deveCadastrarMoradorComSenhaCodificada() {
		when(senhaEncoder.codificar("Senha@123")).thenReturn("hash");
		when(moradorGateway.salvar(any())).thenAnswer(invocacao -> comId(invocacao.getArgument(0), 1L));

		Morador morador = useCase.executar(command);

		ArgumentCaptor<Morador> captor = ArgumentCaptor.forClass(Morador.class);
		verify(moradorGateway).salvar(captor.capture());
		assertThat(captor.getValue().getSenhaHash()).isEqualTo("hash");
		assertThat(captor.getValue().getEmail()).isEqualTo("ana@email.com");
		assertThat(morador.getId()).isEqualTo(1L);
	}

	@Test
	void naoDeveCadastrarComEmailJaUsadoPorMorador() {
		when(moradorGateway.existePorEmail("ana@email.com")).thenReturn(true);

		assertThatThrownBy(() -> useCase.executar(command))
				.isInstanceOf(ConflitoException.class)
				.hasMessageContaining("e-mail");
		verify(moradorGateway, never()).salvar(any());
	}

	@Test
	void naoDeveCadastrarComEmailJaUsadoPorFuncionario() {
		when(funcionarioGateway.existePorEmail("ana@email.com")).thenReturn(true);

		assertThatThrownBy(() -> useCase.executar(command))
				.isInstanceOf(ConflitoException.class);
		verify(moradorGateway, never()).salvar(any());
	}

	@Test
	void naoDeveCadastrarMoradorComMesmoNomeNoMesmoApartamento() {
		Morador existente = Morador.restaurar(9L, "ANA  souza", "outra@email.com", "hash", "11977776666", "101");
		when(moradorGateway.buscarPorApartamentoENome("101", "ana souza")).thenReturn(Optional.of(existente));

		assertThatThrownBy(() -> useCase.executar(command))
				.isInstanceOf(ConflitoException.class)
				.hasMessageContaining("apartamento");
		verify(moradorGateway, never()).salvar(any());
	}

	@Test
	void naoDeveCadastrarComSenhaFraca() {
		CadastrarMoradorCommand senhaCurta =
				new CadastrarMoradorCommand("Ana Souza", "ana@email.com", "123", "11988887777", "101");

		assertThatThrownBy(() -> useCase.executar(senhaCurta))
				.isInstanceOf(DadosInvalidosException.class);
		verify(moradorGateway, never()).salvar(any());
	}

	static Morador comId(Morador morador, Long id) {
		return Morador.restaurar(id, morador.getNome(), morador.getEmail(), morador.getSenhaHash(),
				morador.getTelefone(), morador.getApartamento());
	}
}
