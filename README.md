# eBuffet — Sistema de Gerenciamento Centralizado de Buffets e Eventos (Back-end)

O **eBuffet** é um aplicativo móvel para Android destinado ao gerenciamento centralizado de buffets e eventos, no qual clientes realizam reservas de forma estruturada e os responsáveis pelo buffet administram reservas, eventos, comidas, serviços e identidade visual em um único ambiente digital.
O back-end foi desenvolvido em Java com Spring Boot, expondo os serviços por meio de uma API RESTful que serve como base do aplicativo móvel. A autenticação é baseada em JWT, dispensando gerenciamento de sessões no servidor; o contexto de tenant é identificado em cada requisição pelo header `X-Buffet-Id`, garantindo que cada buffet opere com autonomia sobre seus próprios dados e serviços.
Entre as funcionalidades implementadas destacam-se autenticação baseada em JWT, gerenciamento de comidas e serviços com upload de imagens via Cloudinary, criação e aprovação de reservas com geração automática de eventos, envio de notificações de estado e recuperação de senha por e-mail. A arquitetura multi-tenant permite que diferentes buffets operem sobre a mesma base tecnológica sem necessidade de instâncias separadas.

---

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.4.4
- Spring Security (JWT com Auth0 java-jwt)
- Spring Data JPA / Hibernate
- PostgreSQL 15+
- Flyway (migrations)
- Cloudinary (upload de imagens)
- Spring Mail (SMTP Gmail)
- Springdoc OpenAPI / Swagger UI
- JasperReports
- Lombok
- Maven

---

## Perfis de Usuários

O sistema contempla dois perfis com responsabilidades distintas. Não existe administrador global, cada buffet gerencia exclusivamente seus próprios dados.

**Cliente:** usuário final que realiza reservas no buffet. Consulta o catálogo de comidas e serviços, cria reservas informando data, horário, quantidade de pessoas, endereço e itens selecionados, edita ou cancela reservas no estado *pendente*, e acompanha o andamento de suas reservas e eventos por meio de notificações automáticas.

**Buffet:** responsável pelo estabelecimento e pela oferta de serviços para eventos. Gerencia o cadastro de comidas e serviços com imagens, analisa reservas recebidas decidindo pela aprovação (com valor estimado) ou recusa, e administra o ciclo de vida dos eventos originados de reservas aprovadas. Cada buffet acessa e gerencia exclusivamente seus próprios dados, garantindo isolamento lógico no ambiente multi-tenant.

---

## Estrutura do Sistema

### Arquitetura

```
src/main/java/com/ebuffet/
├── config/               # Configurações de segurança e integrações
│   ├── CloudinaryConfig.java
│   ├── JwtAuthenticationFilter.java
│   └── SecurityConfig.java
├── controller/           # Camada REST (10 controllers)
│   ├── AuthController.java
│   ├── BuffetController.java
│   ├── ClienteController.java
│   ├── ComidaController.java
│   ├── EventoController.java
│   ├── NotificacaoController.java
│   ├── PasswordRecoveryController.java
│   ├── ReservaBuffetController.java
│   ├── ReservaClienteController.java
│   ├── ServicoController.java
│   ├── dto/              # Data Transfer Objects por domínio
│   └── exceptions/       # Handlers de exceções HTTP
├── models/               # Entidades JPA (11 entidades)
│   ├── enums/            # Enumerações de domínio
│   └── BaseEntity.java   # Auditoria automática de datas e status
├── repository/           # Repositórios Spring Data JPA
├── service/              # Regras de negócio (12 serviços)
│   └── impl/             # Implementações concretas
└── utils/
    ├── filters/          # BuffetContextFilter (header X-Buffet-Id)
    ├── schedules/        # Tarefas agendadas (expiração)
    └── security/         # JwtUtil, SecurityUtils, BuffetContext
```

### Camada de Controllers

| Controller                   | Rota base               | Ator           | Responsabilidade                                            |
|------------------------------|-------------------------|----------------|-------------------------------------------------------------|
| `AuthController`             | `/api/auth`             | Público        | Login, cadastro e dados do usuário autenticado              |
| `PasswordRecoveryController` | `/api/password`         | Público        | Solicitar, verificar e redefinir senha por e-mail           |
| `BuffetController`           | `/api/buffets`          | Buffet         | CRUD do perfil do buffet                                    |
| `ClienteController`          | `/api/clientes`         | Cliente        | Atualização do perfil do cliente                            |
| `ComidaController`           | `/api/comidas`          | Cliente/Buffet | Listagem (cliente) e CRUD com imagem (buffet)               |
| `ServicoController`          | `/api/servicos`         | Cliente/Buffet | Listagem (cliente) e CRUD com imagem (buffet)               |
| `ReservaClienteController`   | `/api/reservas/cliente` | Cliente        | Criar, editar, listar e cancelar reservas                   |
| `ReservaBuffetController`    | `/api/reservas/buffet`  | Buffet         | Listar, aprovar, recusar e cancelar reservas                |
| `EventoController`           | `/api/eventos`          | Cliente/Buffet | Gerenciar eventos, datas bloqueadas e transições de estado  |
| `NotificacaoController`      | `/api/notificacoes`     | Cliente/Buffet | Listar, marcar como lida e excluir notificações             |

### Entidades e Relacionamentos

| Entidade            | Campos principais                                                              | Relacionamentos                                                              |
|---------------------|--------------------------------------------------------------------------------|------------------------------------------------------------------------------|
| `User`              | nome, email, telefone, senha, role, buffetId, foto                             | OneToMany → Reserva                                                          |
| `Buffet`            | nome, telefone, email, owner                                                   | OneToOne → Endereco; OneToMany → Comida, Servico, Evento                     |
| `Reserva`           | statusReserva, qtdPessoas, horarioDesejado, dataDesejada, titulo, descricao    | ManyToOne → User, Buffet; ManyToMany → Comida, Servico; OneToOne → Evento, Endereco |
| `Evento`            | statusEvento, valor, dataEvento, horaEvento, bloquearCalendario, descricao     | OneToOne → Reserva; ManyToOne → Buffet                                       |
| `Comida`            | nome, descricao, categoria, imagem                                             | ManyToOne → Buffet; ManyToOne → Arquivo                                      |
| `Servico`           | nome, descricao, categoria, imagem                                             | ManyToOne → Buffet; ManyToOne → Arquivo                                      |
| `Endereco`          | rua, numero, bairro, cidade, estado, cep, complemento                          | —                                                                            |
| `Arquivo`           | tipo, url                                                                      | Referência a imagem hospedada no Cloudinary                                  |
| `Notificacao`       | titulo, mensagem, lida, status, dataCriacao                                    | ManyToOne → User; ManyToOne → Reserva                                        |
| `PasswordResetCode` | código temporário de redefinição de senha                                      | —                                                                            |

### Estados de Reservas e Eventos

| Entidade | Estados possíveis                        | Transições                                                                                  |
|----------|------------------------------------------|---------------------------------------------------------------------------------------------|
| Reserva  | `PENDENTE` → `APROVADA` ou `CANCELADA`   | Buffet aprova (com valor estimado) ou recusa; Cliente cancela enquanto *pendente*           |
| Evento   | `AGENDADO` → `CONCLUIDO` ou `CANCELADO`  | Buffet conclui ou cancela; Buffet pode reverter cancelamento; Cliente pode cancelar         |

Toda reserva aprovada é automaticamente convertida em um evento com estado *agendado*.

### Segurança e Autenticação

- **Autenticação:** JWT Bearer token gerado no login e validado em cada requisição via `JwtAuthenticationFilter`
- **Multi-tenant:** `BuffetContextFilter` extrai o `X-Buffet-Id` do header e o injeta no `BuffetContext` para isolar os dados de cada buffet
- **Sessão:** STATELESS — sem estado no servidor; toda informação de contexto é extraída do token e dos headers
- **CORS:** configurado para `http://localhost:4200` (Angular), `http://localhost:8100` (Ionic) e túneis ngrok
- **Senhas:** armazenadas com hash `BCryptPasswordEncoder`

### Sistema de Notificações

Notificações são geradas automaticamente pelo `NotificacaoService` nos seguintes eventos:

- Nova reserva criada → notifica o Buffet
- Reserva aprovada → notifica o Cliente
- Reserva recusada → notifica o Cliente
- Reserva cancelada por qualquer parte → notifica o outro ator
- Evento concluído ou cancelado → notifica o Cliente

---

## Endpoints da API

### Autenticação (`/api/auth`)

| Método | Rota               | Ator           | Descrição                                 |
|--------|--------------------|----------------|-------------------------------------------|
| POST   | `/login`           | Público        | Autenticação e obtenção do token JWT      |
| POST   | `/register`        | Público        | Cadastro de novo cliente                  |
| POST   | `/register/buffet` | Público        | Cadastro de novo buffet                   |
| GET    | `/me`              | Cliente/Buffet | Dados do usuário autenticado              |

### Recuperação de Senha (`/api/password`)

| Método | Rota           | Descrição                                   |
|--------|----------------|---------------------------------------------|
| POST   | `/forgot`      | Solicitar envio de código de verificação    |
| POST   | `/verify-code` | Validar código recebido por e-mail          |
| POST   | `/reset`       | Redefinir senha com o código verificado     |

### Comidas (`/api/comidas`)

| Método | Rota    | Ator    | Descrição                              |
|--------|---------|---------|----------------------------------------|
| GET    | `/`     | Cliente | Listar comidas disponíveis do buffet   |
| POST   | `/`     | Buffet  | Cadastrar nova comida (multipart)      |
| PUT    | `/{id}` | Buffet  | Atualizar comida existente (multipart) |
| DELETE | `/{id}` | Buffet  | Remover (inativar) comida              |

### Serviços (`/api/servicos`)

| Método | Rota    | Ator    | Descrição                               |
|--------|---------|---------|-----------------------------------------|
| GET    | `/`     | Cliente | Listar serviços disponíveis do buffet   |
| POST   | `/`     | Buffet  | Cadastrar novo serviço (multipart)      |
| PUT    | `/{id}` | Buffet  | Atualizar serviço existente (multipart) |
| DELETE | `/{id}` | Buffet  | Remover (inativar) serviço              |

### Reservas — visão do Cliente (`/api/reservas/cliente`)

| Método | Rota             | Descrição                                       |
|--------|------------------|-------------------------------------------------|
| POST   | `/`              | Criar reserva com comidas, serviços e endereço  |
| GET    | `/me`            | Listar reservas do cliente autenticado          |
| GET    | `/{id}`          | Detalhar reserva                                |
| PUT    | `/{id}`          | Editar reserva no estado *pendente*             |
| PUT    | `/{id}/cancelar` | Cancelar reserva                                |

### Reservas — visão do Buffet (`/api/reservas/buffet`)

| Método | Rota                          | Descrição                           |
|--------|-------------------------------|-------------------------------------|
| GET    | `/`                           | Listar reservas recebidas           |
| PUT    | `/aprovar/{id}`               | Aprovar reserva com valor estimado  |
| PUT    | `/recusar/{id}`               | Recusar reserva                     |
| PUT    | `/cancelar/{id}`              | Cancelar reserva                    |
| PUT    | `/reverter-cancelamento/{id}` | Reverter cancelamento               |

### Eventos (`/api/eventos`)

| Método | Rota                          | Ator           | Descrição                               |
|--------|-------------------------------|----------------|-----------------------------------------|
| GET    | `/`                           | Cliente/Buffet | Listar todos os eventos do tenant       |
| GET    | `/me`                         | Cliente/Buffet | Listar eventos do usuário autenticado   |
| GET    | `/{id}`                       | Cliente/Buffet | Detalhar evento                         |
| GET    | `/datas-indisponiveis`        | Público        | Datas bloqueadas no calendário          |
| PUT    | `/{id}/valor`                 | Buffet         | Atualizar valor do evento               |
| PUT    | `/{id}/concluir`              | Buffet         | Marcar evento como concluído            |
| PUT    | `/{id}/cancelar`              | Buffet         | Cancelar evento                         |
| PUT    | `/{id}/reverter-cancelamento` | Buffet         | Reverter cancelamento de evento         |
| PUT    | `/{id}/cliente`               | Cliente        | Editar dados do evento                  |
| PUT    | `/{id}/cliente/cancelar`      | Cliente        | Cancelar evento                         |
| DELETE | `/{id}`                       | Buffet         | Remover evento                          |

### Notificações (`/api/notificacoes`)

| Método | Rota                 | Descrição                        |
|--------|----------------------|----------------------------------|
| GET    | `/`                  | Listar notificações do usuário   |
| GET    | `/unread-count`      | Contar notificações não lidas    |
| PUT    | `/{id}/mark-as-read` | Marcar notificação como lida     |
| DELETE | `/{id}`              | Excluir notificação              |

---

## Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 15+
- Conta Cloudinary (para upload de imagens)
- Conta Gmail com senha de aplicativo habilitada (para envio de e-mails)

### Configuração do banco de dados

```sql
CREATE DATABASE ebuffet;
```

### Configuração (application.properties)

```properties
# Banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/ebuffet
spring.datasource.username=postgres
spring.datasource.password=sua_senha

# JWT
jwt.secret=seu_secret_jwt

# Cloudinary
cloudinary.cloud_name=seu_cloud_name
cloudinary.api_key=sua_api_key
cloudinary.api_secret=seu_api_secret

# E-mail
spring.mail.username=seu_email@gmail.com
spring.mail.password=sua_senha_de_app
```

### Instalação e execução

```bash
# Clonar o repositório
git clone https://github.com/seu-usuario/ebuffet-backend.git
cd ebuffet-backend

# Compilar e executar
./mvnw spring-boot:run

# Build para produção
./mvnw clean package -DskipTests

# Executar o JAR gerado
java -jar target/ebuffet-backend-*.jar
```

As migrations do Flyway são executadas automaticamente na inicialização. A API ficará disponível em `http://localhost:8080`.

A documentação interativa Swagger UI pode ser acessada em `http://localhost:8080/swagger-ui.html`.

### Exemplos de uso

- Autenticação
- POST
- http://localhost:8080/api/auth/login
- Headers: `X-Buffet-Id: 1`
- Body JSON: `{ "email": "maria.santos@email.com", "senha": "cliente123" }`
- Resposta esperada: `{ "token": "eyJhbGciOi...", "tipo": "CLIENTE" }`
-------------------------------------------------------------
- Criar reserva (Cliente)
- POST
- http://localhost:8080/api/reservas/cliente
- Headers: `X-Buffet-Id: 1`, `Authorization: Bearer {token}`
- Body JSON: `{ "titulo": "Aniversário", "dataDesejada": "2026-06-20", "horarioDesejado": "18:00", "qtdPessoas": 50, "descricao": "Festa de aniversário", "endereco": { "rua": "Rua das Flores", "numero": "123", "bairro": "Centro", "cidade": "Goiânia", "estado": "GO", "cep": "74000-000" }, "comidas": [1, 2], "servicos": [1] }`
- Resposta esperada: `{ "id": 10, "statusReserva": "PENDENTE", "dataDesejada": "2026-06-20" }`
-------------------------------------------------------------
- Aprovar reserva (Buffet)
- PUT
- http://localhost:8080/api/reservas/buffet/aprovar/10
- Headers: `X-Buffet-Id: 1`, `Authorization: Bearer {token}`
- Body JSON: `{ "valorEstimado": 3500.00 }`
- Resposta esperada: `{ "id": 10, "statusReserva": "APROVADA", "evento": { "id": 5, "statusEvento": "AGENDADO" } }`
-------------------------------------------------------------
- Listar eventos (Buffet)
- GET
- http://localhost:8080/api/eventos
- Headers: `X-Buffet-Id: 1`, `Authorization: Bearer {token}`
- Resposta esperada: `[{ "id": 5, "statusEvento": "AGENDADO", "valor": 3500.00, "dataEvento": "2026-06-20" }]`
-------------------------------------------------------------
- Datas indisponíveis (calendário)
- GET
- http://localhost:8080/api/eventos/datas-indisponiveis
- Headers: `X-Buffet-Id: 1`
- Resposta esperada: `["2026-06-20", "2026-07-05"]`
