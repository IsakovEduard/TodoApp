---
name: commit-and-log
description: Commit the current changes to Git with a generated message and add a one-line entry (the commit message head) to the change list at history/CL.md. Use when the user asks to commit, or runs /commit-and-log.
disable-model-invocation: true
---

# Commit and Log

Create a Git commit and record a short entry in the project change list, `history/CL.md` (repository root).

This skill commits, so run it only when the user asks. Never push.

## Steps

1. **Inspect state.** Run `git status --short` and `git diff --staged --stat`.
   - If nothing is staged, show the changed files and stage the relevant ones explicitly by path. Do not use `git add -A` or `git add .`.
   - Never stage build output or IDE files (`out/`, `target/`, `.idea/workspace.xml`) or anything that looks like a secret (`.env`, keys, credentials). Mention what was left out.

2. **Generate the message.** Follow the rules of the `commit-message` skill (`.claude/skills/commit-message/SKILL.md`): type prefix, mandatory overview, optional bullets, no invented details.
   - If that skill recommends a split (balanced unrelated concerns), present the split and get the user's confirmation. Then run steps 3 to 5 once per commit, in order, staging only that commit's files.

3. **Update the change list.** Add one entry to `history/CL.md`, creating the `history/` folder and file if missing.
   - The entry is the **head** of the commit message only: the first line (`TYPE: overview`). Do not include bullets or trailers.
   - Format, newest entry first, directly under the file header:
     `- YYYY-MM-DD – TYPE: overview`
   - New file header: `# Change List` followed by a blank line.
   - Use today's date. The commit hash is not known before committing, so do not include it.

4. **Stage the change list** (`git add history/CL.md`) so the entry is part of the same commit.

5. **Commit.** Use the full generated message. Append any attribution trailer that the session or CLAUDE.md requires. Do not use `--no-verify` or amend earlier commits. If a hook fails, fix the cause and create a new commit.

6. **Report.** Show `git log -1 --stat` output summary: hash, message head, files. Remind the user that nothing was pushed.

## Example `history/CL.md`

```
# Change List

- 2026-11-06 – REFACTOR: Replace manual bean registration with constructor-injected Spring stereotypes
- 2026-11-05 – FEATURE: Add spring cache using Caffeine for task lookups
```

## Guardrails

- Commit only when invoked by the user; do not push, force-push, or switch branches. If on `master`, mention it and ask whether to create a branch first.
- Do not rewrite or reorder existing entries in `history/CL.md`.
- If there are no changes to commit, say so and do nothing.
