package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import java.util.List;
import java.util.function.Function;

public record PaginaResponse<T>(List<T> conteudo, int pagina, int tamanho, long totalElementos, int totalPaginas) {

	public PaginaResponse {
		conteudo = List.copyOf(conteudo);
	}

	public static <D, T> PaginaResponse<T> de(Pagina<D> pagina, Function<D, T> conversor) {
		return new PaginaResponse<>(pagina.conteudo().stream().map(conversor).toList(), pagina.pagina(),
				pagina.tamanho(), pagina.totalElementos(), pagina.totalPaginas());
	}
}
