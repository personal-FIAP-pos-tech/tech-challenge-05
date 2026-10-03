package com.viniciuspadovam.tc.cinco.packagemanagement.application.exception;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DomainException;
import java.io.Serial;

public class CredenciaisInvalidasException extends DomainException {

	@Serial
	private static final long serialVersionUID = 1L;

	public CredenciaisInvalidasException() {
		super("E-mail ou senha inválidos.");
	}
}
