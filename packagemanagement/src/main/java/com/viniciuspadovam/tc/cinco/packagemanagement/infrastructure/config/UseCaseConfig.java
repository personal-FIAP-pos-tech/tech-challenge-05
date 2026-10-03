package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.config;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.TokenProvider;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.AutenticarUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.AtualizarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.BuscarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.CadastrarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.AtualizarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.BuscarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.CadastrarMoradorUseCase;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

	@Bean
	public CadastrarMoradorUseCase cadastrarMoradorUseCase(MoradorGateway moradorGateway,
			FuncionarioGateway funcionarioGateway, SenhaEncoder senhaEncoder) {
		return new CadastrarMoradorUseCase(moradorGateway, funcionarioGateway, senhaEncoder);
	}

	@Bean
	public AtualizarMoradorUseCase atualizarMoradorUseCase(MoradorGateway moradorGateway, SenhaEncoder senhaEncoder) {
		return new AtualizarMoradorUseCase(moradorGateway, senhaEncoder);
	}

	@Bean
	public BuscarMoradorUseCase buscarMoradorUseCase(MoradorGateway moradorGateway) {
		return new BuscarMoradorUseCase(moradorGateway);
	}

	@Bean
	public CadastrarFuncionarioUseCase cadastrarFuncionarioUseCase(FuncionarioGateway funcionarioGateway,
			MoradorGateway moradorGateway, SenhaEncoder senhaEncoder) {
		return new CadastrarFuncionarioUseCase(funcionarioGateway, moradorGateway, senhaEncoder);
	}

	@Bean
	public AtualizarFuncionarioUseCase atualizarFuncionarioUseCase(FuncionarioGateway funcionarioGateway,
			SenhaEncoder senhaEncoder) {
		return new AtualizarFuncionarioUseCase(funcionarioGateway, senhaEncoder);
	}

	@Bean
	public BuscarFuncionarioUseCase buscarFuncionarioUseCase(FuncionarioGateway funcionarioGateway) {
		return new BuscarFuncionarioUseCase(funcionarioGateway);
	}

	@Bean
	public RegistrarEncomendaUseCase registrarEncomendaUseCase(MoradorGateway moradorGateway,
			EncomendaGateway encomendaGateway, EncomendaRecebidaPublisher encomendaRecebidaPublisher, Clock clock) {
		return new RegistrarEncomendaUseCase(moradorGateway, encomendaGateway, encomendaRecebidaPublisher, clock);
	}

	@Bean
	public AutenticarUseCase autenticarUseCase(MoradorGateway moradorGateway, FuncionarioGateway funcionarioGateway,
			SenhaEncoder senhaEncoder, TokenProvider tokenProvider) {
		return new AutenticarUseCase(moradorGateway, funcionarioGateway, senhaEncoder, tokenProvider);
	}
}
