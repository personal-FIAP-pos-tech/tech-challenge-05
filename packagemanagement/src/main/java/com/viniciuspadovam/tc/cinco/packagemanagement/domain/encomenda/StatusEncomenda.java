package com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda;

public enum StatusEncomenda {
	RECEBIDA,
	NOTIFICADA,
	CONFIRMADA,
	RETIRADA;

	public boolean podeIrPara(StatusEncomenda destino) {
		return destino.ordinal() == ordinal() + 1;
	}
}
