## 1. Investigate

- [ ] 1.1 Run `dependencyInsight` for `androidx.fragment:fragment` on `releaseRuntimeClasspath` and note the parent. Verify: the source of 1.1.0 is named in the PR.
- [ ] 1.2 List the release classpath and note any other outdated or flagged SDKs. Verify: the findings are written in the PR.

## 2. Fix

- [ ] 2.1 Bump the parent dependency in `app/src/gradle/libs.versions.toml`, or else add `androidx-fragment` and a constraint in `app/src/androidApp/build.gradle.kts`. Verify: `dependencies --configuration releaseRuntimeClasspath` shows fragment 1.2.1 or newer.
- [ ] 2.2 Raise any other clearly flagged SDK found in 1.2. Verify: it resolves to the new version.

## 3. Verify

- [ ] 3.1 Run `./gradlew :androidApp:assembleDebug` and `./gradlew :shared:allTests` from `app/src`. Verify: both succeed.
- [ ] 3.2 Human: upload the next release and confirm the Play Console warning is gone.
