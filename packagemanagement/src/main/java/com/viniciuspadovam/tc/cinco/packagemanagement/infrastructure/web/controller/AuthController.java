package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.AutenticarUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.LoginRequest;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AutenticarUseCase autenticarUseCase;

	@PostMapping("/login")
	public TokenResponse login(@Valid @RequestBody LoginRequest request) {
		return TokenResponse.de(autenticarUseCase.executar(request.email(), request.senha()));
	}
}
