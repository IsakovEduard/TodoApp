# Docs index

Start here. Each file is self-contained: load only the one the task needs. Facts live in one place; other files link to it.
Source of truth for request/response schemas: `src/main/java/com/todo/controller/api/task_management.yaml` (docs do not repeat field lists).

## Which file to read

| Task touches | Read |
|---|---|
| register, login, `/auth/me`, JWT, token expiry, security rules | [api/auth.md](api/auth.md) |
| create, get, list/filter, update, delete tasks; cache; soft delete | [api/tasks.md](api/tasks.md) |
| an HTTP status, error body or exception mapping | [api/errors.md](api/errors.md) |
| layers, packages, request flow, where to add code, UI hosting, config | [architecture.md](architecture.md) |

## Endpoint map

| Method | Path | Auth | Purpose | Doc section |
|---|---|---|---|---|
| POST | `/auth/register` | public | create a user | auth.md#register |
| POST | `/auth/login` | public | exchange credentials for a JWT | auth.md#login |
| GET | `/auth/me` | JWT | current username | auth.md#me |
| POST | `/task-management` | JWT | create a task | tasks.md#create |
| GET | `/task-management` | JWT | list own active tasks, optional filters | tasks.md#list |
| GET | `/task-management/{taskId}` | JWT | get one task (cached) | tasks.md#get |
| PUT | `/task-management` | JWT | change fields of one task | tasks.md#update |
| DELETE | `/task-management` | JWT | soft-delete tasks | tasks.md#delete |

## Conventions used in these docs

- Page order per endpoint: Purpose, Contract, Rules and limits, Flow (Mermaid), Code.
- "JWT" = header `Authorization: Bearer <token>`. Without a valid one the answer is `401` (see errors.md).
- Task ids are numeric strings. Layer names: C = controller, D = delegate, S = application service, R = repository.
- Docs are verified against code and tests. When behavior changes, update the matching doc in the same commit.
