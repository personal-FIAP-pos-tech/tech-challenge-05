package com.viniciuspadovam.tc.cinco.packagemanagement.domain.funcionario;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;
import lombok.Getter;

@Getter
public final class Funcionario {

	private static final int TAMANHO_MAXIMO_NOME = 120;

	private final Long id;
	private final String email;
	private final Perfil perfil = Perfil.PORTEIRO;
	private String nome;
	private String senhaHash;

	private Funcionario(Long id, String nome, String email, String senhaHash) {
		this.id = id;
		this.nome = Validacoes.obrigatorio(nome, "nome", TAMANHO_MAXIMO_NOME);
		this.email = Validacoes.email(email);
		this.senhaHash = Validacoes.obrigatorio(senhaHash, "senha");
	}

	public static Funcionario novo(String nome, String email, String senhaHash) {
		return new Funcionario(null, nome, email, senhaHash);
	}

	public static Funcionario restaurar(Long id, String nome, String email, String senhaHash) {
		return new Funcionario(id, nome, email, senhaHash);
	}

	public void atualizarNome(String novoNome) {
		this.nome = Validacoes.obrigatorio(novoNome, "nome", TAMANHO_MAXIMO_NOME);
	}

	public void alterarSenha(String novaSenhaHash) {
		this.senhaHash = Validacoes.obrigatorio(novaSenhaHash, "senha");
	}
}
