package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ConfirmarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.NotificacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

	private final ConfirmarNotificacaoUseCase confirmarNotificacaoUseCase;

	@PatchMapping("/{id}/confirmacao")
	public NotificacaoResponse confirmar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		return NotificacaoResponse.de(confirmarNotificacaoUseCase.executar(UsuarioLogado.id(jwt), id));
	}
}
