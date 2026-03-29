# Task Manager

Sistema de gerenciamento de tarefas com autenticação JWT, controle de acesso por perfis e interface moderna.

---

## Tecnologias

**Backend**
- Java 17
- Spring Boot 3
- Spring Security (JWT stateless)
- Spring Data JPA
- PostgreSQL
- Maven

**Frontend**
- Angular 17 (Standalone Components)
- Angular Material
- TypeScript

---

## Estrutura do Projeto

```
task-manager/
├── backend/                  # API REST Spring Boot
│   ├── src/
│   │   └── main/java/com/task_manager/demo/
│   │       ├── controllers/
│   │       ├── domain/
│   │       ├── dtos/
│   │       ├── enums/
│   │       ├── infra/security/
│   │       ├── repositories/
│   │       └── services/
│   └── pom.xml
├── frontend/                 # SPA Angular
│   ├── src/
│   │   └── app/
│   │       ├── core/
│   │       │   ├── guards/
│   │       │   ├── interceptors/
│   │       │   └── services/
│   │       ├── models/
│   │       └── pages/
│   │           ├── login/
│   │           ├── register/
│   │           └── tasks/
│   └── package.json
└── README.md
```

---

## Pré-requisitos

- Java 17+
- Maven 3.8+
- Node.js 18+
- Angular CLI 17: `npm install -g @angular/cli`
- PostgreSQL

---

## Configuração

### Backend

Edite o arquivo `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/task_manager
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update

api.security.token.secret=seu_secret_jwt
```

### Frontend

Edite o arquivo `frontend/src/environments/environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'
};
```

---

## Como Rodar

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

A API ficará disponível em: `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
ng serve
```

A aplicação ficará disponível em: `http://localhost:4200`

---

## Endpoints da API

### Autenticação

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| POST | `/auth/login` | Público | Realiza login e retorna JWT |
| POST | `/auth/register` | ADMIN | Cadastra novo usuário |

### Tarefas

| Método | Endpoint | Acesso | Descrição |
|--------|----------|--------|-----------|
| GET | `/tasks` | Autenticado | Lista todas as tarefas |
| GET | `/tasks/{id}` | Autenticado | Busca tarefa por ID |
| POST | `/tasks/new-task` | MANAGER | Cria nova tarefa |
| PUT | `/tasks/update-task/{id}` | MANAGER | Atualiza tarefa |
| DELETE | `/tasks/{id}` | MANAGER | Remove tarefa |

---

## Perfis de Acesso

| Perfil | Visualizar Tarefas | Criar/Editar/Deletar Tarefas | Cadastrar Usuários |
|--------|--------------------|------------------------------|--------------------|
| COLLABORATOR | ✅ | ❌ | ❌ |
| MANAGER | ✅ | ✅ | ❌ |
| ADMIN | ✅ | ✅ | ✅ |

---

## Autenticação

A autenticação é feita via **JWT (JSON Web Token)**. Após o login, o token é armazenado no `localStorage` do navegador e enviado automaticamente em todas as requisições protegidas via header `Authorization: Bearer <token>`.

O token expira em **2 horas**.

---

## Funcionalidades

- Login com validação de credenciais
- Cadastro de usuários (restrito a ADMIN)
- Listagem de tarefas em tabela
- Criação, edição e exclusão de tarefas (restrito a MANAGER e ADMIN)
- Interface adaptada por perfil — botões de ação visíveis apenas para quem tem permissão
- Logout com limpeza de sessão
- Redirecionamento automático para login quando não autenticado