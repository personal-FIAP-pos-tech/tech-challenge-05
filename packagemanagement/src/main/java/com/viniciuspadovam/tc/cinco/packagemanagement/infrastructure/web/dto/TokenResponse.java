package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.TokenGerado;

public record TokenResponse(String accessToken, String tokenType, long expiresIn) {

	public static TokenResponse de(TokenGerado token) {
		return new TokenResponse(token.accessToken(), "Bearer", token.expiraEmSegundos());
	}
}
