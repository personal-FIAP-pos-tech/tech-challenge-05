package com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador;

public record CadastrarMoradorCommand(String nome, String email, String senha, String telefone, String apartamento) {
}
