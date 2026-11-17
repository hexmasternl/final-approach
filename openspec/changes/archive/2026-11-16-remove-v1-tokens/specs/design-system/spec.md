## MODIFIED Requirements

### Requirement: The app keeps building
The design system SHALL define no token marked `deprecated`, and no code under `app/src` SHALL read a token that has been removed. Tokens that are still current SHALL keep their name and value.

#### Scenario: No deprecated tokens
- **WHEN** `check-tokens.cjs` is run and `tokens.json` and the generated theme are searched for `deprecated`
- **THEN** the checker passes and neither contains a deprecated token

#### Scenario: No reader of a removed token
- **WHEN** `app/src` is searched for the names of the removed tokens (surface colours, deep shades, numbered line widths, old radii, v1 lips, HUD chip height, v1 toggle sizes, disabled opacity)
- **THEN** there is no match

#### Scenario: Regenerating the theme
- **WHEN** the generator is run and `./gradlew :shared:allTests :androidApp:assembleDebug` is run from `app/src`
- **THEN** the build and all tests pass

#### Scenario: Same look in every theme
- **WHEN** the game screen, tutorial, splash and menu are reviewed in Day, Night and Dawn before and after this change
- **THEN** the design review finds no visible difference in the flight path end dot, proximity ring, tutorial highlight and drag demo, selection ring, splash banner or shadows
