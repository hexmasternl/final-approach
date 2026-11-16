## Why

Google Play Console flags the app for using an outdated `androidx.fragment:fragment` (1.1.0, recommended 1.2.1 or newer). The library is not declared by us, so it arrives transitively and must be raised before the next release upload.

GitHub issue: #89

## What Changes

- Find which dependency pulls in `androidx.fragment:fragment` 1.1.0 on the release runtime classpath.
- Make the release build resolve it to a current stable version (at least 1.2.1), by bumping the parent dependency, or else by a version-catalog entry and a constraint in the Android host.
- Check the release classpath for other outdated or flagged SDKs and record the result.

## Capabilities

### New Capabilities
- `android-dependencies`: minimum resolved versions of AndroidX libraries on the Android release classpath.

### Modified Capabilities

## Out of scope

- Upgrading unrelated dependencies that Play does not flag.
- Changing the release pipeline (`play-store-deployment`).

## Depends on

- Nothing. Confirming the warning disappears needs the next Play upload, a human check.

## Impact

`app/src/gradle/libs.versions.toml` and `app/src/androidApp/build.gradle.kts`. No game code.
