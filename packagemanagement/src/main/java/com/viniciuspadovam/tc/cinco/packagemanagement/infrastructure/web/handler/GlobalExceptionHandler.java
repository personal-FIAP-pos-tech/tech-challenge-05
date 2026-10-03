package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.handler;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.ConflitoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.CredenciaisInvalidasException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.DadosInvalidosException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(DadosInvalidosException.class)
	public ProblemDetail dadosInvalidos(DadosInvalidosException ex) {
		return problema(HttpStatus.BAD_REQUEST, "Dados inválidos", ex.getMessage());
	}

	@ExceptionHandler(RegraNegocioException.class)
	public ProblemDetail regraNegocio(RegraNegocioException ex) {
		return problema(HttpStatus.UNPROCESSABLE_CONTENT, "Regra de negócio violada", ex.getMessage());
	}

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ProblemDetail naoEncontrado(RecursoNaoEncontradoException ex) {
		return problema(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage());
	}

	@ExceptionHandler(ConflitoException.class)
	public ProblemDetail conflito(ConflitoException ex) {
		return problema(HttpStatus.CONFLICT, "Conflito", ex.getMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ProblemDetail integridade(DataIntegrityViolationException ex) {
		return problema(HttpStatus.CONFLICT, "Conflito", "Os dados informados conflitam com um registro existente.");
	}

	@ExceptionHandler(CredenciaisInvalidasException.class)
	public ProblemDetail credenciaisInvalidas(CredenciaisInvalidasException ex) {
		return problema(HttpStatus.UNAUTHORIZED, "Não autorizado", ex.getMessage());
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Map<String, String> erros = new LinkedHashMap<>();
		for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
			erros.putIfAbsent(erro.getField(), erro.getDefaultMessage());
		}
		ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "Dados inválidos",
				"Um ou mais campos estão inválidos.");
		problema.setProperty("erros", erros);
		return handleExceptionInternal(ex, problema, headers, status, request);
	}

	private static ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(status, detalhe);
		problema.setTitle(titulo);
		return problema;
	}
}
