package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.persistence;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.UnidadeDeTrabalho;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
public class TransacaoSpring implements UnidadeDeTrabalho {

	private final TransactionTemplate transactionTemplate;

	public TransacaoSpring(PlatformTransactionManager transactionManager) {
		this.transactionTemplate = new TransactionTemplate(transactionManager);
	}

	@Override
	public void executar(Runnable acao) {
		transactionTemplate.executeWithoutResult(status -> acao.run());
	}
}
