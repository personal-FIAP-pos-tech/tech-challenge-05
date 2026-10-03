package com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception;

import java.io.Serial;

public class RegraNegocioException extends DomainException {

	@Serial
	private static final long serialVersionUID = 1L;

	public RegraNegocioException(String mensagem) {
		super(mensagem);
	}
}
