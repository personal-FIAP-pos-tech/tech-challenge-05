package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.AtualizarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.BuscarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.CadastrarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.AtualizarFuncionarioRequest;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.CadastrarFuncionarioRequest;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.FuncionarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Funcionários", description = "Cadastro e dados do funcionário (porteiro) logado")
@RestController
@RequestMapping("/funcionarios")
@RequiredArgsConstructor
public class FuncionarioController {

	private final CadastrarFuncionarioUseCase cadastrarFuncionarioUseCase;
	private final AtualizarFuncionarioUseCase atualizarFuncionarioUseCase;
	private final BuscarFuncionarioUseCase buscarFuncionarioUseCase;

	@Operation(summary = "Cadastra um funcionário com perfil PORTEIRO (não exige login)")
	@SecurityRequirements
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FuncionarioResponse cadastrar(@Valid @RequestBody CadastrarFuncionarioRequest request) {
		return FuncionarioResponse.de(cadastrarFuncionarioUseCase.executar(request.paraCommand()));
	}

	@Operation(summary = "Consulta os dados do funcionário logado")
	@GetMapping("/me")
	public FuncionarioResponse meusDados(@AuthenticationPrincipal Jwt jwt) {
		return FuncionarioResponse.de(buscarFuncionarioUseCase.executar(UsuarioLogado.id(jwt)));
	}

	@Operation(summary = "Atualiza o nome e a senha do funcionário logado")
	@PutMapping("/me")
	public FuncionarioResponse atualizar(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody AtualizarFuncionarioRequest request) {
		return FuncionarioResponse.de(
				atualizarFuncionarioUseCase.executar(UsuarioLogado.id(jwt), request.paraCommand()));
	}
}
