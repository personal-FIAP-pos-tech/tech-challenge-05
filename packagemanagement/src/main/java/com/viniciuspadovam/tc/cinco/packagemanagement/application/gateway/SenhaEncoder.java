package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

public interface SenhaEncoder {

	String codificar(String senha);

	boolean confere(String senha, String senhaHash);
}
