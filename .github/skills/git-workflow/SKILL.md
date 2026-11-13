---
name: git-workflow
description: The git way of working for this repo - which branch to use, when to commit and push, and what must happen around OpenSpec changes. Use before any git commit, push, branch, worktree or pull request, and always together with the OpenSpec skills - when proposing a change, when implementing one ("openspec apply", "opsx apply"), and when archiving one ("openspec archive", "opsx archive").
---

# Git workflow

This repo has two long-lived branches and one short-lived branch per OpenSpec change.

| Branch | Represents | Who writes to it |
|---|---|---|
| `main` | Production | Nobody by hand. Never commit, push, merge, rebase or reset here. Never branch from it, except to create `development` when that does not exist. |
| `development` | Integration, and the home of OpenSpec planning | Claude and Copilot may commit and push directly, for OpenSpec proposals and archives. Implementation arrives only through pull requests. |
| `change/<change-name>` | One OpenSpec change being implemented | Created from `development` in its own worktree. Merged back by pull request. |

This skill adds git rules around the OpenSpec skills. Follow the OpenSpec skill for the OpenSpec steps themselves, and this skill for everything git and GitHub.

## Creating `development` when it does not exist

If `development` does not exist, create it from `main` and push it. This is the one time `main` is used as a starting point.

```bash
git fetch origin
git show-ref --verify --quiet refs/remotes/origin/development || {
  git switch -c development origin/main
  git push -u origin development
}
```

If it exists on the remote but not locally, check it out instead: `git switch development`. Creating the branch does not change `main`.

If the current checkout is on `main` with uncommitted work, switch to `development` (git carries the uncommitted changes along) and commit there. Never commit them on `main`.

## No worktrees for `main` or `development`

NEVER create a worktree for `main` or for `development`. Both are only ever checked out in the main checkout of the repo, by switching branches there. Worktrees exist for `change/<change-name>` branches and nothing else.

## Proposing a change (on `development`)

1. Be on `development`, up to date: `git switch development && git pull --ff-only`.
2. Create the OpenSpec change as the OpenSpec skill describes.
3. Record the GitHub issue in the change's `proposal.md` as a line of its own: `GitHub issue: #<number>`.
4. Make sure the issue names the change: its body carries the change reference block that the `triage` skill defines (it starts with `<!-- openspec-change: <change-name> -->`). If it does not, add that block to the issue body after pushing.
5. Commit the change's files and push `development`.

## Implementing a change (`openspec apply`)

### 1. Gate: the issue link must hold both ways

Do not implement anything until all of this is true. If any check fails, stop, say which one, and offer to fix the link. Do not work around it.

- The change's `proposal.md` contains `GitHub issue: #<number>`.
- That issue exists in this repo and is open: `gh issue view <number> --json number,state,title,body,comments`.
- The issue body carries the change reference block for this change: it contains `<!-- openspec-change: <change-name> -->` with the exact change name.

### 2. Worktree and branch from `development`

Worktrees are ALWAYS created in a directory named `final-approach-worktrees`, one directory higher than the repository: next to the repository folder, never inside it. Each change gets its own folder there, named after the change.

```
<parent>/
├─ final-approach/                  # the repository (main checkout)
└─ final-approach-worktrees/
   └─ <change-name>/                # one worktree per change
```

From the repository root that is `../final-approach-worktrees/<change-name>`. Run the commands below from the repository root so the relative path resolves there, and create the directory if it does not exist (git does this for you). Never put a worktree anywhere else: not inside the repository, not in `.claude/`, not in a temp folder.

The change must already be committed and pushed on `development`, so the new branch contains it.

```bash
git fetch origin
git worktree add -b "change/<change-name>" "../final-approach-worktrees/<change-name>" origin/development
```

Do all implementation work inside that worktree. Leave the main checkout on `development`.

If the branch or worktree already exists, the change was started earlier: continue in it rather than creating a second one.

### 3. Commit often

- Commit after each completed task of the change's `tasks.md`. One task, one commit is the goal; a large task may take several.
- Tick the task off in `tasks.md` in the same commit as its code.
- Message: `<change-name>: <what the task did>`.
- Commits stay local while work is in progress. Do not push after every commit.

### 4. Push and open the pull request

When every task is done, or when the only tasks left are checks a human has to do:

```bash
git push -u origin "change/<change-name>"
gh pr create --base development --head "change/<change-name>" --title "<change-name>: <summary>" --body-file <file>
```

The pull request body:

- Says what the change does, in a few sentences.
- References the issue as `Refs #<number>`. Do not use `Closes` or `Fixes`: the issue is closed when the change is archived, not when the pull request merges.
- Lists any remaining human checks as an unticked checklist.

Never target `main`. Do not merge the pull request yourself unless the user asks.

## Archiving a change (`openspec archive`)

Archive only after the change's pull request has been merged into `development`. Check with `gh pr list --head "change/<change-name>" --state merged`. If it is not merged, stop and say so.

1. **Archive on `development`.** In the main checkout: `git switch development && git pull --ff-only`, run the OpenSpec archive as its skill describes, then commit and push `development`.
2. **Close the issues and move them to Done.** Follow the `close-archived-issues` skill: it finds every issue of the change (from `proposal.md` and from the issues' change reference blocks), keeps open any issue another active change still needs, closes the rest with a comment, and moves each to the Done lane of every GitHub project it is on.
3. **Clean up the worktree and branches.**

   ```bash
   git worktree remove "../final-approach-worktrees/<change-name>"
   git branch -d "change/<change-name>"
   git push origin --delete "change/<change-name>"
   git worktree prune
   ```

   If the worktree has uncommitted changes or the branch is not fully merged, stop and show the user what is there. Do not force-remove or force-delete without their say-so. A remote branch that GitHub already deleted on merge is fine.

Finish by reporting what was done: archive commit, issue closed, project lane moved or not applicable, worktree and branches removed.

## Rules that always apply

- Never touch `main`. Production is updated by people, outside this workflow. The only use of `main` here is as the starting point when `development` has to be created.
- Never create a worktree for `main` or `development`. Only `change/` branches get a worktree.
- Worktrees always live in `final-approach-worktrees`, one directory higher than the repository: `../final-approach-worktrees/<change-name>`.
- Direct commits to `development` are only for OpenSpec proposals, archives and their documentation. Code reaches `development` through a pull request from a `change/` branch.
- No implementation without an open issue that names the change, and a change that names the issue.
- Never force-push `development`, and never rewrite history that has been pushed.
- Never skip hooks or signing.
