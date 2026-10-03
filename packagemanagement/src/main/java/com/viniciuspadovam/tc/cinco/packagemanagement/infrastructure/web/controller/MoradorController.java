package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.ListarEncomendasDoMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.AtualizarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.BuscarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.CadastrarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ListarNotificacoesDoMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.AtualizarMoradorRequest;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.CadastrarMoradorRequest;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.EncomendaResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.MoradorResponse;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto.NotificacaoResponse;
import jakarta.validation.Valid;
import java.util.List;
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

@RestController
@RequestMapping("/moradores")
@RequiredArgsConstructor
public class MoradorController {

	private final CadastrarMoradorUseCase cadastrarMoradorUseCase;
	private final AtualizarMoradorUseCase atualizarMoradorUseCase;
	private final BuscarMoradorUseCase buscarMoradorUseCase;
	private final ListarEncomendasDoMoradorUseCase listarEncomendasDoMoradorUseCase;
	private final ListarNotificacoesDoMoradorUseCase listarNotificacoesDoMoradorUseCase;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MoradorResponse cadastrar(@Valid @RequestBody CadastrarMoradorRequest request) {
		return MoradorResponse.de(cadastrarMoradorUseCase.executar(request.paraCommand()));
	}

	@GetMapping("/me")
	public MoradorResponse meusDados(@AuthenticationPrincipal Jwt jwt) {
		return MoradorResponse.de(buscarMoradorUseCase.executar(UsuarioLogado.id(jwt)));
	}

	@PutMapping("/me")
	public MoradorResponse atualizar(@AuthenticationPrincipal Jwt jwt,
			@Valid @RequestBody AtualizarMoradorRequest request) {
		return MoradorResponse.de(atualizarMoradorUseCase.executar(UsuarioLogado.id(jwt), request.paraCommand()));
	}

	@GetMapping("/me/encomendas")
	public List<EncomendaResponse> minhasEncomendas(@AuthenticationPrincipal Jwt jwt) {
		return listarEncomendasDoMoradorUseCase.executar(UsuarioLogado.id(jwt)).stream()
				.map(EncomendaResponse::de)
				.toList();
	}

	@GetMapping("/me/notificacoes")
	public List<NotificacaoResponse> minhasNotificacoes(@AuthenticationPrincipal Jwt jwt) {
		return listarNotificacoesDoMoradorUseCase.executar(UsuarioLogado.id(jwt)).stream()
				.map(NotificacaoResponse::de)
				.toList();
	}
}
