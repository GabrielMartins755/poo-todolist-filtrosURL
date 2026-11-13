# API de Tarefas (todolist) — Spring Boot

Projeto bimestral de POO — IFPR Cascavel. API REST de tarefas organizada em **Controller → Service → Repository**, usando **DTOs** na entrada e na saída.

## Como executar

1. Ter um MySQL rodando com o banco `todolistif`.
2. Configurar `SPRING_DATASOURCE_USERNAME` com um usuário que tenha acesso ao banco e `SPRING_DATASOURCE_PASSWORD` com a senha desse usuário. Se a variável de usuário não for definida, será usado `todolist`. Não grave a senha no código nem a envie para o repositório.
3. Na pasta `todolist`: `./mvnw spring-boot:run` (no PowerShell, use `.\mvnw.cmd spring-boot:run`).
3. A API fica em `http://localhost:8080/api/todos`.

Para rodar os testes automatizados (usam banco H2 em memória, não precisam de MySQL): `./mvnw test`

## Documentação (Swagger)

Com a aplicação rodando:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`: dá para testar todos os endpoints pelo navegador.
- **OpenAPI (JSON):** `http://localhost:8080/v3/api-docs`

A documentação é gerada pelo `springdoc-openapi-starter-webmvc-ui` (versão 3.x, compatível com Spring Boot 4). Os textos vêm das anotações `@Tag`, `@Operation`, `@ApiResponse` e `@Parameter` do `TodoController` e do `@OpenAPIDefinition` da `TodolistApplication`.

## Estrutura

```
todolist/src/main/java/br/edu/ifpr/todolist/
├── controller/TodoController.java        → recebe requisições e define o status HTTP
├── service/TodoService.java              → regras (data de criação, conclusão, conversão DTO)
├── repository/TodoRepository.java        → acesso ao banco (JPA)
├── model/Todo.java                       → entidade (tabela "todos")
└── dto/
    ├── TodoRequestDTO.java               → entrada do cadastro (title, deadLine)
    ├── TodoResponseDTO.java              → resposta "atual", sem finishedAt
    ├── TodoConclusaoRequestDTO.java      → entrada da conclusão (finishedAt)
    └── TodoDetalhadoResponseDTO.java     → resposta completa, com finishedAt
```

## Caminho de uma requisição

Exemplo: `PATCH /api/todos/1/conclusao`

1. **Controller**: o `TodoController.concluir` recebe o `id` da URL e converte o JSON em `TodoConclusaoRequestDTO`. Ele não aplica nenhuma regra, só repassa para a Service.
2. **Service**: o `TodoService.concluir` busca a tarefa pelo Repository, altera **somente** `finishedAt`, salva e converte a entidade em `TodoDetalhadoResponseDTO`.
3. **Repository**: o `TodoRepository` faz o `SELECT` (`findById`) e o `UPDATE` (`save`) no banco.
4. **Controller**: devolve o DTO com status **200**.

## Solução adotada para os DTOs

**Problema:** o DTO de resposta atual (`TodoResponseDTO`) não tem `finishedAt`. Então (a) não existia como receber a data de conclusão e (b) o cliente não conseguia conferir a data registrada.

**Solução:** separar os DTOs por finalidade, em vez de colocar tudo num DTO só:

| DTO | Direção | Campos | Usado em |
|---|---|---|---|
| `TodoRequestDTO` | entrada | title, deadLine | POST /api/todos |
| `TodoResponseDTO` | saída | id, title, createdAt, deadLine | POST e GET /api/todos |
| `TodoConclusaoRequestDTO` | entrada | finishedAt | PATCH /{id}/conclusao |
| `TodoDetalhadoResponseDTO` | saída | id, title, createdAt, deadLine, **finishedAt** | PATCH /{id}/conclusao, GET /concluidas, GET /pendentes |

Por que assim:

- **`TodoResponseDTO` continua como estava.** Quem já usa a resposta do cadastro não é afetado.
- **`TodoConclusaoRequestDTO` só tem `finishedAt`.** O cliente não consegue alterar título, prazo nem datas do sistema por essa rota, e os outros dados ficam preservados.
- **`TodoDetalhadoResponseDTO` inclui `finishedAt`.** Assim o cliente confere a data registrada logo depois de concluir, e também nas listagens de concluídas/pendentes (nas pendentes, `finishedAt` aparece como `null`).
- **`TodoRequestDTO` não tem `id`, `createdAt` nem `finishedAt`.** Esses valores são definidos pelo sistema, na Service.

Os DTOs são `record`s do Java. Cada DTO de resposta tem um método `fromEntity(Todo)` que faz a conversão da entidade.

## Endpoints e exemplos

### 1. Inserir tarefa — `POST /api/todos` → 201

```json
{
  "title": "Estudar Spring Boot",
  "deadLine": "2026-10-30T18:00:00"
}
```

Resposta:

```json
{
  "id": 1,
  "title": "Estudar Spring Boot",
  "createdAt": "2026-11-05T07:50:12.345",
  "deadLine": "2026-10-30T18:00:00"
}
```

### 2. Listar todas — `GET /api/todos` → 200

Retorna uma lista de `TodoResponseDTO`.

### 3. Informar conclusão — `PATCH /api/todos/1/conclusao` → 200

```json
{
  "finishedAt": "2026-10-22T10:30:00"
}
```

Resposta (com a data registrada):

```json
{
  "id": 1,
  "title": "Estudar Spring Boot",
  "createdAt": "2026-11-05T07:50:12.345",
  "deadLine": "2026-10-30T18:00:00",
  "finishedAt": "2026-10-22T10:30:00"
}
```

### 4. Listar concluídas — `GET /api/todos/concluidas` → 200

Tarefas com `finishedAt` preenchido. Usa `findByFinishedAtIsNotNull()`. Sem resultados, retorna `[]`.

### 5. Listar pendentes — `GET /api/todos/pendentes` → 200

Tarefas com `finishedAt` nulo. Usa `findByFinishedAtIsNull()`. Sem resultados, retorna `[]`.

### 6. Excluir tarefa — `DELETE /api/todos/1` → 204 (sem corpo)

## Roteiro de testes (Thunder Client)

1. `POST /api/todos` duas vezes (ex.: "Estudar Spring Boot" e "Fazer trabalho de POO").
2. `PATCH /api/todos/1/conclusao` com `{"finishedAt": "2026-10-22T10:30:00"}`.
3. `GET /api/todos/concluidas` mostra só a tarefa 1. `GET /api/todos/pendentes` mostra só a tarefa 2.
4. Conferir `finishedAt` na resposta do PATCH e da listagem de concluídas.
5. `DELETE /api/todos/1` e depois `GET /api/todos`: a tarefa 1 não aparece mais.

Esse mesmo roteiro está automatizado em `todolist/src/test/java/br/edu/ifpr/todolist/TodoControllerTests.java`.

> Nesta etapa não há tratamento de exceções (IDs inexistentes ou dados inválidos). Isso será trabalhado em aula específica.
