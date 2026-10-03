package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.usuario.Perfil;

public record UsuarioAutenticado(Long id, String email, Perfil perfil) {
}
