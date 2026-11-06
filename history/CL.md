# Change List

- 2026-11-06 – FIX: Enforce validation on registration and login requests
- 2026-11-06 – FIX: Reject tokens of non-existent users with 403 and remove unused ForbiddenException
- 2026-11-06 – FIX: Return 404 for missing tasks and 401 for invalid credentials instead of 500
- 2026-11-06 – FIX: Restrict task lookup by id to the owning user and scope cache entries per user
- 2026-11-06 – FIX: Cache task lookups for all ids by removing leftover cache condition
- 2026-11-06 – DOCS: Add commit approval, post-test review and choice prompt workflow rules
- 2026-11-06 – FIX: Externalize JWT secret and expiration to validated configuration
- 2026-11-06 – REFACTOR: Apply coding standards across services, models, security and tests
- 2026-11-06 – DOCS: Add coding standards with on-demand loading from CLAUDE.md
- 2026-11-06 – CHORE: Add Claude Code skills for commit message generation and commit logging
- 2026-11-06 – DOCS: Rewrite README and add CLAUDE.md project guide
- 2026-11-06 – REFACTOR: Replace manual bean registration with constructor-injected Spring stereotypes
