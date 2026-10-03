package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ConfirmarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.NotificacaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notificações", description = "Confirmação de recebimento das notificações pelo morador")
@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

	private final ConfirmarNotificacaoUseCase confirmarNotificacaoUseCase;

	@Operation(summary = "Confirma o recebimento de uma notificação do morador logado")
	@PatchMapping("/{id}/confirmacao")
	public NotificacaoResponse confirmar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		return NotificacaoResponse.de(confirmarNotificacaoUseCase.executar(UsuarioLogado.id(jwt), id));
	}
}
