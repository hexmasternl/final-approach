# Tasks

## 1. Build and release signing

- [x] 1.1 Add release signing config to the Android app reading keystore values from environment, and verify a local `bundleRelease` still works unsigned without them
- [x] 1.2 Add `GitVersion.yml` and read `VERSION_NAME`/`VERSION_CODE` in Gradle, and verify the bundle reports the GitVersion-derived values

## 2. Workflow

- [x] 2.1 Add a workflow triggered when a `development` to `main` pull request is merged that builds and runs unit tests only, and verify it does not run on open or unmerged close
- [x] 2.2 Add bundle build with signing from secrets and verify the signed `.aab` is produced
- [x] 2.3 Add Play upload to the internal track and verify a release is created
- [x] 2.4 Add a final step creating tag `vX.Y.Z` and a GitHub release with notes and bundle, and verify it only runs after a successful upload

## 3. Release notes and docs

- [x] 3.1 Build release notes from the `development`/`main` diff, trimmed to the Play limit, and verify they appear in the internal release and the GitHub release
- [x] 3.2 Keep `docs/deployment.md` (keys, service account, secrets, troubleshooting) in sync with the workflow
