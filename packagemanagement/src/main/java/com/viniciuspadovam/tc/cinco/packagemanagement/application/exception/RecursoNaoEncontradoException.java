package com.viniciuspadovam.tc.cinco.packagemanagement.application.exception;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DomainException;
import java.io.Serial;

public class RecursoNaoEncontradoException extends DomainException {

	@Serial
	private static final long serialVersionUID = 1L;

	public RecursoNaoEncontradoException(String recurso, Long id) {
		super(recurso + " não encontrado(a) com id " + id + ".");
	}
}
