package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.AtualizarMoradorCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarMoradorRequest(
		@NotBlank @Size(max = 120) String nome,
		@NotBlank String telefone,
		@NotBlank @Size(max = 10) String apartamento,
		@Size(min = 8, max = 72) String novaSenha) {

	public AtualizarMoradorCommand paraCommand() {
		return new AtualizarMoradorCommand(nome, telefone, apartamento, novaSenha);
	}
}
