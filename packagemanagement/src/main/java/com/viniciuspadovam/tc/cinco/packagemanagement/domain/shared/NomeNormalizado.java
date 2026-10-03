package com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public record NomeNormalizado(String valor) {

	private static final Pattern ACENTOS = Pattern.compile("\\p{M}");
	private static final Pattern ESPACOS = Pattern.compile("\\s+");

	public static NomeNormalizado de(String nome) {
		String texto = Validacoes.obrigatorio(nome, "nome");
		String semAcentos = ACENTOS.matcher(Normalizer.normalize(texto, Normalizer.Form.NFD)).replaceAll("");
		return new NomeNormalizado(ESPACOS.matcher(semAcentos).replaceAll(" ").toLowerCase(Locale.ROOT));
	}
}
