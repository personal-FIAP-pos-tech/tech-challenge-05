package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.config;

import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EncomendaRecebidaPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.EnvioEmailGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.FuncionarioGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.MoradorGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoGateway;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.NotificacaoPublisher;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.SenhaEncoder;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.TokenProvider;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.gateway.UnidadeDeTrabalho;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.autenticacao.AutenticarUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.BuscarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.ListarEncomendasDoMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.ListarEncomendasUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarEncomendaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.encomenda.RegistrarRetiradaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.AtualizarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.BuscarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.funcionario.CadastrarFuncionarioUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.AtualizarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.BuscarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.morador.CadastrarMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ConfirmarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.EnviarNotificacaoUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ListarNotificacoesDoMoradorUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.ProcessarEncomendaRecebidaUseCase;
import com.viniciuspadovam.tc.cinco.packagemanagement.application.usecase.notificacao.RegistrarFalhaNotificacaoUseCase;
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
	public ProcessarEncomendaRecebidaUseCase processarEncomendaRecebidaUseCase(EncomendaGateway encomendaGateway,
			MoradorGateway moradorGateway, NotificacaoGateway notificacaoGateway,
			NotificacaoPublisher notificacaoPublisher, Clock clock) {
		return new ProcessarEncomendaRecebidaUseCase(encomendaGateway, moradorGateway, notificacaoGateway,
				notificacaoPublisher, clock);
	}

	@Bean
	public EnviarNotificacaoUseCase enviarNotificacaoUseCase(NotificacaoGateway notificacaoGateway,
			EncomendaGateway encomendaGateway, EnvioEmailGateway envioEmailGateway,
			UnidadeDeTrabalho unidadeDeTrabalho, Clock clock) {
		return new EnviarNotificacaoUseCase(notificacaoGateway, encomendaGateway, envioEmailGateway,
				unidadeDeTrabalho, clock);
	}

	@Bean
	public RegistrarFalhaNotificacaoUseCase registrarFalhaNotificacaoUseCase(NotificacaoGateway notificacaoGateway) {
		return new RegistrarFalhaNotificacaoUseCase(notificacaoGateway);
	}

	@Bean
	public ConfirmarNotificacaoUseCase confirmarNotificacaoUseCase(NotificacaoGateway notificacaoGateway,
			EncomendaGateway encomendaGateway, UnidadeDeTrabalho unidadeDeTrabalho, Clock clock) {
		return new ConfirmarNotificacaoUseCase(notificacaoGateway, encomendaGateway, unidadeDeTrabalho, clock);
	}

	@Bean
	public ListarNotificacoesDoMoradorUseCase listarNotificacoesDoMoradorUseCase(
			NotificacaoGateway notificacaoGateway) {
		return new ListarNotificacoesDoMoradorUseCase(notificacaoGateway);
	}

	@Bean
	public RegistrarRetiradaUseCase registrarRetiradaUseCase(EncomendaGateway encomendaGateway, Clock clock) {
		return new RegistrarRetiradaUseCase(encomendaGateway, clock);
	}

	@Bean
	public ListarEncomendasUseCase listarEncomendasUseCase(EncomendaGateway encomendaGateway) {
		return new ListarEncomendasUseCase(encomendaGateway);
	}

	@Bean
	public BuscarEncomendaUseCase buscarEncomendaUseCase(EncomendaGateway encomendaGateway) {
		return new BuscarEncomendaUseCase(encomendaGateway);
	}

	@Bean
	public ListarEncomendasDoMoradorUseCase listarEncomendasDoMoradorUseCase(EncomendaGateway encomendaGateway) {
		return new ListarEncomendasDoMoradorUseCase(encomendaGateway);
	}

	@Bean
	public AutenticarUseCase autenticarUseCase(MoradorGateway moradorGateway, FuncionarioGateway funcionarioGateway,
			SenhaEncoder senhaEncoder, TokenProvider tokenProvider) {
		return new AutenticarUseCase(moradorGateway, funcionarioGateway, senhaEncoder, tokenProvider);
	}
}
