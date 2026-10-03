package com.viniciuspadovam.tc.cinco.packagemanagement.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	public static final String ESQUEMA_SEGURANCA = "bearerAuth";

	private static final String DESCRICAO = String.join(" ",
			"API para a portaria de prédios residenciais.",
			"O porteiro registra as encomendas recebidas, o sistema notifica o morador por e-mail",
			"através de mensageria, o morador confirma o recebimento da notificação e o porteiro",
			"dá baixa na retirada.",
			"\n\nPara usar as rotas protegidas, faça login em POST /auth/login e informe o token",
			"no botão Authorize. Os usuários da carga inicial usam a senha Senha@123.");

	@Bean
	public OpenAPI openApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Gerenciamento de Encomendas - Portaria")
						.version("1.0.0")
						.description(DESCRICAO))
				.components(new Components().addSecuritySchemes(ESQUEMA_SEGURANCA, new SecurityScheme()
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(ESQUEMA_SEGURANCA));
	}
}
