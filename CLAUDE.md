# CLAUDE.md

@README.md

## Coding standards (load on demand)

Read `instructions/CODING_STANDARDS.md` **before** writing, modifying, refactoring or reviewing Java code or tests, and apply it. Do not read it for tasks that don't touch code quality: answering questions, explaining code, git operations, documentation-only edits, or running builds and tests.

## Workflow rules

- **Present choices as a selectable prompt:** whenever the user must pick between a few options (approaches, scope, commit approval, which item to do next), ask with `AskUserQuestion` so the choices are visible and selectable. Do not bury options in prose or end with an open "which do you prefer?". Put the recommended option first, marked "(Recommended)", and keep a free-text answer available through the built-in "Other" choice. Plain text questions are fine only for open-ended input that has no fixed options.
- **Quick code review after tests pass:** once the build and tests pass, review the diff before proposing a commit. Check three things: (1) the change matches the task and every stated requirement is fulfilled, with nothing missing and nothing extra; (2) no dead code is left (unused classes, methods, fields, imports, config, files or tests made obsolete by the change); (3) the code follows `instructions/CODING_STANDARDS.md`. Report findings with an explicit severity so the difference is clear:
  - **Potential bug / unexpected behavior:** the change may behave incorrectly or differently from what was intended (state the scenario and what goes wrong). Fix it, or flag it and let the user decide, before committing.
  - **Minor issue:** style, naming, small cleanup or a standards deviation with no behavior impact.
  - Report only findings that need attention. Do not list checks that passed (no "Dead code: none"); if nothing is found, say nothing about the review beyond a brief "Review: no issues". Do not hide or soften a potential bug among minor items.
- **Approval before commit:** approval to make a change is not approval to commit it. When work is ready, run the build and tests, do the quick review, summarize the changes (files and intent, `git diff --stat`, review findings) and propose the commit message. Then ask the user with `AskUserQuestion`, offering:
  1. **Approve this commit** – commit now; ask again for the next commit.
  2. **Auto-approve commits for this session** – commit without asking again until the session ends. Still run the build and tests first, and report what was committed afterwards.
  3. **Let me review first** – do not commit; wait for the user.
- Auto-approval lasts only for the current session and is never assumed in a new one. It covers committing only, never pushing, amending, resetting or branch changes. If tests fail, do not commit even under auto-approval; report instead.

## Structure (`src/main/java/com/todo`)

- `controller/` – REST controllers (`TaskControllerImpl`, `AuthControllerImpl`), `exception/` handler, `api/task_management.yaml` (OpenAPI spec, source of truth for the task API)
- `business/` – `service/` (application services, one per use case), `domain/`, `model/` (DTOs), `config/` (cache)
- `resource/` – `delegate/` (input validation and request/response mapping between controller and services), `mapper/`
- `repository/` – JPA `entity/`, `mapper/`, `service/` (repositories)
- `security/` – JWT filter/util, security config, auth DTOs

## Conventions and gotchas

- **Contract-first:** `controller/api/interfaces` and `controller/api/model` are generated from the YAML by `openapi-generator-maven-plugin` at build time. Change the YAML, not generated code. The custom template `src/main/resources/openapi-templates/model.mustache` is not wired into the build (`templateDirectory` is not set).
- **Dependency injection:** use Spring stereotypes with constructor injection and `private final` fields (no field injection, no `@Inject`/`@Autowired`). `@Service` for application/domain services, `@Component` for delegates and mappers, `@Repository` for repository implementations. Beans are found by component scan; there is no `spring.factories`. Keep `@Configuration` classes only for third-party beans (`CacheConfig`, `PasswordConfig`, `SecurityConfig`).
- **Data objects are not beans:** create DTOs, `Filter`, `PatchElement`, `Characteristic` and entities with `new`.
- **One application service per use case**, with an `I`-prefixed interface (e.g. `IGetTaskByIdApplicationService`).
- **User scoping:** resolve the current user via `IGetUserFromContextService`; task queries must filter by user id.
- **Cache:** Caffeine cache `tasks` (`CacheConfig`), keyed per user and task id via `TaskCacheKeys` (`@Cacheable` on get, `@CachePut` on update, explicit eviction on delete). Always build keys with `TaskCacheKeys` so users never share entries, and keep all read/write paths consistent.
- **Task updates:** `UpdateTaskCharacteristicsApplicationService` accepts only TITLE, DESCRIPTION, DUE_DATE, CREATED_AT, URGENCY, STATUS; anything else throws `IllegalArgumentException`.
- **Config:** `application.properties` points H2 at `/app/data/todo-db` (Docker path); override `spring.datasource.url` for local runs.

## Commands

- Build/test: `./mvnw clean verify`
- Run: `./mvnw spring-boot:run` or `docker compose up --build`
- Manual API requests: `src/main/resources/test.http`
- `out/` is IDE build output; don't commit it.
