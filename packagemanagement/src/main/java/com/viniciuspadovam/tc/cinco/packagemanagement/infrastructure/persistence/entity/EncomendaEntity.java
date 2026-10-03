package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "encomendas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncomendaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "morador_id", nullable = false)
	private Long moradorId;

	@Column(name = "nome_destinatario", nullable = false, length = 120)
	private String nomeDestinatario;

	@Column(nullable = false, length = 10)
	private String apartamento;

	@Column(nullable = false)
	private String descricao;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusEncomenda status;

	@Column(name = "data_recebimento", nullable = false)
	private LocalDateTime dataRecebimento;

	@Column(name = "porteiro_recebimento_id", nullable = false)
	private Long porteiroRecebimentoId;

	@Column(name = "data_notificacao")
	private LocalDateTime dataNotificacao;

	@Column(name = "data_confirmacao")
	private LocalDateTime dataConfirmacao;

	@Column(name = "data_retirada")
	private LocalDateTime dataRetirada;

	@Column(name = "porteiro_retirada_id")
	private Long porteiroRetiradaId;
}
