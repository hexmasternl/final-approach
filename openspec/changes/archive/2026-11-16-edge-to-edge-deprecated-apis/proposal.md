## Why

Google Play Console reports "Your app uses deprecated APIs or parameters for edge-to-edge" and "Edge-to-edge may not display for all users" for version 0.1.8. `MainActivity` has called `enableEdgeToEdge()` since v0.1.0 and a first scan of `app/src` found no deprecated call, so the source is not known. The Play Console class list is not available, so this change finds the flagged call itself and fixes it where it lives.

GitHub issue: #90

## What Changes

- Investigate: scan the release dependencies (jars and AARs, `play-services-ads` 25.5.0 first) and the theme for deprecated edge-to-edge calls and attributes, and record where each one lives.
- Fix each finding at its source: bump the dependency, change our code, or change the theme. If a finding sits in a library with no fixed release, record that and what we do instead.
- Run `./gradlew :androidApp:lint` and clear edge-to-edge findings that are ours.
- Verify insets on Android 15 or newer: 3-button navigation, gesture navigation, display cutout, landscape.

## Capabilities

### New Capabilities
- `edge-to-edge`: the Android app draws edge-to-edge without deprecated window APIs, and keeps its content clear of system bars and cutouts.

### Modified Capabilities

## Out of scope

- Changing the immersive-mode behaviour (`system-gestures`).
- Redesigning screens; only inset fixes for content hidden by a bar or cutout.
- Upgrading dependencies that are not implicated.

## Depends on

- Nothing. Confirming the Play warnings are gone needs the next release upload, a human check.

## Impact

`app/src/gradle/libs.versions.toml`, `app/src/androidApp` (build file, theme, `MainActivity`), possibly inset handling in `shared` UI. A findings record in `design.md`.
