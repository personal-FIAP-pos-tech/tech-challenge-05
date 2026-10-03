package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SecurityConfig {

	public static final String CLAIM_PERFIS = "roles";

	private static final String[] ROTAS_PUBLICAS = {
		"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/error"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
				.sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(rotas -> rotas
						.requestMatchers(HttpMethod.POST, "/auth/login", "/moradores", "/funcionarios").permitAll()
						.requestMatchers(ROTAS_PUBLICAS).permitAll()
						.requestMatchers("/moradores/me/**").hasRole("MORADOR")
						.requestMatchers("/funcionarios/me/**").hasRole("PORTEIRO")
						.requestMatchers("/encomendas/**").hasRole("PORTEIRO")
						.requestMatchers("/notificacoes/**").hasRole("MORADOR")
						.anyRequest().authenticated())
				.oauth2ResourceServer(servidor -> servidor
						.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
		return http.build();
	}

	@Bean
	public JwtEncoder jwtEncoder(JwtProperties propriedades) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(chave(propriedades)));
	}

	@Bean
	public JwtDecoder jwtDecoder(JwtProperties propriedades) {
		return NimbusJwtDecoder.withSecretKey(chave(propriedades)).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter perfis = new JwtGrantedAuthoritiesConverter();
		perfis.setAuthoritiesClaimName(CLAIM_PERFIS);
		perfis.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
		conversor.setJwtGrantedAuthoritiesConverter(perfis);
		return conversor;
	}

	private static SecretKey chave(JwtProperties propriedades) {
		return new SecretKeySpec(propriedades.secret().getBytes(StandardCharsets.UTF_8), "HmacSHA256");
	}
}
