# Gerenciamento de Encomendas — Tech Challenge Java (Fase 5)

Sistema para a portaria de prédios residenciais. O porteiro registra as encomendas que chegam, o sistema avisa o morador por e-mail usando mensageria, o morador confirma que recebeu o aviso e o porteiro dá baixa quando a encomenda é retirada.

Construído com **Java 25**, **Spring Boot 4.1**, **Clean Architecture** e **TDD**.

O relatório técnico, com as tecnologias, os desafios enfrentados e as soluções adotadas, está em [docs/relatorio-tecnico.md](docs/relatorio-tecnico.md).

## Sumário

- [Como executar](#como-executar)
- [Usuários da carga inicial](#usuários-da-carga-inicial)
- [Fluxo e regras de negócio](#fluxo-e-regras-de-negócio)
- [API](#api)
- [Arquitetura](#arquitetura)
- [Testes e qualidade](#testes-e-qualidade)
- [Configuração](#configuração)

## Como executar

### Com Docker (recomendado)

Pré-requisito: Docker com Docker Compose.

```bash
docker compose up --build
```

| Serviço | Endereço |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| Mailpit (e-mails enviados) | http://localhost:8025 |
| RabbitMQ (painel) | http://localhost:15672 (usuário `guest`, senha `guest`) |
| Console do H2 | http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:encomendas`, usuário `sa`, sem senha) |

### Localmente

Pré-requisitos: JDK 25 e Docker (apenas para o RabbitMQ e o Mailpit).

```bash
docker compose up -d rabbitmq mailpit
cd packagemanagement
./mvnw spring-boot:run
```

O banco é um H2 em memória. A cada inicialização ele é recriado a partir de `schema.sql` e recebe a carga inicial de `data.sql`, com 6 registros em cada tabela principal.

## Usuários da carga inicial

Todos usam a senha **`Senha@123`**.

| Perfil | Nome | E-mail | Apartamento |
|---|---|---|---|
| Morador | Ana Souza | ana.souza@email.com | 101 |
| Morador | Bruno Lima | bruno.lima@email.com | 102 |
| Morador | Carla Mendes | carla.mendes@email.com | 201 |
| Morador | Diego Rocha | diego.rocha@email.com | 202 |
| Morador | Elisa Ferreira | elisa.ferreira@email.com | 301 |
| Morador | Fábio Gonçalves | fabio.goncalves@email.com | 302 |
| Porteiro | Carlos Pereira | carlos.pereira@portaria.com | — |
| Porteiro | Joana Alves | joana.alves@portaria.com | — |
| Porteiro | Marcos Dias | marcos.dias@portaria.com | — |
| Porteiro | Patrícia Nunes | patricia.nunes@portaria.com | — |
| Porteiro | Roberto Silva | roberto.silva@portaria.com | — |
| Porteiro | Sandra Costa | sandra.costa@portaria.com | — |

A carga também traz 6 encomendas, uma ou mais em cada status, e as notificações correspondentes. A encomenda 1 está com a notificação em **FALHA**, para ilustrar um envio que não deu certo.

## Fluxo e regras de negócio

```text
 Porteiro                    Sistema                                         Morador
    │  POST /encomendas         │                                               │
    ├──────────────────────────►│ encomenda RECEBIDA                            │
    │                           │──► fila encomendas.entrada (canal de entrada) │
    │                           │    cria notificação PENDENTE                  │
    │                           │──► fila notificacoes.saida (canal de saída)   │
    │                           │    envia e-mail ─────────────────────────────►│
    │                           │    notificação ENVIADA, encomenda NOTIFICADA  │
    │                           │                                               │
    │                           │◄──────── PATCH /notificacoes/{id}/confirmacao │
    │                           │    notificação e encomenda CONFIRMADA         │
    │  PATCH /encomendas/{id}/retirada                                          │
    ├──────────────────────────►│ encomenda RETIRADA                            │
```

- **Cadastro:** moradores e funcionários se cadastram sem login. Qualquer outra operação exige login.
- **Login:** é feito com e-mail e senha e devolve um token JWT. O e-mail é único no sistema todo e não pode ser alterado.
- **Perfis:** todo funcionário tem o perfil `PORTEIRO`. Só o porteiro registra encomendas, consulta todas e dá baixa. Só o morador confirma notificações.
- **Vínculo com o morador:** a encomenda só é aceita se o nome do destinatário e o apartamento casarem com um morador cadastrado. A comparação ignora maiúsculas, acentos e espaços extras. Sem correspondência, a API responde 422 e nada é gravado. Por isso, não podem existir dois moradores com o mesmo nome no mesmo apartamento.
- **Ciclo de vida:** a encomenda passa por `RECEBIDA → NOTIFICADA → CONFIRMADA → RETIRADA`, sem pular etapas. A baixa só é aceita depois que o morador confirma a notificação.
- **Registro automático:** datas e porteiros responsáveis pelo recebimento e pela retirada são preenchidos pelo sistema a partir do token.
- **Falha de envio:** o e-mail é tentado até 3 vezes. Esgotadas as tentativas, a mensagem vai para a fila `notificacoes.saida.dlq` e a notificação fica como `FALHA`. Reentregas da mesma mensagem não duplicam notificações nem e-mails.
- **Validações:** a senha precisa ter de 8 a 72 caracteres. O telefone precisa de DDD, com 10 ou 11 dígitos, e aceita máscara. O apartamento aceita até 10 letras, números ou hífen.

## API

A documentação completa e interativa está no Swagger UI. Para as rotas protegidas, faça login e informe o token no botão **Authorize**.

| Método | Rota | Acesso | Descrição |
|---|---|---|---|
| POST | `/auth/login` | público | Login, devolve o token JWT |
| POST | `/moradores` | público | Cadastro de morador |
| GET, PUT | `/moradores/me` | morador | Consulta e atualiza os próprios dados |
| GET | `/moradores/me/encomendas` | morador | Lista as próprias encomendas |
| GET | `/moradores/me/notificacoes` | morador | Lista as próprias notificações |
| PATCH | `/notificacoes/{id}/confirmacao` | morador | Confirma o recebimento da notificação |
| POST | `/funcionarios` | público | Cadastro de funcionário (porteiro) |
| GET, PUT | `/funcionarios/me` | porteiro | Consulta e atualiza os próprios dados |
| POST | `/encomendas` | porteiro | Registra uma encomenda recebida |
| GET | `/encomendas?status=&apartamento=&pagina=&tamanho=` | porteiro | Lista encomendas com filtros e paginação |
| GET | `/encomendas/{id}` | porteiro | Consulta uma encomenda |
| PATCH | `/encomendas/{id}/retirada` | porteiro | Dá baixa na retirada |

Os erros seguem o formato `ProblemDetail`:

| Código | Situação |
|---|---|
| 400 | Dados inválidos, com o campo `erros` listando cada campo |
| 401 | Sem login, token inválido ou credenciais erradas |
| 403 | Perfil sem permissão para a rota |
| 404 | Recurso inexistente ou de outro morador |
| 409 | E-mail ou morador duplicado |
| 422 | Regra de negócio violada |

Exemplo de uso com `curl`:

```bash
TOKEN=$(curl -s localhost:8080/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"carlos.pereira@portaria.com","senha":"Senha@123"}' | jq -r .accessToken)

curl -s localhost:8080/encomendas -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"nomeDestinatario":"Ana Souza","apartamento":"101","descricao":"Caixa média - Amazon"}'
```

## Arquitetura

O código segue a Clean Architecture, com dependências apontando sempre para dentro:

```text
packagemanagement/src/main/java/.../packagemanagement
├── domain/            Entidades e regras de negócio (Java puro, sem Spring)
│   ├── morador/ funcionario/ encomenda/ notificacao/ usuario/
│   ├── shared/        Validações e normalização de nomes
│   └── exception/
├── application/       Casos de uso e portas (interfaces) que eles usam
│   ├── usecase/       Um caso de uso por classe, sem anotações do Spring
│   ├── gateway/       Portas: persistência, mensageria, e-mail, senha, token, transação
│   └── exception/
└── infrastructure/    Adaptadores e frameworks
    ├── web/           Controllers REST, DTOs e tratamento de erros
    ├── persistence/   Entidades JPA, repositórios e implementações dos gateways
    ├── messaging/     Configuração do RabbitMQ, publicadores e consumidores
    ├── email/         Envio de e-mail via SMTP
    ├── security/      Spring Security com JWT e BCrypt
    └── config/        Montagem dos casos de uso como beans e OpenAPI
```

- **Domínio:** as entidades validam os próprios dados e controlam as transições de status. Não dependem de nenhuma biblioteca externa além do Lombok.
- **Aplicação:** os casos de uso conversam com o mundo externo só pelas portas. São instanciados pela configuração do Spring, então não conhecem o framework.
- **Infraestrutura:** implementa as portas e expõe a API. O teste `ArquiteturaTest`, com ArchUnit, garante que essas regras de dependência não sejam quebradas.

| Tecnologia | Uso |
|---|---|
| Spring Boot 4.1 (Web MVC, Data JPA, Validation) | API e persistência |
| Spring Security + OAuth2 Resource Server | Autenticação JWT (HS256) e senhas com BCrypt |
| H2 | Banco em memória com carga inicial |
| RabbitMQ (Spring AMQP) | Canais de entrada e saída, com DLQ e novas tentativas |
| Mailpit | Servidor SMTP de desenvolvimento que recebe os e-mails |
| springdoc-openapi | Documentação Swagger |
| JUnit 5, Mockito, AssertJ, ArchUnit | Testes unitários e de arquitetura |
| Testcontainers, GreenMail, Awaitility | Testes de integração com RabbitMQ real e SMTP falso |
| JaCoCo, Checkstyle, SpotBugs | Cobertura e análise estática |

## Testes e qualidade

Os comandos abaixo são executados dentro da pasta `packagemanagement`. No Windows, use `mvnw.cmd` no lugar de `./mvnw`.

| Objetivo | Comando |
|---|---|
| Testes unitários e de componentes (domínio, casos de uso, controllers, repositórios, arquitetura) | `./mvnw test` |
| Um teste unitário específico | `./mvnw test -Dtest=EncomendaTest` |
| Apenas os testes de integração | `./mvnw verify -Dtest=nenhum -Dsurefire.failIfNoSpecifiedTests=false -Djacoco.skip=true` |
| Um teste de integração específico | `./mvnw verify -Dtest=nenhum -Dsurefire.failIfNoSpecifiedTests=false -Djacoco.skip=true -Dit.test=FluxoCompletoEncomendaIT` |
| Build completo: todos os testes, cobertura mínima, Checkstyle e SpotBugs | `./mvnw verify` |
| Apenas o Checkstyle | `./mvnw checkstyle:check` |
| Apenas o SpotBugs | `./mvnw compile spotbugs:check` |

- **Testes unitários:** terminam em `Test` e rodam com o Surefire.
- **Testes de integração:** terminam em `IT` e rodam com o Failsafe. Os que usam RabbitMQ real (`FluxoCompletoEncomendaIT`, `FalhaEnvioNotificacaoIT` e `MensageriaEncomendaIT`) sobem o broker com Testcontainers e precisam do Docker. Sem Docker, eles são pulados automaticamente.
- **Cobertura:** o JaCoCo exige pelo menos 80% de cobertura de linhas no `verify`. O relatório fica em `packagemanagement/target/site/jacoco/index.html`.
- **Análise estática:** as regras do Checkstyle estão em `packagemanagement/config/checkstyle`. As exclusões do SpotBugs, cada uma com justificativa, estão em `packagemanagement/config/spotbugs`.

## Configuração

Variáveis de ambiente aceitas pela aplicação:

| Variável | Padrão | Descrição |
|---|---|---|
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | `localhost` / `5672` | Endereço do RabbitMQ |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | `guest` / `guest` | Credenciais do RabbitMQ |
| `MAIL_HOST` / `MAIL_PORT` | `localhost` / `1025` | Servidor SMTP |
| `NOTIFICACAO_REMETENTE` | `portaria@condominio.com` | Remetente dos e-mails |
| `JWT_SECRET` | valor de desenvolvimento | Chave HS256 com pelo menos 32 bytes; troque em produção |
| `JWT_EXPIRACAO` | `1h` | Validade do token |
| `H2_CONSOLE_ENABLED` | `true` | Habilita o console do H2 |
