package com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.NomeNormalizado;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import lombok.Getter;

@Getter
public final class Morador {

	private static final int TAMANHO_MAXIMO_NOME = 120;

	private final Long id;
	private final String email;
	private String nome;
	private String senhaHash;
	private String telefone;
	private String apartamento;

	private Morador(Long id, String nome, String email, String senhaHash, String telefone, String apartamento) {
		this.id = id;
		this.nome = Validacoes.obrigatorio(nome, "nome", TAMANHO_MAXIMO_NOME);
		this.email = Validacoes.email(email);
		this.senhaHash = Validacoes.obrigatorio(senhaHash, "senha");
		this.telefone = Validacoes.telefone(telefone);
		this.apartamento = Validacoes.apartamento(apartamento);
	}

	public static Morador novo(String nome, String email, String senhaHash, String telefone, String apartamento) {
		return new Morador(null, nome, email, senhaHash, telefone, apartamento);
	}

	public static Morador restaurar(Long id, String nome, String email, String senhaHash, String telefone,
			String apartamento) {
		return new Morador(id, nome, email, senhaHash, telefone, apartamento);
	}

	public void atualizarDados(String novoNome, String novoTelefone, String novoApartamento) {
		String nomeValidado = Validacoes.obrigatorio(novoNome, "nome", TAMANHO_MAXIMO_NOME);
		String telefoneValidado = Validacoes.telefone(novoTelefone);
		String apartamentoValidado = Validacoes.apartamento(novoApartamento);
		this.nome = nomeValidado;
		this.telefone = telefoneValidado;
		this.apartamento = apartamentoValidado;
	}

	public void alterarSenha(String novaSenhaHash) {
		this.senhaHash = Validacoes.obrigatorio(novaSenhaHash, "senha");
	}

	public String getNomeNormalizado() {
		return NomeNormalizado.de(nome).valor();
	}
}
