package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarEncomendaRequest(
		@NotBlank @Size(max = 120) String nomeDestinatario,
		@NotBlank @Size(max = 10) String apartamento,
		@NotBlank @Size(max = 255) String descricao) {

	public RegistrarEncomendaCommand paraCommand() {
		return new RegistrarEncomendaCommand(nomeDestinatario, apartamento, descricao);
	}
}
