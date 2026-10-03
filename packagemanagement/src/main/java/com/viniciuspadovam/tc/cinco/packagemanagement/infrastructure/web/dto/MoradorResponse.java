package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;

public record MoradorResponse(Long id, String nome, String email, String telefone, String apartamento) {

	public static MoradorResponse de(Morador morador) {
		return new MoradorResponse(morador.getId(), morador.getNome(), morador.getEmail(), morador.getTelefone(),
				morador.getApartamento());
	}
}
