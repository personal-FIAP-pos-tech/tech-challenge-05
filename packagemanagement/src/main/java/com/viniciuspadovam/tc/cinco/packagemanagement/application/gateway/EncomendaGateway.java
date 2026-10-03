package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import java.util.Optional;

public interface EncomendaGateway {

	Encomenda salvar(Encomenda encomenda);

	Optional<Encomenda> buscarPorId(Long id);
}
