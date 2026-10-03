package com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception;

import java.io.Serial;

public class DadosInvalidosException extends DomainException {

	@Serial
	private static final long serialVersionUID = 1L;

	public DadosInvalidosException(String mensagem) {
		super(mensagem);
	}
}
