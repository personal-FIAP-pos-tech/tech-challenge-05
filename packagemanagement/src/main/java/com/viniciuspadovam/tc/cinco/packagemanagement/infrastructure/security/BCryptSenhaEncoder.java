package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BCryptSenhaEncoder implements SenhaEncoder {

	private final PasswordEncoder passwordEncoder;

	@Override
	public String codificar(String senha) {
		return passwordEncoder.encode(senha);
	}

	@Override
	public boolean confere(String senha, String senhaHash) {
		return passwordEncoder.matches(senha, senhaHash);
	}
}
