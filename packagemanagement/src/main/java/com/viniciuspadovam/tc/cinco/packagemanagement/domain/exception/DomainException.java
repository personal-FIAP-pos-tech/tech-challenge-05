package com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception;

import java.io.Serial;

public abstract class DomainException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 1L;

	protected DomainException(String mensagem) {
		super(mensagem);
	}
}
