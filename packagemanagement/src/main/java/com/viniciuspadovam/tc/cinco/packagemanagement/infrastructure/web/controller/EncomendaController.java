package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.BuscarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.ListarEncomendasUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarRetiradaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.EncomendaResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.PaginaResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.RegistrarEncomendaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Encomendas", description = "Recebimento, consulta e baixa de encomendas pela portaria (perfil PORTEIRO)")
@RestController
@RequestMapping("/encomendas")
@RequiredArgsConstructor
public class EncomendaController {

	private final RegistrarEncomendaUseCase registrarEncomendaUseCase;
	private final RegistrarRetiradaUseCase registrarRetiradaUseCase;
	private final ListarEncomendasUseCase listarEncomendasUseCase;
	private final BuscarEncomendaUseCase buscarEncomendaUseCase;

	@Operation(summary = "Registra uma encomenda recebida e a coloca na fila de processamento")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EncomendaResponse registrar(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody RegistrarEncomendaRequest request) {
		return EncomendaResponse.de(registrarEncomendaUseCase.executar(UsuarioLogado.id(jwt), request.paraCommand()));
	}

	@Operation(summary = "Lista encomendas com filtros por status e apartamento")
	@GetMapping
	public PaginaResponse<EncomendaResponse> listar(
			@RequestParam(required = false) StatusEncomenda status,
			@RequestParam(required = false) String apartamento,
			@RequestParam(defaultValue = "0") int pagina,
			@RequestParam(defaultValue = "20") int tamanho) {
		return PaginaResponse.de(listarEncomendasUseCase.executar(status, apartamento, pagina, tamanho),
				EncomendaResponse::de);
	}

	@Operation(summary = "Consulta uma encomenda pelo id")
	@GetMapping("/{id}")
	public EncomendaResponse buscar(@PathVariable Long id) {
		return EncomendaResponse.de(buscarEncomendaUseCase.executar(id));
	}

	@Operation(summary = "Dá baixa na retirada; exige a confirmação prévia do morador")
	@PatchMapping("/{id}/retirada")
	public EncomendaResponse registrarRetirada(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) {
		return EncomendaResponse.de(registrarRetiradaUseCase.executar(UsuarioLogado.id(jwt), id));
	}
}
