package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AtualizarMoradorUseCaseTest {

	@Mock
	private MoradorGateway moradorGateway;

	@Mock
	private SenhaEncoder senhaEncoder;

	@InjectMocks
	private AtualizarMoradorUseCase useCase;

	private Morador moradorAtual() {
		return Morador.restaurar(1L, "Ana Souza", "ana@email.com", "hash-antigo", "11988887777", "101");
	}

	@Test
	void deveAtualizarDadosSemTrocarSenha() {
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.of(moradorAtual()));
		when(moradorGateway.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

		Morador morador = useCase.executar(1L, new AtualizarMoradorCommand("Ana Maria", "1122223333", "202", null));

		assertThat(morador.getNome()).isEqualTo("Ana Maria");
		assertThat(morador.getTelefone()).isEqualTo("1122223333");
		assertThat(morador.getApartamento()).isEqualTo("202");
		assertThat(morador.getSenhaHash()).isEqualTo("hash-antigo");
		verifyNoInteractions(senhaEncoder);
	}

	@Test
	void deveTrocarSenhaQuandoInformada() {
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.of(moradorAtual()));
		when(moradorGateway.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
		when(senhaEncoder.codificar("NovaSenha@1")).thenReturn("hash-novo");

		Morador morador = useCase.executar(1L,
				new AtualizarMoradorCommand("Ana Souza", "11988887777", "101", "NovaSenha@1"));

		assertThat(morador.getSenhaHash()).isEqualTo("hash-novo");
	}

	@Test
	void devePermitirManterOProprioNomeEApartamento() {
		Morador atual = moradorAtual();
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.of(atual));
		when(moradorGateway.buscarPorApartamentoENome("101", "ana souza")).thenReturn(Optional.of(atual));
		when(moradorGateway.salvar(any())).thenAnswer(invocacao -> invocacao.getArgument(0));

		Morador morador = useCase.executar(1L, new AtualizarMoradorCommand("Ana Souza", "1133334444", "101", ""));

		assertThat(morador.getTelefone()).isEqualTo("1133334444");
	}

	@Test
	void naoDevePermitirNomeEApartamentoDeOutroMorador() {
		Morador outro = Morador.restaurar(2L, "Bruno Lima", "bruno@email.com", "hash", "11977776666", "202");
		when(moradorGateway.buscarPorId(1L)).thenReturn(Optional.of(moradorAtual()));
		when(moradorGateway.buscarPorApartamentoENome("202", "bruno lima")).thenReturn(Optional.of(outro));

		assertThatThrownBy(() -> useCase.executar(1L,
				new AtualizarMoradorCommand("Bruno Lima", "11988887777", "202", null)))
				.isInstanceOf(ConflitoException.class);
		verify(moradorGateway, never()).salvar(any());
	}

	@Test
	void deveFalharQuandoMoradorNaoExiste() {
		when(moradorGateway.buscarPorId(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> useCase.executar(99L,
				new AtualizarMoradorCommand("Ana", "11988887777", "101", null)))
				.isInstanceOf(RecursoNaoEncontradoException.class);
	}
}
