package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.BuscarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.ListarEncomendasUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarRetiradaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.EncomendaResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.PaginaResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.RegistrarEncomendaRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/encomendas")
@RequiredArgsConstructor
public class EncomendaController {

	private final RegistrarEncomendaUseCase registrarEncomendaUseCase;
	private final RegistrarRetiradaUseCase registrarRetiradaUseCase;
	private final ListarEncomendasUseCase listarEncomendasUseCase;
	private final BuscarEncomendaUseCase buscarEncomendaUseCase;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EncomendaResponse registrar(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody RegistrarEncomendaRequest request) {
		return EncomendaResponse.de(registrarEncomendaUseCase.executar(UsuarioLogado.id(jwt), request.paraCommand()));
	}

	@GetMapping
	public PaginaResponse<EncomendaResponse> listar(
			@RequestParam(required = false) StatusEncomenda status,
			@RequestParam(required = false) String apartamento,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamanho) {
		return PaginaResponse.de(listarEncomendasUseCase.executar(status, apartamento, pagina, tamanho),
				EncomendaResponse::de);
	}

	@GetMapping("/{id}")
	public EncomendaResponse buscar(@PathVariable Long id) {
		return EncomendaResponse.de(buscarEncomendaUseCase.executar(id));
	}

	@PatchMapping("/{id}/retirada")
	public EncomendaResponse registrarRetirada(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		return EncomendaResponse.de(registrarRetiradaUseCase.executar(UsuarioLogado.id(jwt), id));
	}
}
