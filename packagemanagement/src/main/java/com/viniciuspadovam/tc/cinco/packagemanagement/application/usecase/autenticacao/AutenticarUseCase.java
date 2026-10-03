package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.CredenciaisInvalidasException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.TokenProvider;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AutenticarUseCase {

	private final MoradorGateway moradorGateway;
	private final FuncionarioGateway funcionarioGateway;
	private final SenhaEncoder senhaEncoder;
	private final TokenProvider tokenProvider;

	public TokenGerado executar(String email, String senha) {
		if (email == null || email.isBlank() || senha == null || senha.isEmpty()) {
			throw new CredenciaisInvalidasException();
		}
		String emailNormalizado = email.strip().toLowerCase(Locale.ROOT);

		UsuarioAutenticado usuario = buscarCredenciais(emailNormalizado)
				.filter(credenciais -> senhaEncoder.confere(senha, credenciais.senhaHash()))
				.map(Credenciais::usuario)
				.orElseThrow(CredenciaisInvalidasException::new);

		return tokenProvider.gerar(usuario);
	}

	private Optional<Credenciais> buscarCredenciais(String email) {
		return moradorGateway.buscarPorEmail(email)
				.map(morador -> new Credenciais(
						new UsuarioAutenticado(morador.getId(), morador.getEmail(), Perfil.MORADOR),
						morador.getSenhaHash()))
				.or(() -> funcionarioGateway.buscarPorEmail(email)
						.map(funcionario -> new Credenciais(
								new UsuarioAutenticado(funcionario.getId(), funcionario.getEmail(),
										funcionario.getPerfil()),
								funcionario.getSenhaHash())));
	}

	private record Credenciais(UsuarioAutenticado usuario, String senhaHash) {
	}
}
