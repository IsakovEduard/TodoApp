# CLAUDE.md

@README.md

## Structure (`src/main/java/com/todo`)

- `controller/` – REST controllers (`TaskControllerImpl`, `AuthControllerImpl`), `exception/` handler, `api/task_management.yaml` (OpenAPI spec, source of truth for the task API)
- `business/` – `service/` (application services, one per use case), `domain/`, `model/` (DTOs), `config/` (cache)
- `resource/` – `delegate/` (input validation and request/response mapping between controller and services), `mapper/`
- `repository/` – JPA `entity/`, `mapper/`, `service/` (repositories)
- `security/` – JWT filter/util, security config, auth DTOs

## Conventions and gotchas

- **Contract-first:** `controller/api/interfaces` and `controller/api/model` are generated from the YAML by `openapi-generator-maven-plugin` at build time. Change the YAML, not generated code. Custom model template: `src/main/resources/openapi-templates/model.mustache`.
- **Dependency injection:** use Spring stereotypes with constructor injection and `private final` fields (no field injection, no `@Inject`/`@Autowired`). `@Service` for application/domain services, `@Component` for delegates and mappers, `@Repository` for repository implementations. Beans are found by component scan; there is no `spring.factories`. Keep `@Configuration` classes only for third-party beans (`CacheConfig`, `PasswordConfig`, `SecurityConfig`).
- **Data objects are not beans:** create DTOs, `Filter`, `PatchElement`, `Characteristic` and entities with `new`.
- **One application service per use case**, with an `I`-prefixed interface (e.g. `IGetTaskByIdApplicationService`).
- **User scoping:** resolve the current user via `IGetUserServiceFromContextService`; task queries must filter by user id.
- **Cache:** Caffeine cache `tasks` (`CacheConfig`), keyed by task id via `@CachePut`/`@Cacheable`. Keep it consistent when changing task read/write paths.
- **Task updates:** `UpdateTaskCharacteristicsApplicationService` accepts only TITLE, DESCRIPTION, DUE_DATE, CREATED_AT, URGENCY, STATUS; anything else throws `IllegalArgumentException`.
- **Config:** `application.properties` points H2 at `/app/data/todo-db` (Docker path); override `spring.datasource.url` for local runs.

## Commands

- Build/test: `./mvnw clean verify`
- Run: `./mvnw spring-boot:run` or `docker compose up --build`
- Manual API requests: `src/main/resources/test.http`
- `out/` is IDE build output; don't commit it.
