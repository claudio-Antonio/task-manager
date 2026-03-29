# Task Manager Frontend — Angular 17

Frontend Angular para o backend Spring Boot 3 com autenticação JWT.

## Pré-requisitos

- Node.js 18+
- Angular CLI 17: `npm install -g @angular/cli`

## Instalação

```bash
npm install
```

## Executar em desenvolvimento

```bash
ng serve
```

Acesse: http://localhost:4200

## Build para produção

```bash
ng build
```

---

## Estrutura do projeto

```
src/app/
├── core/
│   ├── guards/
│   │   └── auth.guard.ts          # Protege rotas autenticadas
│   ├── interceptors/
│   │   └── auth.interceptor.ts    # Injeta Bearer token em toda requisição
│   └── services/
│       ├── auth.service.ts        # Login, register, logout, decode JWT
│       └── task.service.ts        # CRUD de tarefas
├── models/
│   └── index.ts                   # Interfaces e tipos (DTOs)
├── pages/
│   ├── login/
│   │   └── login.component.ts
│   ├── register/
│   │   └── register.component.ts
│   └── tasks/
│       └── tasks.component.ts     # Dashboard + dialog de criação/edição
├── app.component.ts
├── app.config.ts
└── app.routes.ts
```

---

## Configuração da API

Edite `src/environments/environment.ts`:

```ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080'  // URL do seu backend Spring Boot
};
```

---

## CORS no Spring Boot

Adicione esta configuração no seu backend para permitir requisições do Angular:

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*");
    }
}
```

---

## Rotas

| Rota        | Descrição                              | Guard       |
|-------------|----------------------------------------|-------------|
| `/login`    | Tela de login                          | —           |
| `/register` | Tela de cadastro                       | —           |
| `/tasks`    | Dashboard de tarefas (CRUD)            | authGuard   |

---

## Permissões

| Ação                  | USER | MANAGER | ADMIN |
|-----------------------|------|---------|-------|
| Ver tarefas           | ✅   | ✅      | ✅    |
| Criar tarefa          | ❌   | ✅      | ✅    |
| Editar tarefa         | ❌   | ✅      | ✅    |
| Excluir tarefa        | ❌   | ✅      | ✅    |
