# play-store-deployment Specification

## Purpose
Defines the automated pipeline from a merged `development` to `main` pull request to a Play Store internal track release and a GitHub release.

## Requirements

### Requirement: Workflow runs when a development-to-main pull request is merged
The system SHALL start the deployment workflow when a pull request from `development` to `main` is merged.

#### Scenario: Merged release pull request
- **WHEN** a pull request from `development` to `main` is merged
- **THEN** the deployment workflow starts

#### Scenario: Opened or unmerged pull request
- **WHEN** the pull request is only opened, updated, or closed without merging
- **THEN** the deployment workflow does not run

#### Scenario: Other pull request
- **WHEN** a pull request has any other head or base branch
- **THEN** the deployment workflow does not run

### Requirement: Version is generated with GitVersion
The workflow SHALL derive the version name and version code from GitVersion using the repository's `GitVersion.yml`.

#### Scenario: Increasing version
- **WHEN** a new release is built after a previous release tag
- **THEN** the version code is higher than the previous release's version code

### Requirement: Build and unit tests gate the release
The workflow SHALL build the app and run unit tests, excluding emulator tests, before building the bundle.

#### Scenario: Tests fail
- **WHEN** a unit test fails
- **THEN** no bundle is uploaded and no GitHub release is created

### Requirement: Bundle is released to the internal track
The workflow SHALL build a signed bundle, upload it to Google Play and create a release on the internal track.

#### Scenario: Successful run
- **WHEN** build and tests pass
- **THEN** a new internal track release exists containing the bundle

### Requirement: Release notes are built from the branch diff
The workflow SHALL build release notes from the diff between `development` and `main` and include them in the internal track release.

#### Scenario: Notes included
- **WHEN** the release is created
- **THEN** it carries release notes summarising the commits and changes between `main` and `development`

### Requirement: GitHub release on success
The workflow SHALL create a tag and a GitHub release, with the release notes and the bundle, only when all previous steps succeeded.

#### Scenario: Successful run
- **WHEN** the internal track release was created
- **THEN** a GitHub release `vX.Y.Z` exists with the notes and bundle

#### Scenario: Failed run
- **WHEN** any earlier step failed
- **THEN** no GitHub release is created

### Requirement: Setup is documented
The repository SHALL include `docs/deployment.md` explaining how to create the upload key, Play service account and GitHub secrets.

#### Scenario: New maintainer
- **WHEN** a maintainer follows `docs/deployment.md`
- **THEN** they can configure everything the workflow needs
