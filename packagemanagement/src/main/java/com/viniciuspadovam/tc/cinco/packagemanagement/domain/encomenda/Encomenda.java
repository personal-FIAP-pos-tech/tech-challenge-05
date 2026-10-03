package com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.shared.Validacoes;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public final class Encomenda {

	private static final int TAMANHO_MAXIMO_NOME = 120;
	private static final int TAMANHO_MAXIMO_DESCRICAO = 255;

	private final Long id;
	private final Long moradorId;
	private final String nomeDestinatario;
	private final String apartamento;
	private final String descricao;
	private final LocalDateTime dataRecebimento;
	private final Long porteiroRecebimentoId;
	private StatusEncomenda status;
	private LocalDateTime dataNotificacao;
	private LocalDateTime dataConfirmacao;
	private LocalDateTime dataRetirada;
	private Long porteiroRetiradaId;

	private Encomenda(Long id, Long moradorId, String nomeDestinatario, String apartamento, String descricao,
			StatusEncomenda status, LocalDateTime dataRecebimento, Long porteiroRecebimentoId,
			LocalDateTime dataNotificacao, LocalDateTime dataConfirmacao, LocalDateTime dataRetirada,
			Long porteiroRetiradaId) {
		this.id = id;
		this.moradorId = Validacoes.idObrigatorio(moradorId, "morador");
		this.nomeDestinatario = Validacoes.obrigatorio(nomeDestinatario, "nome do destinatário", TAMANHO_MAXIMO_NOME);
		this.apartamento = Validacoes.apartamento(apartamento);
		this.descricao = Validacoes.obrigatorio(descricao, "descrição", TAMANHO_MAXIMO_DESCRICAO);
		this.status = status;
		this.dataRecebimento = dataRecebimento;
		this.porteiroRecebimentoId = Validacoes.idObrigatorio(porteiroRecebimentoId, "porteiro");
		this.dataNotificacao = dataNotificacao;
		this.dataConfirmacao = dataConfirmacao;
		this.dataRetirada = dataRetirada;
		this.porteiroRetiradaId = porteiroRetiradaId;
	}

	public static Encomenda receber(Long moradorId, String nomeDestinatario, String apartamento, String descricao,
			Long porteiroId, LocalDateTime agora) {
		return new Encomenda(null, moradorId, nomeDestinatario, apartamento, descricao, StatusEncomenda.RECEBIDA,
				agora, porteiroId, null, null, null, null);
	}

	public static Encomenda restaurar(Long id, Long moradorId, String nomeDestinatario, String apartamento,
			String descricao, StatusEncomenda status, LocalDateTime dataRecebimento, Long porteiroRecebimentoId,
			LocalDateTime dataNotificacao, LocalDateTime dataConfirmacao, LocalDateTime dataRetirada,
			Long porteiroRetiradaId) {
		return new Encomenda(id, moradorId, nomeDestinatario, apartamento, descricao, status, dataRecebimento,
				porteiroRecebimentoId, dataNotificacao, dataConfirmacao, dataRetirada, porteiroRetiradaId);
	}

	public void marcarNotificada(LocalDateTime agora) {
		avancarPara(StatusEncomenda.NOTIFICADA, "A encomenda só pode ser notificada logo após o recebimento.");
		this.dataNotificacao = agora;
	}

	public void confirmarRecebimento(LocalDateTime agora) {
		avancarPara(StatusEncomenda.CONFIRMADA, "A encomenda só pode ser confirmada depois de notificada.");
		this.dataConfirmacao = agora;
	}

	public void registrarRetirada(Long porteiroId, LocalDateTime agora) {
		Validacoes.idObrigatorio(porteiroId, "porteiro");
		if (status == StatusEncomenda.RETIRADA) {
			throw new RegraNegocioException("Esta encomenda já foi retirada.");
		}
		avancarPara(StatusEncomenda.RETIRADA,
				"A retirada só pode ser registrada depois que o morador confirmar o recebimento da notificação.");
		this.dataRetirada = agora;
		this.porteiroRetiradaId = porteiroId;
	}

	private void avancarPara(StatusEncomenda destino, String mensagemErro) {
		if (!status.podeIrPara(destino)) {
			throw new RegraNegocioException(mensagemErro + " Status atual: " + status + ".");
		}
		this.status = destino;
	}
}
