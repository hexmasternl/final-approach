## 1. Investigate

- [x] 1.1 Run `dependencyInsight` for `androidx.fragment:fragment` on `releaseRuntimeClasspath` and note the parent. Verify: the source of 1.1.0 is named in the PR.
- [x] 1.2 List the release classpath and note any other outdated or flagged SDKs. Verify: the findings are written in the PR.

## 2. Fix

- [x] 2.1 Bump the parent dependency in `app/src/gradle/libs.versions.toml`, or else add `androidx-fragment` and a constraint in `app/src/androidApp/build.gradle.kts`. Verify: `dependencies --configuration releaseRuntimeClasspath` shows fragment 1.2.1 or newer.
- [x] 2.2 Raise any other clearly flagged SDK found in 1.2. Verify: it resolves to the new version.

## 3. Verify

- [x] 3.1 Run `./gradlew :androidApp:assembleDebug` and `./gradlew :shared:allTests` from `app/src`. Verify: both succeed.
- [ ] 3.2 Human: upload the next release and confirm the Play Console warning is gone.

## Findings (autocoder run runner-20261116-vpu1)

- androidx.fragment 1.1.0 came from `play-services-basement:18.9.0` (via `play-services-ads` 25.5.0 and `user-messaging-platform` 4.0.0); no newer parent release changes it, so the fallback (catalog entry plus constraint, fragment 1.8.9) was used.
- Other SDKs on the release classpath: `play-services-measurement-*` 20.1.2, `play-services-ads-identifier` 18.0.0 and `play-services-appset` 16.0.1 are old but not flagged by Play; left alone.
