package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.CadastrarMoradorCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastrarMoradorRequest(
		@NotBlank @Size(max = 120) String nome,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 8, max = 72) String senha,
		@NotBlank String telefone,
		@NotBlank @Size(max = 10) String apartamento) {

	public CadastrarMoradorCommand paraCommand() {
		return new CadastrarMoradorCommand(nome, email, senha, telefone, apartamento);
	}
}
