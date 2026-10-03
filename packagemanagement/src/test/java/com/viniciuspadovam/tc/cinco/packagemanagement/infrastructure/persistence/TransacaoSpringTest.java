package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

@ExtendWith(MockitoExtension.class)
class TransacaoSpringTest {

	@Mock
	private PlatformTransactionManager transactionManager;

	@Mock
	private TransactionStatus status;

	@Test
	void deveExecutarAcaoDentroDeUmaTransacaoEConfirmar() {
		when(transactionManager.getTransaction(any())).thenReturn(status);
		AtomicBoolean executou = new AtomicBoolean();

		new TransacaoSpring(transactionManager).executar(() -> executou.set(true));

		assertThat(executou).isTrue();
		verify(transactionManager).commit(status);
	}

	@Test
	void deveDesfazerQuandoAcaoFalha() {
		when(transactionManager.getTransaction(any())).thenReturn(status);

		assertThatThrownBy(() -> new TransacaoSpring(transactionManager).executar(() -> {
			throw new IllegalStateException("falhou");
		})).isInstanceOf(IllegalStateException.class);

		verify(transactionManager).rollback(status);
	}
}
