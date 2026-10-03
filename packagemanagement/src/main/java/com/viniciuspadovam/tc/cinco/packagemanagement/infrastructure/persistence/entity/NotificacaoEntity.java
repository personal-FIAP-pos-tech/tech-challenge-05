package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence.entity;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao.StatusNotificacao;
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
@Table(name = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificacaoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "encomenda_id", nullable = false, unique = true)
	private Long encomendaId;

	@Column(name = "morador_id", nullable = false)
	private Long moradorId;

	@Column(nullable = false)
	private String destinatario;

	@Column(nullable = false, length = 150)
	private String assunto;

	@Column(nullable = false, length = 2000)
	private String mensagem;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private StatusNotificacao status;

	@Column(name = "data_criacao", nullable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_envio")
	private LocalDateTime dataEnvio;

	@Column(name = "data_confirmacao")
	private LocalDateTime dataConfirmacao;
}
