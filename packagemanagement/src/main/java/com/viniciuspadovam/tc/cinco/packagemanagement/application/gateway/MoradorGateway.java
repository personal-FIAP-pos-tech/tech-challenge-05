package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.morador.Morador;
import java.util.Optional;

public interface MoradorGateway {

	Morador salvar(Morador morador);

	Optional<Morador> buscarPorId(Long id);

	Optional<Morador> buscarPorEmail(String email);

	boolean existePorEmail(String email);

	Optional<Morador> buscarPorApartamentoENome(String apartamento, String nomeNormalizado);
}
