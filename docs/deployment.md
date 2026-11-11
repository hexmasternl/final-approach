# Deployment

How Final Approach is released to the Google Play **internal testing** track.

## Overview

1. Work lands on `development`.
2. A pull request from `development` to `main` is **merged**.
3. The deployment workflow starts (it does not run when the PR is opened or when it is closed without merging).
4. The workflow:
   1. checks out `main` with full history,
   2. calculates the version with GitVersion (`GitVersion.yml`),
   3. builds the app and runs unit tests (no emulator tests),
   4. builds a signed Android App Bundle (`.aab`),
   5. builds release notes from the diff between `development` and `main`,
   6. uploads the bundle to the internal track and creates the release,
   7. when all of that succeeded, tags `vX.Y.Z` and creates a GitHub release with the same notes and the bundle attached.

Nothing is published if the build or a unit test fails.

## Versioning (GitVersion)

`GitVersion.yml` in the repository root uses the GitFlow workflow: `main` produces release versions (`0.1.0`, `0.1.1`, ...), `development` produces `-beta` pre-releases. The workflow computes the version and passes it to Gradle:

- `versionName` = `MajorMinorPatch` (for example `0.1.3`)
- `versionCode` = `Major * 1_000_000 + Minor * 1_000 + Patch` (for example `1003`)

`versionCode` only increases as long as the version increases, which Google Play requires. Bump the minor or major version by tagging or by adding `+semver: minor` / `+semver: major` to a commit message. Each release tag (`vX.Y.Z`) created by the workflow makes GitVersion continue from there.

## One-time setup

### 1. Create the app in Play Console

1. Go to <https://play.google.com/console> and create the app (package `nl.hexmaster.final_approach`, the Android `applicationId`).
2. Complete the required setup tasks (app content, store listing basics).
3. **The first bundle must be uploaded manually.** Google Play does not accept API uploads until the app has had at least one manual release. Build a signed bundle locally (see below) and upload it under *Testing > Internal testing > Create new release*.
4. Under *Internal testing > Testers* add a tester list (email addresses) and share the opt-in link.

### 2. Create the upload key

Play App Signing is used: Google holds the app signing key, and you hold an **upload key**.

```powershell
keytool -genkeypair -v `
  -keystore upload-keystore.jks `
  -alias upload `
  -keyalg RSA -keysize 2048 -validity 10000
```

Answer the prompts and remember the keystore password, the key password and the alias (`upload`). Store the file and passwords in a password manager. **Never commit the keystore.** If you lose it, you can request an upload key reset from Play Console, but it takes time.

When you first upload a bundle, accept *Play App Signing* so Google manages the real signing key.

Encode the keystore for use as a secret:

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("upload-keystore.jks")) | Set-Clipboard
```

### 3. Create a Google Play service account

The workflow authenticates to the Play Developer API with a service account.

1. In the [Google Cloud Console](https://console.cloud.google.com/) create or pick a project.
2. Enable the **Google Play Android Developer API** (*APIs & Services > Library*).
3. Go to *IAM & Admin > Service Accounts > Create service account*. Name it, for example, `play-publisher`. No project roles are needed.
4. Open the account, then *Keys > Add key > Create new key > JSON*. A JSON file is downloaded. Keep it private.
5. In Play Console go to *Users and permissions > Invite new users* and enter the service account email (`play-publisher@<project>.iam.gserviceaccount.com`).
6. Grant it access to the Final Approach app only, with the permission **Release to testing tracks** (and *View app information*). Save. Permissions can take up to a few hours to apply.

### 4. Add the GitHub secrets

In GitHub go to *Settings > Secrets and variables > Actions > New repository secret* and add:

| Secret | Value |
|---|---|
| `UPLOAD_KEYSTORE_BASE64` | Base64 of `upload-keystore.jks` (step 2) |
| `UPLOAD_KEYSTORE_PASSWORD` | Keystore password |
| `UPLOAD_KEY_ALIAS` | Key alias (`upload`) |
| `UPLOAD_KEY_PASSWORD` | Key password |
| `PLAY_SERVICE_ACCOUNT_JSON` | Full contents of the service account JSON file |

`GITHUB_TOKEN` is provided automatically. The workflow needs `contents: write` permission to tag and create the GitHub release; check *Settings > Actions > General > Workflow permissions* does not block this.

Optionally create a GitHub *environment* named `play-internal`, put the secrets there and add required reviewers if you want a manual approval before each release.

### 5. Gradle signing config

Already in `app/src/androidApp/build.gradle.kts`. Signing values are read from the environment (`UPLOAD_KEYSTORE_PATH`, `UPLOAD_KEYSTORE_PASSWORD`, `UPLOAD_KEY_ALIAS`, `UPLOAD_KEY_PASSWORD`), so nothing sensitive is in the repository. Without them `bundleRelease` produces an unsigned bundle. `versionCode` and `versionName` come from `VERSION_CODE` and `VERSION_NAME`, with local defaults.

### 6. Branches

Make sure the long-lived branches are called `development` and `main`, and that `development` to `main` pull requests are how releases happen. Consider protecting `main` so merges require the PR.

## The workflow

`.github/workflows/deploy.yml` runs on `pull_request: closed` into `main`, guarded by `merged == true` and head branch `development`. Steps: checkout `main` with full history, GitVersion, `:shared:allTests`, decode keystore, `:androidApp:bundleRelease`, build release notes, upload to the internal track, create the GitHub release.

- Release notes come from the **diff between `development` and `main`**: the commits and changed files in `base.sha..head.sha` of the merged pull request. Play limits notes to 500 characters per language, so the Play notes are the trimmed commit list; the GitHub release carries the full list plus the changed files.
- The GitHub release step is last and only runs when everything before it succeeded.
- The Play package name is `nl.hexmaster.final_approach` (the Android `applicationId`).

## Trying it locally

Build a bundle to check the Gradle setup (from `app/src`):

```powershell
./gradlew :androidApp:bundleRelease
```

Preview the version GitVersion calculates:

```powershell
dotnet tool install --global GitVersion.Tool
dotnet-gitversion
```

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| `Package not found` on upload | App not created in Play Console, or `packageName` differs from `applicationId` |
| `The caller does not have permission` | Service account not invited to Play Console or lacks *Release to testing tracks*; wait for permissions to apply |
| `Version code already used` | The version did not increase; make sure GitVersion sees the previous `v*` tag (checkout with `fetch-depth: 0`) |
| `Only releases with status draft may be created on draft app` | First release was not done manually in Play Console, or the app is still a draft |
| `Wrong keystore` / signing mismatch | The upload key differs from the one registered with Play; reset it in Play Console |
| Workflow does not start | The PR was not merged, was not from `development`, or did not target `main` |