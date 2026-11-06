# Coding Standards

Rules for writing new code and changing existing code in this project. Goal: clean, concise, easy to read and modify. When touching existing code, follow these rules in the lines you change; do not reformat unrelated code.

## Principles

- Prefer clarity over cleverness. Write the simplest code that is correct.
- Keep changes minimal and scoped to the task. No speculative abstractions, no unused code.
- Match the surrounding code unless it conflicts with this file.
- Behavior-preserving refactors and behavior changes go in separate commits.

## Naming

- **Classes and records:** `UpperCamelCase` nouns (`TaskFilterDomainService`). **Methods and variables:** `lowerCamelCase`; methods start with a verb (`getTaskById`, `applyFilter`).
- **Constants:** `UPPER_SNAKE_CASE` in `private static final` fields. **Packages:** lowercase, no underscores.
- Use full words, no abbreviations: `customerDetails`, not `custDtl`. Exceptions: well-known terms (`id`, `url`, `dto`).
- Names state purpose, not type: `taskIds`, not `list`. Booleans read as questions: `isDeleted`, `hasAccess`.
- Interfaces keep the `I` prefix with an `Xxx` implementation (`ITaskRepository` / `TaskRepository`), one application service per use case.
- Fix misleading names when you touch them; do not leave a name that contradicts the code.

## Structure and layering

- Respect the layers: `controller` → `resource` (delegates, mapping) → `business` (services, domain) → `repository`. Dependencies point downward only.
- Controllers contain no logic beyond delegating. Input validation lives in delegates, business rules in services, persistence in repositories.
- One responsibility per class. Move logic into the layer it belongs to rather than adding a helper "util" class.
- Constructor injection with `private final` fields; stereotype annotations only (see `CLAUDE.md`). No field injection.

## Types and data

- Use Java records for immutable data carriers (requests, filters, results, responses). Keep ordinary classes for JPA entities and objects built up in steps.
- Do not add Lombok.
- Prefer immutability: `final` fields and locals where practical, unmodifiable collections (`List.of`, `.toList()`).
- Use enums or constants instead of repeated string literals (`"DE"`, `"AC"`, status values) in new code.
- Do not return or pass `null` for collections; return an empty collection. Use `Optional` for return values only, not fields or parameters.

## Writing methods

- Do one thing per method; keep the abstraction level consistent inside it.
- Use early returns and guard clauses instead of deep nesting.
- Replace magic numbers and strings with named constants.
- Prefer streams for simple transformations; use a loop when a stream hurts readability.
- Do not duplicate logic. Extract a method on the second repetition, not before.

## Size limits (guidelines; justify exceptions)

- Methods: about 20 lines or fewer.
- Parameters: 4 or fewer. Beyond that, introduce a record.
- Nesting: 2 levels or fewer.
- Classes: about 200 lines or fewer. Split by responsibility when larger.
- Lines: 120 characters or fewer.

## Comments

- Code should explain itself through names. Comment the why (a constraint, a trade-off, a non-obvious decision), never the what.
- Delete comments that narrate steps or restate the code (`// Step 1: ...`). Delete commented-out code.
- Javadoc only for public APIs whose behavior is not obvious from the signature.

## Logging

- Use SLF4J with parameterized messages: `logger.info("Task created: id={}", id)`. No string concatenation.
- Levels: `ERROR` for failures needing attention, `WARN` for recoverable problems, `INFO` for significant business events, `DEBUG` for diagnostic detail.
- Log identifiers and outcomes, not whole objects. Never log passwords, tokens, secrets or personal data.
- Log an exception once, where it is handled, with the exception as the last argument.
- Do not log inside tight loops.

## Testing

- Every new or changed service and domain class has unit tests. Bug fixes include a regression test.
- Name tests `methodUnderTest_ShouldExpectedBehavior` (existing style), adding `_WhenCondition` when needed.
- Structure each test as Arrange / Act / Assert, separated by blank lines or short comments.
- One behavior per test; assert the outcome, not implementation details. Avoid unused mocks.
- Use JUnit 5 and Mockito (`@Mock` with `@InjectMocks`, which works with constructor injection). Keep tests independent and free of shared mutable state, including `SecurityContextHolder`; clear it after use.
- Do not hit the database or network in unit tests. Reserve `@SpringBootTest` for context and integration checks.
- Run `./mvnw clean verify` before committing; do not commit failing tests.

## Definition of done

- Code compiles and all tests pass.
- No unused imports, variables, methods or commented-out code.
- No leftover `@Inject`, `@Autowired`, `System.out` or debug output.
- `README.md` and `CLAUDE.md` updated if behavior, structure or conventions changed.

## Frontend (TypeScript / React, `frontend/`)

- TypeScript strict; no `any`, no `@ts-ignore`. Use `import type` for types. Prefer function components and hooks.
- Group code by feature (`src/features/<name>/`): slice, components, pages and tests together. Shared pieces go in `src/components/` and `src/app/`.
- Server data goes through the generated RTK Query client (`src/api/generated.ts`); never edit it, regenerate it. Client-only state (token, theme, filters, selection) goes in Redux slices.
- Colors come from the CSS variables and Tailwind tokens (`bg-surface`, `text-muted`, `bg-accent`, ...) so light and dark themes both work. Do not hard-code colors in components.
- Accessible by default: real `<button>`, `<a>`, `<label>` with `htmlFor`, `aria-label` on icon-only buttons, visible focus, 44px minimum touch targets, text contrast of at least 4.5:1 in both themes. Status, urgency and errors are conveyed by text, not color alone.
- Layouts are mobile first and must work from 360px wide with no horizontal page scroll.
- Component names `UpperCamelCase`, hooks `useXyz`, files named after their main export. Keep components small and focused; extract a hook or component rather than nesting deeply.
- Tests with Vitest and Testing Library: query by role or label, as a user would. Mock the network with MSW, never the Redux store or RTK Query internals. Every new feature has tests for the success path and the main error paths.
- Run `npm test`, `npm run build` and `npm run lint` (or `./mvnw clean verify`) before committing.
