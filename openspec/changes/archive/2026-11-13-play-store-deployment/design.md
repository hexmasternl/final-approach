# Design

## Context

Android app with Gradle (`:androidApp:bundleRelease`, `:shared:allTests`). See proposal.md.

## Goals / Non-Goals

**Goals:**
- One workflow, triggered by a `development` to `main` pull request, that ends in an internal track release.
- Release notes derived from the pull request's commits.

**Non-Goals:**
- Emulator tests, production promotion, iOS.

## Decisions

- **Trigger:** `pull_request` with `types: [closed]` and `branches: [main]`, job guarded by `merged == true` and head ref `development`. Nothing runs on open or on unmerged close.
- **Tests:** JVM unit tests only (`./gradlew :shared:allTests` plus Android unit tests); no emulator.
- **Signing:** upload keystore stored base64 in a secret, decoded at build time; passwords via secrets.
- **Versioning:** GitVersion with `GitVersion.yml` (GitFlow workflow, `v` tag prefix). `versionName` = MajorMinorPatch; `versionCode` = `Major*1_000_000 + Minor*1_000 + Patch`. Both passed to Gradle through environment variables.
- **Upload:** `r0adkll/upload-google-play` with a service account JSON secret, track `internal`, status `completed`.
- **Release notes:** built from the diff between `development` and `main` (the merged PR's base and head SHAs): commit subjects plus changed files. The Play notes are trimmed to 500 characters; the GitHub release gets the full text.
- **GitHub release:** last step, runs only if all earlier steps succeeded: tag `vX.Y.Z` on `main`, notes attached, bundle attached.
- **Docs:** `docs/deployment.md` documents key, service account and secret setup.

## Risks / Trade-offs

- [First upload must be manual in Play Console] → Documented in `docs/deployment.md`.
- [Version not increasing is rejected by Play] → Full-history checkout and release tags let GitVersion keep increasing.
