---
name: commit-message
description: Generate a concise, professional Git commit message (TYPE: overview, optional bullet list) from staged changes or provided code modifications. Use when the user asks for a commit message, or before committing.
user-invocable: false
---

# Commit Message Generator

Generate a concise, senior-level Git commit message from the staged changes or the code modifications provided.

## Gathering the changes

1. Run `git diff --staged`. If nothing is staged, run `git diff` and `git status --short`, and say that the message is based on unstaged changes.
2. Read the diff itself, not only file names. Base the message on what the code actually does.
3. If the user pasted code or a description instead, use that.

## Rules

- Start with an explicit commit type, uppercase, followed by a colon:
  - `FEATURE` – new functionality
  - `FIX` – bug fixes
  - `REFACTOR` – code restructuring without behavior changes
  - `PERFORMANCE` – performance improvements
  - `TEST` – adding or modifying tests
  - `DOCS` – documentation changes
  - `CHORE` – maintenance, configuration, dependencies, or tooling
  - `HOTFIX` – urgent production fixes
- Follow with a short, descriptive overview of what changed and why. The overview is mandatory.
- Add a bullet list of key changes only when needed to clarify multiple significant modifications. Omit it when the overview is sufficient. Do not list trivial implementation details.
- Keep it concise, technically precise, and focused on the actual changes.
- Use professional engineering terminology. No vague descriptions, unnecessary context, marketing language, or first-person statements.
- Do not invent functionality, benefits, or implementation details that the changes do not support.
- Prioritize clarity and brevity over exhaustive description.
- If the diff mixes unrelated concerns (for example a refactor and a docs rewrite), judge how the changes are distributed:
  - One concern clearly dominates: use its type and mention the minor one in a bullet.
  - The concerns are roughly balanced (close to a 50:50 split by size or significance): recommend splitting into separate commits. List which files belong to each commit and provide a relevant message for each, in commit order.

## Output format

```
TYPE: Short overview of the change

- Key change 1
- Key change 2
- Key change 3
```

Output only the commit message in a code block, with no extra commentary, unless a split is recommended. When recommending a split, output one block per commit, each preceded by its file list:

```
Commit 1 – files: <paths>
TYPE: Overview

Commit 2 – files: <paths>
TYPE: Overview
```

## Examples

```
FEATURE: Introduce Kafka-based asynchronous processing for quote workflows

- Add event-driven task orchestration
- Implement parallel execution for independent operations
- Improve workflow extensibility
```

```
FIX: Prevent duplicate quote processing during concurrent requests
```
