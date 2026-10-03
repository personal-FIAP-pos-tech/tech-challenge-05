package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.TokenProvider;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.TokenGerado;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.UsuarioAutenticado;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements TokenProvider {

	private static final String EMISSOR = "packagemanagement";

	private final JwtEncoder jwtEncoder;
	private final JwtProperties propriedades;
	private final Clock clock;

	@Override
	public TokenGerado gerar(UsuarioAutenticado usuario) {
		Instant agora = clock.instant();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(EMISSOR)
				.subject(String.valueOf(usuario.id()))
				.issuedAt(agora)
				.expiresAt(agora.plus(propriedades.expiracao()))
				.claim("email", usuario.email())
				.claim(SecurityConfig.CLAIM_PERFIS, List.of(usuario.perfil().name()))
				.build();
		JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(cabecalho, claims)).getTokenValue();
		return new TokenGerado(token, propriedades.expiracao().toSeconds());
	}
}
