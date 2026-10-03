package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentacaoApiIT {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void documentacaoOpenApiDeveSerPublicaEDescreverAsRotas() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Gerenciamento de Encomendas - Portaria"))
				.andExpect(jsonPath("$.components.securitySchemes.bearerAuth.scheme").value("bearer"))
				.andExpect(jsonPath("$.paths['/auth/login'].post.security").isEmpty())
				.andExpect(jsonPath("$.paths['/encomendas'].post.summary").exists())
				.andExpect(jsonPath("$.paths['/encomendas/{id}/retirada'].patch").exists())
				.andExpect(jsonPath("$.paths['/notificacoes/{id}/confirmacao'].patch").exists())
				.andExpect(jsonPath("$.paths['/moradores/me/notificacoes'].get").exists());
	}

	@Test
	void interfaceDoSwaggerDeveSerPublica() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk());
	}
}
