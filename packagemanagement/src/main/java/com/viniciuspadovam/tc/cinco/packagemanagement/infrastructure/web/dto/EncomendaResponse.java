package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.web.dto;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import java.time.LocalDateTime;

public record EncomendaResponse(
		Long id,
		Long moradorId,
		String nomeDestinatario,
		String apartamento,
		String descricao,
		StatusEncomenda status,
		LocalDateTime dataRecebimento,
		Long porteiroRecebimentoId,
		LocalDateTime dataNotificacao,
		LocalDateTime dataConfirmacao,
		LocalDateTime dataRetirada,
		Long porteiroRetiradaId) {

	public static EncomendaResponse de(Encomenda encomenda) {
		return new EncomendaResponse(encomenda.getId(), encomenda.getMoradorId(), encomenda.getNomeDestinatario(),
				encomenda.getApartamento(), encomenda.getDescricao(), encomenda.getStatus(),
				encomenda.getDataRecebimento(), encomenda.getPorteiroRecebimentoId(), encomenda.getDataNotificacao(),
				encomenda.getDataConfirmacao(), encomenda.getDataRetirada(), encomenda.getPorteiroRetiradaId());
	}
}
