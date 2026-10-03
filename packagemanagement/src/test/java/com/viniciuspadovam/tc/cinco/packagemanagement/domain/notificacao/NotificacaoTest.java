package com.viniciuspadovam.tc.cinco.packagemanagement.domain.notificacao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.exception.RegraNegocioException;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class NotificacaoTest {

	private static final LocalDateTime AGORA = LocalDateTime.of(2026, 10, 1, 9, 5);

	private final Morador morador = Morador.restaurar(1L, "Ana Souza", "ana@email.com", "hash", "11988887777", "101");
	private final Encomenda encomenda = Encomenda.restaurar(20L, 1L, "Ana Souza", "101", "Caixa Amazon",
			StatusEncomenda.RECEBIDA, LocalDateTime.of(2026, 10, 1, 9, 0), 10L, null, null, null, null);

	private Notificacao novaNotificacao() {
		return Notificacao.paraEncomenda(encomenda, morador, AGORA);
	}

	@Test
	void deveCriarNotificacaoPendenteParaOMorador() {
		Notificacao notificacao = novaNotificacao();

		assertThat(notificacao.getId()).isNull();
		assertThat(notificacao.getEncomendaId()).isEqualTo(20L);
		assertThat(notificacao.getMoradorId()).isEqualTo(1L);
		assertThat(notificacao.getDestinatario()).isEqualTo("ana@email.com");
		assertThat(notificacao.getAssunto()).contains("encomenda");
		assertThat(notificacao.getMensagem()).contains("Ana Souza", "Caixa Amazon", "01/10/2026 09:00");
		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.PENDENTE);
		assertThat(notificacao.getDataCriacao()).isEqualTo(AGORA);
		assertThat(notificacao.getDataEnvio()).isNull();
		assertThat(notificacao.getDataConfirmacao()).isNull();
	}

	@Test
	void deveMarcarComoEnviadaEDepoisConfirmar() {
		Notificacao notificacao = novaNotificacao();

		notificacao.marcarEnviada(AGORA.plusSeconds(10));
		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.ENVIADA);
		assertThat(notificacao.getDataEnvio()).isEqualTo(AGORA.plusSeconds(10));

		notificacao.confirmar(AGORA.plusHours(1));
		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.CONFIRMADA);
		assertThat(notificacao.getDataConfirmacao()).isEqualTo(AGORA.plusHours(1));
	}

	@Test
	void naoDeveConfirmarNotificacaoNaoEnviada() {
		Notificacao notificacao = novaNotificacao();

		assertThatThrownBy(() -> notificacao.confirmar(AGORA))
				.isInstanceOf(RegraNegocioException.class);
	}

	@Test
	void naoDeveConfirmarDuasVezes() {
		Notificacao notificacao = novaNotificacao();
		notificacao.marcarEnviada(AGORA);
		notificacao.confirmar(AGORA);

		assertThatThrownBy(() -> notificacao.confirmar(AGORA))
				.isInstanceOf(RegraNegocioException.class)
				.hasMessageContaining("já foi confirmada");
	}

	@Test
	void deveMarcarFalhaApenasQuandoPendente() {
		Notificacao notificacao = novaNotificacao();

		notificacao.marcarFalha();
		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.FALHA);

		assertThatThrownBy(() -> notificacao.marcarEnviada(AGORA))
				.isInstanceOf(RegraNegocioException.class);
	}

	@Test
	void deveIndicarSePertenceAoMorador() {
		Notificacao notificacao = novaNotificacao();

		assertThat(notificacao.pertenceAo(1L)).isTrue();
		assertThat(notificacao.pertenceAo(2L)).isFalse();
	}

	@Test
	void devePermitirEnvioApenasQuandoPendente() {
		Notificacao notificacao = novaNotificacao();
		assertThat(notificacao.aguardandoEnvio()).isTrue();

		notificacao.marcarEnviada(AGORA);
		assertThat(notificacao.aguardandoEnvio()).isFalse();
	}

	@Test
	void deveRestaurarNotificacao() {
		Notificacao notificacao = Notificacao.restaurar(3L, 20L, 1L, "ana@email.com", "Assunto", "Mensagem",
				StatusNotificacao.ENVIADA, AGORA, AGORA, null);

		assertThat(notificacao.getId()).isEqualTo(3L);
		assertThat(notificacao.getStatus()).isEqualTo(StatusNotificacao.ENVIADA);
	}
}
