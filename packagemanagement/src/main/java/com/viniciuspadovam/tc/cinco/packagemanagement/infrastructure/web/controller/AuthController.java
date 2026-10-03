package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.AutenticarUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.LoginRequest;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Autenticação", description = "Login de moradores e funcionários com geração de token JWT")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AutenticarUseCase autenticarUseCase;

	@Operation(summary = "Faz login com e-mail e senha e devolve o token JWT")
	@SecurityRequirements
	@PostMapping("/login")
	public TokenResponse login(@Valid @RequestBody LoginRequest request) {
		return TokenResponse.de(autenticarUseCase.executar(request.email(), request.senha()));
	}
}
