package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.TokenGerado;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.UsuarioAutenticado;

public interface TokenProvider {

	TokenGerado gerar(UsuarioAutenticado usuario);
}
