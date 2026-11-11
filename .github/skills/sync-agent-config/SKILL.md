---
name: sync-agent-config
description: Keeps the skills, agents, commands and instructions of this repo in sync between Claude Code (.claude/, CLAUDE.md) and GitHub Copilot (.github/). Use after adding or changing anything under .claude/ or .github/skills, .github/agents or .github/prompts, when Copilot is missing a skill or agent that Claude Code has (or the other way round), or when asked to sync, mirror or check the agent configuration.
---

Keep both coding tools working from the same repo. `.claude/` is the **source of truth**; `.github/` is the generated mirror. Only go the other way (Copilot to Claude) when the user says so or when a file exists only under `.github/` — then ask which side wins.

This skill is itself mirrored, like every other skill. Run it on `development`, or in the worktree of the change you are working on. Never touch `main`. Follow the `git-workflow` skill for commits.

## Mapping

| Claude Code | GitHub Copilot | How |
|---|---|---|
| `.claude/skills/<n>/SKILL.md` (+ supporting files) | `.github/skills/<n>/SKILL.md` | Copy the folder. Same frontmatter (`name`, `description`). Rewrite command references (below). |
| `.claude/agents/<n>.md` | `.github/agents/<n>.agent.md` | Convert frontmatter (below); body copied as is. |
| `.claude/commands/<ns>/<c>.md` | `.github/prompts/<ns>-<c>.prompt.md` | Keep only `description` in the frontmatter; keep the body. |
| `CLAUDE.md` | `.github/copilot-instructions.md` | Not generated. Both are hand-written; check they agree and report drift. |

### Command references
Claude uses `/ns:cmd`, Copilot prompt files use `/ns-cmd`. In mirrored files replace `/opsx:` with `/opsx-` (and likewise for any other namespace found under `.claude/commands/`). Other differences between a skill and its mirror are drift, not intent.

### Agent frontmatter
- Keep `name` and `description`.
- `tools`: map Claude tool names to Copilot aliases: `Bash` → `execute`, `Read` → `read`, `Write` and `Edit` → `edit`, `Glob` and `Grep` → `search`, `WebFetch`/`WebSearch` → `web`, `Task` → `agent`. De-duplicate. Write as a YAML list, e.g. `tools: ['execute', 'read', 'search']`.
- `skills`, `model`, `color` and other Claude-only keys: drop them. If `skills:` was set, add a first line to the body: "Use the `<skill>` skills for this work." (Copilot loads skills by matching their descriptions.)
- A read-only agent (no `Write`/`Edit`) must stay read-only: never add `edit`.

## Steps

1. **Inventory.** List `.claude/skills/*`, `.claude/agents/*`, `.claude/commands/**/*` and the three `.github/` counterparts. Build the expected mirror name for each source.
2. **Diff.** For each source, generate the expected mirror content (apply the mapping in memory) and compare to the file on disk, ignoring line endings. Classify as: *missing*, *outdated*, *in sync*, or *orphan* (exists under `.github/` with no source).
3. **Report** a short table of what is missing, outdated and orphaned. If asked only to check ("is it in sync?"), stop here.
4. **Apply.** Create or overwrite missing and outdated mirrors with the create/edit tools. Preserve the file's existing line-ending style. Do not delete orphans, or mirrors whose source was deleted, without the user's confirmation.
5. **Instructions check.** Compare `CLAUDE.md` and `.github/copilot-instructions.md`. Skill paths (`.claude/skills/...` vs `.github/skills/...`) may legitimately differ; rules, commands and conventions must not. List real differences and propose the fix; edit only after the user agrees.
6. **Verify.** Re-run the diff; everything must be *in sync*. Check each generated agent has valid frontmatter (`name`, `description`, `tools` as a list) and that no `/opsx:` reference remains under `.github/`.
7. **Commit** the `.github/` changes (and any `.claude/` change that triggered the sync) on the current branch following the `git-workflow` skill.

## Guardrails

- Mirror only; do not rewrite the substance of a skill or agent. Put improvements in the source first, then sync.
- Never copy secrets or local settings: skip `.claude/settings.local.json` and anything not listed in the mapping.
- If a Copilot file needs to differ from its source, add the rule to this mapping first.
