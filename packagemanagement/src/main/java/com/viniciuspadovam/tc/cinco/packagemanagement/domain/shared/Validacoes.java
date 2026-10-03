package com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Validacoes {

	private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern TELEFONE = Pattern.compile("^\\d{10,11}$");
	private static final Pattern APARTAMENTO = Pattern.compile("^[A-Za-z0-9-]{1,10}$");
	private static final Pattern MASCARA_TELEFONE = Pattern.compile("[\\s()\\-+.]");
	private static final int SENHA_TAMANHO_MINIMO = 8;
	private static final int SENHA_TAMANHO_MAXIMO = 72;

	private Validacoes() {
	}

	public static String obrigatorio(String valor, String campo) {
		if (valor == null || valor.isBlank()) {
			throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
		}
		return valor.strip();
	}

	public static String obrigatorio(String valor, String campo, int tamanhoMaximo) {
		String texto = obrigatorio(valor, campo);
		if (texto.length() > tamanhoMaximo) {
			throw new DadosInvalidosException(
					"O campo " + campo + " deve ter no máximo " + tamanhoMaximo + " caracteres.");
		}
		return texto;
	}

	public static Long idObrigatorio(Long id, String campo) {
		if (id == null) {
			throw new DadosInvalidosException("O campo " + campo + " é obrigatório.");
		}
		return id;
	}

	public static String email(String email) {
		String valor = email == null ? "" : email.strip().toLowerCase(Locale.ROOT);
		if (!EMAIL.matcher(valor).matches()) {
			throw new DadosInvalidosException("O e-mail informado é inválido.");
		}
		return valor;
	}

	public static String senha(String senha) {
		if (senha == null || senha.isBlank()
				|| senha.length() < SENHA_TAMANHO_MINIMO || senha.length() > SENHA_TAMANHO_MAXIMO) {
			throw new DadosInvalidosException("A senha deve ter entre " + SENHA_TAMANHO_MINIMO + " e "
					+ SENHA_TAMANHO_MAXIMO + " caracteres.");
		}
		return senha;
	}

	public static String telefone(String telefone) {
		String valor = telefone == null ? "" : MASCARA_TELEFONE.matcher(telefone).replaceAll("");
		if (!TELEFONE.matcher(valor).matches()) {
			throw new DadosInvalidosException("O telefone deve conter DDD e número, com 10 ou 11 dígitos.");
		}
		return valor;
	}

	public static String apartamento(String apartamento) {
		String valor = apartamento == null ? "" : apartamento.strip();
		if (!APARTAMENTO.matcher(valor).matches()) {
			throw new DadosInvalidosException(
					"O apartamento deve ter até 10 caracteres, apenas letras, números ou hífen.");
		}
		return valor;
	}
}
