package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

public interface EnvioEmailGateway {

	void enviar(String destinatario, String assunto, String mensagem);
}
