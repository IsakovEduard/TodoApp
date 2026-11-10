# Errors

Single reference for error responses. Endpoint docs only list which codes they can return.
Code: `controller/exception/CustomExceptionHandler.java`, `security/jwt/SecurityConfig.java`.

## Body shape (JSON, `V1Error`)

`{ timestamp, status, error, message, path }`. Bean-validation failures replace `message` with `fieldErrors: { field: message }`.

## Mapping

| Status | Trigger | Body |
|---|---|---|
| 400 | `IllegalArgumentException` (includes `NumberFormatException` from a non-numeric id) | `V1Error`, message from exception |
| 400 | unreadable/malformed body, or bad enum value in JSON (e.g. urgency `URGENT`) | `V1Error`, message `Malformed or invalid request body` |
| 400 | `@Valid` failure (blank username/password) | `V1Error` with `fieldErrors` |
| 400 | duplicate username on register | **plain text** `Username already exists` (not JSON) |
| 401 | `UnauthorizedException` (wrong credentials on login) | `V1Error`, message `Invalid credentials` |
| 401 | missing, malformed, expired or wrongly signed token; token for a deleted user | **empty body** (security entry point, before controllers) |
| 404 | `ResourceNotFoundException`: task missing, soft-deleted, or owned by another user | `V1Error` |
| 404 | unknown path (`NoResourceFoundException`) | `V1Error`, message `Resource not found` |
| 500 | any other `RuntimeException` / `Exception` | `V1Error` |

## Rules

- A task of another user is indistinguishable from a missing one: always `404`, never `403`.
- Exceptions live in `com.todo.exception` and may be thrown from any layer; map new ones in `CustomExceptionHandler` and add them here.
- Handler order matters little: Spring picks the most specific `@ExceptionHandler`; `RuntimeException` and `Exception` are the catch-alls.
