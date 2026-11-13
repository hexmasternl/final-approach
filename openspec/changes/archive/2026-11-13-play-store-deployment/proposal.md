# Proposal

GitHub issue: #20

## Why

Releases to testers are manual. A pull request from `development` to `main` signals a release candidate, so a workflow should build, test and publish it to the Play Store internal track with release notes, without manual steps.

## What Changes

- A GitHub Actions workflow runs when a pull request from `development` to `main` is merged.
- Version name and version code are generated with GitVersion (`GitVersion.yml`).
- It builds the app, runs unit tests (not emulator tests) and builds a signed Android App Bundle.
- It uploads the bundle to Google Play and creates a release on the internal track.
- Release notes are built from the diff between `development` and `main` and included in the internal track release.
- When the workflow succeeds, it tags the version and creates a GitHub release with the notes and the bundle.
- `docs/deployment.md` explains how to create the keys and secrets and set everything up.

## Capabilities

### New Capabilities
- `play-store-deployment`: the pipeline from a development-to-main pull request to an internal track release.

### Modified Capabilities

## Impact

- `.github/workflows/` (new workflow), `GitVersion.yml`, Gradle release signing and version config, `docs/deployment.md`.
- Requires repository secrets for the upload key and a Play service account.

## Out of scope

- Emulator/instrumented tests, promotion beyond the internal track, iOS releases.
