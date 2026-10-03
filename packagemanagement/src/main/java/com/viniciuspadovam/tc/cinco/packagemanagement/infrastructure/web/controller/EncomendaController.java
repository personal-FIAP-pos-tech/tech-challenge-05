package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.EncomendaResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.RegistrarEncomendaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/encomendas")
@RequiredArgsConstructor
public class EncomendaController {

	private final RegistrarEncomendaUseCase registrarEncomendaUseCase;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EncomendaResponse registrar(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody RegistrarEncomendaRequest request) {
		return EncomendaResponse.de(registrarEncomendaUseCase.executar(UsuarioLogado.id(jwt), request.paraCommand()));
	}
}
