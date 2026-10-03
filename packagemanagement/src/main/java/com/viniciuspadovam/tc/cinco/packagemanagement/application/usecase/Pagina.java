package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase;

import java.util.List;

public record Pagina<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos) {

	public Pagina {
		conteudo = List.copyOf(conteudo);
	}

	public int totalPaginas() {
		return tamanho == 0 ? 0 : (int) Math.ceil((double) totalElementos / tamanho);
	}
}
