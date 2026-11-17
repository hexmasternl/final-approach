## ADDED Requirements

### Requirement: Fragment library is not outdated
The release runtime classpath of the Android app SHALL resolve `androidx.fragment:fragment` to version 1.2.1 or newer.

#### Scenario: Resolved version
- **WHEN** `./gradlew :androidApp:dependencies --configuration releaseRuntimeClasspath` is run
- **THEN** `androidx.fragment:fragment` resolves to 1.2.1 or newer

#### Scenario: Play Console warning
- **WHEN** the next release bundle is uploaded to Google Play
- **THEN** the outdated `androidx.fragment:fragment` warning no longer appears

### Requirement: Other flagged SDKs are checked
The release runtime classpath SHALL be reviewed for other outdated or flagged SDKs when the fragment version is raised.

#### Scenario: Review recorded
- **WHEN** the fragment version has been raised
- **THEN** the result of the classpath review is recorded in the change's design notes or the pull request

### Requirement: App still builds and passes tests
The app SHALL build and pass all tests after the dependency change.

#### Scenario: Build and tests
- **WHEN** `./gradlew :androidApp:assembleDebug` and `./gradlew :shared:allTests` are run
- **THEN** both succeed
