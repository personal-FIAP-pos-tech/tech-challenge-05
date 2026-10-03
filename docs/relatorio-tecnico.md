# Relatório Técnico — Gerenciamento de Encomendas

Tech Challenge Java, Fase 5 (substitutiva).

Repositório: https://github.com/personal-FIAP-pos-tech/tech-challenge-05

## 1. Visão geral

O sistema atende a portaria de um prédio residencial. O porteiro registra cada encomenda recebida. A encomenda entra em uma fila de processamento, e o sistema avisa o morador por e-mail. O morador entra no sistema com e-mail e senha e confirma que recebeu o aviso. Quando ele retira a encomenda, o porteiro dá baixa, e o sistema registra automaticamente a data e quem fez a entrega.

Todas as funcionalidades pedidas no enunciado foram implementadas:

| Requisito | Como foi atendido |
|---|---|
| Cadastro de moradores | `POST /moradores` sem login. Consulta e alteração em `/moradores/me` com login |
| Cadastro de funcionários | `POST /funcionarios` sem login. Consulta e alteração em `/funcionarios/me` com login |
| Login | JWT próprio, assinado com HS256, para os dois perfis |
| Recebimento pela portaria | `POST /encomendas`, exclusivo do perfil PORTEIRO |
| Fila de processamento e canal de entrada | Fila `encomendas.entrada` no RabbitMQ |
| Notificação aos moradores e canal de saída | Fila `notificacoes.saida`, cujo consumidor envia o e-mail |
| Confirmação do morador | `PATCH /notificacoes/{id}/confirmacao` com login |
| Baixa da retirada | `PATCH /encomendas/{id}/retirada`, exclusivo do perfil PORTEIRO |
| Persistência para consulta e análise | Encomendas e notificações gravadas no banco, com consultas paginadas e filtros |
| Testes unitários e de integração | 215 testes unitários e de componentes e 18 de integração |
| Docker | Dockerfile multi-stage e docker-compose com a aplicação, o RabbitMQ e o Mailpit |
| Documentação | Swagger UI gerado com springdoc-openapi e README |

## 2. Tecnologias e ferramentas

| Camada | Tecnologia | Motivo da escolha |
|---|---|---|
| Linguagem e framework | Java 25, Spring Boot 4.1 | Versões atuais com suporte de longo prazo |
| API | Spring Web MVC, Bean Validation | Validação declarativa dos dados de entrada |
| Segurança | Spring Security, OAuth2 Resource Server, BCrypt | JWT validado pelo próprio Spring, sem servidor de autorização externo |
| Persistência | Spring Data JPA, Hibernate, H2 | Banco em memória simples de executar, recriado a cada inicialização com carga inicial |
| Mensageria | RabbitMQ, Spring AMQP | Filas duráveis, dead letter queue e novas tentativas nativas |
| Notificação | Spring Mail com Mailpit | E-mail real via SMTP, visível em uma interface web |
| Documentação | springdoc-openapi (Swagger) | Documentação interativa gerada a partir do código |
| Testes | JUnit 5, Mockito, AssertJ, ArchUnit, Testcontainers, GreenMail, Awaitility | Cobrem as camadas isoladamente e o fluxo completo com infraestrutura real |
| Qualidade | JaCoCo, Checkstyle, SpotBugs | Cobertura mínima e análise estática executadas no build |
| Produtividade | Lombok | Remove código repetitivo, como getters e construtores |
| Execução | Docker, Docker Compose | Ambiente completo com um único comando |

## 3. Aplicação da Clean Architecture

O código está dividido em três camadas. As dependências sempre apontam para dentro.

- **Domínio (Entities):** contém as entidades de morador, funcionário, encomenda e notificação. Elas validam os próprios dados e concentram as regras de negócio. A encomenda, por exemplo, só muda de status por métodos que impedem pular etapas, e a notificação monta o próprio texto do e-mail. Essa camada não conhece Spring, JPA nem HTTP.
- **Aplicação (Use Cases):** cada caso de uso é uma classe com uma única responsabilidade, como registrar encomenda, processar encomenda recebida, enviar notificação, confirmar notificação ou registrar retirada. Os casos de uso falam com o mundo externo apenas por portas, que são interfaces de gateway para persistência, publicação em filas, envio de e-mail, codificação de senha, geração de token e controle de transação.
- **Infraestrutura (Interface Adapters e Frameworks):** controllers REST, consumidores do RabbitMQ, entidades JPA, adaptador SMTP e segurança JWT. Esses adaptadores implementam as portas e chamam os casos de uso. A classe de configuração cria os casos de uso como beans, e por isso os casos de uso não têm nenhuma anotação do Spring.

As regras são verificadas automaticamente pelo teste `ArquiteturaTest`, com ArchUnit. Ele falha o build se o domínio depender de outra camada ou se o domínio e a aplicação importarem Spring ou Jakarta.

**Benefício prático:** a troca do RabbitMQ por outro broker, ou do e-mail por SMS, exige apenas um novo adaptador para a porta correspondente. Os casos de uso não mudam. Foi o que permitiu testar todo o fluxo de notificação com um SMTP falso (GreenMail) e com uma simulação de falha, sem alterar código de produção.

## 4. Qualidade de software

### 4.1 Desenvolvimento orientado a testes

Cada funcionalidade foi desenvolvida escrevendo primeiro os testes e depois o código. O histórico de commits mostra uma funcionalidade por vez, sempre com os testes junto.

| Tipo de teste | Ferramentas | O que cobre |
|---|---|---|
| Unitários de domínio | JUnit 5, AssertJ | Validações, normalização de nomes e ciclo de status |
| Unitários de casos de uso | Mockito | Regras de orquestração com as portas simuladas |
| Componentes web | `@WebMvcTest`, Spring Security Test | Rotas, validação, códigos HTTP e permissões por perfil |
| Componentes de persistência | `@DataJpaTest` | Gateways JPA, consultas e carga inicial |
| Arquitetura | ArchUnit | Direção das dependências entre camadas |
| Integração | `@SpringBootTest`, GreenMail | Login real, cadastro, recebimento, confirmação e retirada |
| Integração com mensageria | Testcontainers (RabbitMQ), Awaitility | Fluxo de ponta a ponta pelas filas reais e falha de envio indo para a DLQ |

### 4.2 Resultados

| Métrica | Resultado |
|---|---|
| Testes unitários e de componentes | 215, todos passando |
| Testes de integração | 18, todos passando |
| Cobertura de linhas (JaCoCo) | 99,8% (mínimo exigido no build: 80%) |
| Cobertura de ramos (JaCoCo) | 92,2% |
| Violações do Checkstyle | 0 |
| Problemas apontados pelo SpotBugs | 0 |

O comando `./mvnw verify` executa todos os testes e falha se a cobertura ficar abaixo do mínimo ou se o Checkstyle ou o SpotBugs encontrarem problemas. Os comandos para executar cada tipo de teste separadamente estão no README.

## 5. Desafios técnicos e soluções

### 5.1 Vincular a encomenda ao morador certo

O enunciado pede que o porteiro informe o nome do destinatário e o apartamento. O entregador pode escrever o nome com outra capitalização, sem acentos ou com espaços a mais.

**Solução:** o domínio normaliza o nome com decomposição Unicode (NFD), remove acentos, padroniza espaços e converte para minúsculas. O nome normalizado é gravado junto com o morador e indexado com o apartamento. O cadastro impede dois moradores com o mesmo nome no mesmo apartamento, para que o vínculo nunca seja ambíguo.

### 5.2 Mensageria confiável sem duplicar avisos

Uma mensagem pode ser entregue mais de uma vez, e o servidor de e-mail pode estar fora do ar.

**Solução:**
- **Idempotência:** existe no máximo uma notificação por encomenda, garantida por uma restrição única no banco e por uma verificação no caso de uso. Uma notificação já enviada não é reenviada.
- **Novas tentativas:** o consumidor tenta enviar o e-mail até 3 vezes, com intervalo crescente.
- **Dead letter queue:** esgotadas as tentativas, a mensagem vai para `notificacoes.saida.dlq`. Um consumidor dessa fila marca a notificação como `FALHA`, deixando o problema visível para consulta.

O teste `FalhaEnvioNotificacaoIT` reproduz esse cenário com um RabbitMQ real.

### 5.3 Transações sem acoplar os casos de uso ao Spring

Enviar a notificação exige atualizar duas entidades de forma atômica: a notificação e a encomenda. Usar `@Transactional` nos casos de uso quebraria a independência da camada de aplicação.

**Solução:** a porta `UnidadeDeTrabalho` recebe a ação a ser executada. Na infraestrutura, ela é implementada com o `TransactionTemplate` do Spring. O caso de uso continua sem depender do framework, e nos testes unitários a porta é simulada.

### 5.4 Segurança por perfil e por dono do recurso

Um morador não pode ver nem confirmar notificações de outro morador, e só o porteiro pode registrar encomendas ou dar baixa.

**Solução:**
- **Perfil no token:** as rotas são protegidas por perfil a partir da informação gravada no JWT.
- **Identidade vinda do token:** as rotas do próprio usuário (`/me`) usam o id do token, nunca um id da URL.
- **Recurso de outro morador:** a confirmação de uma notificação de outro morador responde 404, sem revelar que ela existe.

### 5.5 Carga inicial com H2 em memória

O banco em memória perde os dados a cada reinício. A carga inicial insere registros com ids fixos, o que deixaria o gerador de ids colidir com eles nos próximos cadastros.

**Solução:** o `schema.sql` cria as tabelas, e o `data.sql` insere 6 registros por tabela, cobrindo todos os status. Ao final, a carga reposiciona os geradores de id para começar em 7. O Hibernate apenas valida o mapeamento contra o schema, sem gerar tabelas sozinho.

### 5.6 Isolamento dos testes de integração

Vários contextos Spring na mesma execução de testes compartilhavam o mesmo banco em memória nomeado, e o segundo contexto tentava recriar tabelas que já existiam. Os consumidores do RabbitMQ também tentavam se conectar em testes que não precisavam de broker.

**Solução:**
- **Banco por contexto:** uma configuração exclusiva de testes dá a cada contexto um banco com nome aleatório.
- **Consumidores desligados:** os consumidores do RabbitMQ não iniciam por padrão nos testes. Os testes com Testcontainers os ligam explicitamente e são pulados automaticamente quando não há Docker disponível.

### 5.7 Adoção do Spring Boot 4

A versão 4 do Spring Boot trouxe mudanças em relação ao que é mais comum em materiais de referência:
- **Jackson 3:** o conversor de mensagens do Spring AMQP passou a ser o `JacksonJsonMessageConverter`.
- **Testes modulares:** as anotações de teste foram divididas em módulos próprios, como o `spring-boot-starter-webmvc-test` e o `spring-boot-starter-data-jpa-test`.
- **Propriedade renomeada:** a configuração de novas tentativas do RabbitMQ passou a se chamar `max-retries`.

Essas diferenças foram resolvidas consultando os próprios artefatos das bibliotecas e cobertas por testes de integração.

### 5.8 Horário correto dentro do container

O container usa UTC por padrão, e as datas de recebimento e retirada ficavam 3 horas adiantadas em relação ao horário de Brasília.

**Solução:** o docker-compose define o fuso `America/Sao_Paulo` para a JVM. Nos casos de uso, o horário vem de um `Clock` injetado, o que também permite testes com datas fixas.

## 6. Como executar e avaliar

```bash
docker compose up --build
```

- **Documentação da API:** fica em http://localhost:8080/swagger-ui/index.html.
- **E-mails enviados:** aparecem no Mailpit, em http://localhost:8025.
- **Filas:** podem ser acompanhadas no painel do RabbitMQ, em http://localhost:15672.
- **Credenciais da carga inicial:** os usuários e os comandos de teste estão no README.
