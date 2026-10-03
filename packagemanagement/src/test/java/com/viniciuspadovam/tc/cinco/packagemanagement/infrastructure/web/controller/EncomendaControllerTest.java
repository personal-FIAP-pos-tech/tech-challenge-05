package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller;

import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.morador;
import static com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.controller.MoradorControllerTest.porteiro;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.exception.RecursoNaoEncontradoException;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.BuscarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.ListarEncomendasUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaCommand;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarRetiradaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.security.SecurityConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EncomendaController.class)
@Import(SecurityConfig.class)
class EncomendaControllerTest {

	private static final String CORPO = """
			{"nomeDestinatario": "Ana Souza", "apartamento": "101", "descricao": "Caixa Amazon"}
			""";
	private static final LocalDateTime RECEBIMENTO = LocalDateTime.of(2026, 10, 1, 9, 0);

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RegistrarEncomendaUseCase registrarEncomendaUseCase;

	@MockitoBean
	private RegistrarRetiradaUseCase registrarRetiradaUseCase;

	@MockitoBean
	private ListarEncomendasUseCase listarEncomendasUseCase;

	@MockitoBean
	private BuscarEncomendaUseCase buscarEncomendaUseCase;

	private Encomenda encomenda(StatusEncomenda status) {
		return Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa Amazon", status, RECEBIMENTO, 5L,
				null, null, null, null);
	}

	@Test
	void porteiroDeveRegistrarEncomenda() throws Exception {
		when(registrarEncomendaUseCase.executar(eq(5L),
				eq(new RegistrarEncomendaCommand("Ana Souza", "101", "Caixa Amazon"))))
				.thenReturn(encomenda(StatusEncomenda.RECEBIDA));

		mockMvc.perform(post("/encomendas").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(30))
				.andExpect(jsonPath("$.moradorId").value(1))
				.andExpect(jsonPath("$.status").value("RECEBIDA"))
				.andExpect(jsonPath("$.dataRecebimento").value("2026-10-01T09:00:00"))
				.andExpect(jsonPath("$.porteiroRecebimentoId").value(5));
	}

	@Test
	void deveRetornar422QuandoMoradorNaoEEncontrado() throws Exception {
		when(registrarEncomendaUseCase.executar(eq(5L), any()))
				.thenThrow(new RegraNegocioException("Nenhum morador encontrado."));

		mockMvc.perform(post("/encomendas").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.detail").value("Nenhum morador encontrado."));
	}

	@Test
	void deveValidarCamposObrigatorios() throws Exception {
		mockMvc.perform(post("/encomendas").with(porteiro(5L))
						.contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.erros.nomeDestinatario").exists())
				.andExpect(jsonPath("$.erros.apartamento").exists())
				.andExpect(jsonPath("$.erros.descricao").exists());
		verifyNoInteractions(registrarEncomendaUseCase);
	}

	@Test
	void moradorNaoPodeRegistrarEncomenda() throws Exception {
		mockMvc.perform(post("/encomendas").with(morador(1L))
						.contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isForbidden());
		verifyNoInteractions(registrarEncomendaUseCase);
	}

	@Test
	void deveExigirLogin() throws Exception {
		mockMvc.perform(post("/encomendas").contentType(MediaType.APPLICATION_JSON).content(CORPO))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void porteiroDeveListarEncomendasComFiltrosEPaginacao() throws Exception {
		when(listarEncomendasUseCase.executar(StatusEncomenda.RECEBIDA, "101", 1, 5))
				.thenReturn(new Pagina<>(List.of(encomenda(StatusEncomenda.RECEBIDA)), 1, 5, 6));

		mockMvc.perform(get("/encomendas").with(porteiro(5L))
						.param("status", "RECEBIDA").param("apartamento", "101")
						.param("pagina", "1").param("tamanho", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.conteudo[0].id").value(30))
				.andExpect(jsonPath("$.pagina").value(1))
				.andExpect(jsonPath("$.tamanho").value(5))
				.andExpect(jsonPath("$.totalElementos").value(6))
				.andExpect(jsonPath("$.totalPaginas").value(2));
	}

	@Test
	void deveUsarPaginacaoPadrao() throws Exception {
		when(listarEncomendasUseCase.executar(null, null, 0, 20)).thenReturn(new Pagina<>(List.of(), 0, 20, 0));

		mockMvc.perform(get("/encomendas").with(porteiro(5L)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalElementos").value(0));
	}

	@Test
	void deveRetornar400ParaStatusInexistente() throws Exception {
		mockMvc.perform(get("/encomendas").with(porteiro(5L)).param("status", "PERDIDA"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void porteiroDeveConsultarEncomendaPorId() throws Exception {
		when(buscarEncomendaUseCase.executar(30L)).thenReturn(encomenda(StatusEncomenda.NOTIFICADA));

		mockMvc.perform(get("/encomendas/30").with(porteiro(5L)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("NOTIFICADA"));
	}

	@Test
	void deveRetornar404ParaEncomendaInexistente() throws Exception {
		when(buscarEncomendaUseCase.executar(99L)).thenThrow(new RecursoNaoEncontradoException("Encomenda", 99L));

		mockMvc.perform(get("/encomendas/99").with(porteiro(5L)))
				.andExpect(status().isNotFound());
	}

	@Test
	void porteiroDeveDarBaixaNaRetirada() throws Exception {
		Encomenda retirada = Encomenda.restaurar(30L, 1L, "Ana Souza", "101", "Caixa Amazon",
				StatusEncomenda.RETIRADA, RECEBIMENTO, 5L, RECEBIMENTO.plusMinutes(1), RECEBIMENTO.plusHours(1),
				RECEBIMENTO.plusHours(5), 6L);
		when(registrarRetiradaUseCase.executar(6L, 30L)).thenReturn(retirada);

		mockMvc.perform(patch("/encomendas/30/retirada").with(porteiro(6L)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("RETIRADA"))
				.andExpect(jsonPath("$.porteiroRetiradaId").value(6))
				.andExpect(jsonPath("$.dataRetirada").value("2026-10-01T14:00:00"));
	}

	@Test
	void deveRetornar422AoDarBaixaSemConfirmacao() throws Exception {
		when(registrarRetiradaUseCase.executar(6L, 30L))
				.thenThrow(new RegraNegocioException("A retirada só pode ser registrada depois da confirmação."));

		mockMvc.perform(patch("/encomendas/30/retirada").with(porteiro(6L)))
				.andExpect(status().isUnprocessableContent());
	}

	@Test
	void moradorNaoPodeDarBaixa() throws Exception {
		mockMvc.perform(patch("/encomendas/30/retirada").with(morador(1L)))
				.andExpect(status().isForbidden());
		verifyNoInteractions(registrarRetiradaUseCase);
	}
}
