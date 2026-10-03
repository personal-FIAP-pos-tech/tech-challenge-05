package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario.Funcionario;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;

public record FuncionarioResponse(Long id, String nome, String email, Perfil perfil) {

	public static FuncionarioResponse de(Funcionario funcionario) {
		return new FuncionarioResponse(funcionario.getId(), funcionario.getNome(), funcionario.getEmail(),
				funcionario.getPerfil());
	}
}
