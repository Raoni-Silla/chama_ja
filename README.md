# ChamaJá

> Plataforma web para intermediação de serviços locais, conectando clientes e prestadores com busca por proximidade, negociação e comunicação em tempo real.

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.13-6DB33F?logo=springboot&logoColor=white" alt="Spring Boot 3.5.13" />
  <img src="https://img.shields.io/badge/Angular-21-DD0031?logo=angular&logoColor=white" alt="Angular 21" />
  <img src="https://img.shields.io/badge/PostgreSQL-PostGIS-4169E1?logo=postgresql&logoColor=white" alt="PostgreSQL + PostGIS" />
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-yellow" alt="Status: em desenvolvimento" />
</p>

## Sobre o projeto

O **ChamaJá** é uma aplicação full stack desenvolvida como Trabalho de Conclusão de Curso em Análise e Desenvolvimento de Sistemas. A proposta nasceu da dificuldade de encontrar prestadores de serviços locais de forma organizada, especialmente quando a busca depende apenas de indicações, redes sociais ou grupos de mensagens.

A plataforma centraliza o processo de descoberta e negociação: o cliente pode localizar profissionais próximos, consultar informações do prestador, solicitar um orçamento, receber propostas e conversar em tempo real dentro do próprio sistema.

O projeto também foi utilizado como ambiente prático para aprofundar conceitos de **arquitetura cliente-servidor, segurança com JWT, persistência relacional, consultas geográficas, WebSocket/STOMP e integração com APIs externas**.

## Principais funcionalidades

### Implementadas

* Cadastro de clientes em múltiplas etapas.
* Validação de telefone por SMS com **Twilio**.
* Autenticação e autorização com **Spring Security, JWT e BCrypt**.
* Controle de acesso baseado no perfil do usuário.
* Gerenciamento de endereços e geolocalização.
* Busca textual e geográfica de prestadores.
* Filtro por raio de distância utilizando **PostgreSQL + PostGIS**.
* Solicitação direta de orçamento para um prestador.
* Fluxo de interação e criação de chamados.
* Envio, validação e aceite de propostas.
* Chat em tempo real utilizando **WebSocket + STOMP**.
* Persistência do histórico de mensagens.
* Cadastro, listagem e exclusão de cartões via **Mercado Pago** em ambiente de testes.
* Interface responsiva construída com **Angular + PrimeNG**.

### Em evolução

* Finalização completa da visão do prestador.
* Encerramento do serviço mediante confirmação de cliente e prestador.
* Envio de imagens e outros conteúdos pelo chat.
* Paginação dos resultados da busca.
* Fluxo completo de pagamento em ambiente de testes.
* Feed público de oportunidades.
* Carteira e recebimentos do prestador.
* Avaliação entre usuários.
* Agenda e acompanhamento completo dos serviços.
* Área administrativa.

## Destaques técnicos

### Busca geográfica com PostGIS

Os endereços armazenam latitude e longitude e são convertidos em uma representação geográfica utilizada diretamente pelo PostgreSQL/PostGIS.

A busca considera a localização do cliente e o raio configurado, retornando os profissionais compatíveis juntamente com a distância aproximada.

A implementação utiliza recursos espaciais do PostGIS, incluindo **ST_DWithin** e índice espacial **GiST**, permitindo filtrar prestadores por proximidade antes de retornar os dados completos para a aplicação.

### Autenticação e controle de acesso

A API utiliza uma política **stateless**. Após a autenticação, o cliente recebe um JWT que acompanha as requisições destinadas a recursos protegidos.

O controle de acesso combina:

* Spring Security;
* JWT;
* BCrypt para armazenamento seguro de senhas;
* interceptação das requisições HTTP no front-end;
* validação do token no back-end;
* autorização por perfil/role com `@PreAuthorize`.

### Chat em tempo real

Após o início de um chamado, cliente e prestador podem trocar mensagens em tempo real.

O fluxo utiliza:

* **WebSocket** para manter a comunicação bidirecional;
* **STOMP** para organização das mensagens e tópicos;
* JWT durante o `CONNECT` para autenticação da conexão;
* persistência das mensagens no PostgreSQL;
* HTTP para recuperar o histórico já armazenado;
* WebSocket/STOMP para distribuir apenas as novas mensagens em tempo real.

### Integrações externas

| Serviço          | Uso                                                        |
| ---------------- | ---------------------------------------------------------- |
| **Twilio**       | Envio do código de validação por SMS                       |
| **Geoapify**     | Pesquisa, obtenção e tratamento de informações geográficas |
| **Mercado Pago** | Cadastro e gerenciamento de cartões em ambiente de testes  |

No fluxo de cartões, a aplicação não mantém localmente o número completo nem o código de segurança. São armazenadas apenas informações necessárias para identificação do meio de pagamento, enquanto os dados sensíveis permanecem associados ao serviço externo.

## Arquitetura

A aplicação segue uma arquitetura **cliente-servidor**, mantendo front-end e back-end separados.

No fluxo HTTP convencional, o Angular envia DTOs para a API Spring Boot. O back-end organiza o processamento em camadas de **Controller, Service e Repository**, com Spring Data JPA realizando o acesso ao PostgreSQL.

O chat utiliza um fluxo paralelo baseado em WebSocket/STOMP, também protegido por autenticação JWT.

<p align="center">
  <img src="docs/screenshots/arquitetura.jpg" alt="Arquitetura geral do ChamaJá" width="900" />
</p>

## Tecnologias

### Back-end

* Java 21
* Spring Boot 3.5.13
* Spring Web
* Spring Data JPA / Hibernate
* Spring Security
* JWT
* WebSocket
* STOMP
* PostgreSQL
* PostGIS
* Maven
* Lombok
* Springdoc OpenAPI / Swagger
* Twilio SDK
* Mercado Pago SDK

### Front-end

* Angular 21
* TypeScript
* PrimeNG
* PrimeIcons
* STOMP.js
* JWT Decode
* Angular HttpClient
* HTML
* CSS

### Banco e ferramentas

* PostgreSQL
* PostGIS
* consultas espaciais
* índice GiST
* Git / GitHub

## Organização do repositório

```text
chama_ja/
├── br.com.chamaja/      # API REST / WebSocket - Spring Boot
├── chamajafront/        # Aplicação web - Angular
└── docs/
    └── screenshots/     # Imagens utilizadas neste README
```

No back-end, a aplicação utiliza separação de responsabilidades entre camadas:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL / PostGIS
```

DTOs são utilizados na comunicação entre cliente e servidor para evitar a exposição direta das entidades persistidas.

## Screenshots

### Busca de prestadores

<p align="center">
  <img src="docs/screenshots/busca-prestadores.jpg" alt="Busca de prestadores no ChamaJá" width="900" />
</p>

### Autenticação

<p align="center">
  <img src="docs/screenshots/login.jpg" alt="Tela de login do ChamaJá" width="850" />
</p>

### Chat em tempo real

<p align="center">
  <img src="docs/screenshots/chat.jpg" alt="Chat em tempo real do ChamaJá" width="900" />
</p>

## Como executar localmente

### Pré-requisitos

Antes de iniciar, tenha instalado:

* **Java 21**;
* **Node.js** compatível com Angular 21;
* **npm**;
* **PostgreSQL**;
* extensão **PostGIS** habilitada no PostgreSQL.

O projeto inclui o Maven Wrapper (`mvnw`), portanto uma instalação global do Maven não é obrigatória.

### 1. Clone o repositório

```bash
git clone https://github.com/Raoni-Silla/chama_ja.git
cd chama_ja
```

### 2. Configure o banco de dados

Crie o banco utilizado pela aplicação:

```sql
CREATE DATABASE chamaja_db;
```

Conectado ao banco `chamaja_db`, habilite o PostGIS:

```sql
CREATE EXTENSION IF NOT EXISTS postgis;
```

Ajuste usuário, senha ou URL do datasource em:

```text
br.com.chamaja/src/main/resources/application.properties
```

### 3. Configure as integrações externas

O back-end lê as seguintes variáveis de ambiente:

```bash
export TWILIO_ACCOUNT_SID="seu_sid"
export TWILIO_ACCOUNT_TOKEN="seu_token"
export TWILIO_ACCOUNT_NUMBER="seu_numero_twilio"
export MERCADO_PAGO_TOKEN="seu_token_mercado_pago"
```

A integração com **Geoapify** também exige uma chave válida na configuração utilizada pelo front-end.

> Alguns fluxos, como validação por SMS, geolocalização e gerenciamento de cartões, dependem das respectivas credenciais externas para funcionar integralmente.

### 4. Execute o back-end

```bash
cd br.com.chamaja
./mvnw spring-boot:run
```

Por padrão:

```text
http://localhost:8080
```

### 5. Execute o front-end

Em outro terminal:

```bash
cd chamajafront
npm install
npm start
```

A aplicação Angular ficará disponível em:

```text
http://localhost:4200
```

## Status do projeto

O ChamaJá está **em desenvolvimento**. Uma parcela significativa dos fluxos centrais já está funcional, enquanto módulos adicionais continuam sendo implementados e refinados.

| Módulo                            | Situação             |
| --------------------------------- | -------------------- |
| Cadastro de clientes              | ✅ Implementado       |
| Cadastro completo de prestadores  | 🟡 Parcial           |
| Autenticação e controle de acesso | ✅ Implementado       |
| Endereços e geolocalização        | ✅ Implementado       |
| Busca geográfica                  | ✅ Implementado       |
| Solicitação direta de orçamento   | ✅ Implementado       |
| Chat em tempo real                | ✅ Implementado       |
| Negociação e propostas            | ✅ Implementado       |
| Gerenciamento de cartões          | ✅ Ambiente de testes |
| Encerramento bilateral do serviço | 🟡 Parcial           |
| Pagamentos completos / retenção   | 🔵 Planejado         |
| Avaliações                        | 🔵 Planejado         |
| Área administrativa               | 🔵 Planejado         |

## Contexto acadêmico

Projeto desenvolvido como **Trabalho de Conclusão de Curso (TCC)** do curso de **Análise e Desenvolvimento de Sistemas**, FEMA / IMESA, em 2026.

O objetivo acadêmico do projeto é aplicar conceitos de desenvolvimento web em uma solução full stack que integra segurança, persistência de dados, geolocalização, comunicação em tempo real e serviços externos.

## Autor

**Raoní Mendes Silla**

* GitHub: [@Raoni-Silla](https://github.com/Raoni-Silla)
* LinkedIn: [Raoní Mendes Silla](https://www.linkedin.com/in/raon%C3%AD-mendes-silla-14a2122b5/)

---

<p align="center">
  Desenvolvido como projeto acadêmico e portfólio de desenvolvimento Full Stack com foco em Java/Spring Boot.
</p>
