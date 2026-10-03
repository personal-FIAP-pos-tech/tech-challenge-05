package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.AtualizarFuncionarioCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AtualizarFuncionarioRequest(
		@NotBlank @Size(max = 120) String nome,
		@Size(min = 8, max = 72) String novaSenha) {

	public AtualizarFuncionarioCommand paraCommand() {
		return new AtualizarFuncionarioCommand(nome, novaSenha);
	}
}
