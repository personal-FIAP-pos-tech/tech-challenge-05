package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.CadastrarFuncionarioCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CadastrarFuncionarioRequest(
		@NotBlank @Size(max = 120) String nome,
		@NotBlank @Email String email,
		@NotBlank @Size(min = 8, max = 72) String senha) {

	public CadastrarFuncionarioCommand paraCommand() {
		return new CadastrarFuncionarioCommand(nome, email, senha);
	}
}
