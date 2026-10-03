package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.TokenGerado;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.UsuarioAutenticado;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

class JwtTokenProviderTest {

	private static final JwtProperties PROPRIEDADES =
			new JwtProperties("chave-secreta-de-teste-com-pelo-menos-32-bytes", Duration.ofMinutes(30));

	private final Instant agora = Instant.now().truncatedTo(ChronoUnit.SECONDS);
	private final SecurityConfig securityConfig = new SecurityConfig();
	private final JwtDecoder decoder = securityConfig.jwtDecoder(PROPRIEDADES);
	private final JwtTokenProvider provider = new JwtTokenProvider(
			securityConfig.jwtEncoder(PROPRIEDADES), PROPRIEDADES, Clock.fixed(agora, ZoneOffset.UTC));

	@Test
	void deveGerarTokenComIdentificadorEPerfilDoUsuario() {
		TokenGerado token = provider.gerar(new UsuarioAutenticado(1L, "ana@email.com", Perfil.MORADOR));

		Jwt jwt = decoder.decode(token.accessToken());
		assertThat(jwt.getSubject()).isEqualTo("1");
		assertThat(jwt.getClaimAsString("email")).isEqualTo("ana@email.com");
		assertThat(jwt.getClaimAsStringList("roles")).containsExactly("MORADOR");
		assertThat(jwt.getIssuedAt()).isEqualTo(agora);
		assertThat(jwt.getExpiresAt()).isEqualTo(agora.plus(Duration.ofMinutes(30)));
		assertThat(token.expiraEmSegundos()).isEqualTo(1800);
	}

	@Test
	void deveGerarTokenDePorteiro() {
		TokenGerado token = provider.gerar(new UsuarioAutenticado(5L, "carlos@portaria.com", Perfil.PORTEIRO));

		assertThat(decoder.decode(token.accessToken()).getClaimAsStringList("roles")).containsExactly("PORTEIRO");
	}
}
