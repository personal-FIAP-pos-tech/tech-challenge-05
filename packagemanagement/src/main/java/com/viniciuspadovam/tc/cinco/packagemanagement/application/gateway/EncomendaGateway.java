package com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.Pagina;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.Encomenda;
import com.viniciuspadovam.tc.cinco.packagemanagement.domain.encomenda.StatusEncomenda;
import java.util.List;
import java.util.Optional;

public interface EncomendaGateway {

	Encomenda salvar(Encomenda encomenda);

	Optional<Encomenda> buscarPorId(Long id);

	Pagina<Encomenda> listar(StatusEncomenda status, String apartamento, int pagina, int tamanho);

	List<Encomenda> listarPorMorador(Long moradorId);
}
