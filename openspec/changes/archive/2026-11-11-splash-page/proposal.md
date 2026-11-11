# Proposal

GitHub issue: #2

## Why

The app has no opening moment: nothing introduces the game by name before the menu appears. A splash page gives Final Approach a recognisable first impression and sets the cartoon tone before the player reaches the main page.

## What Changes

- Add a splash page that is the first screen shown when the app starts.
- Draw a sunset scene: a sky in deep orange bands with a low sun, and a dark silhouette of an airport with an air traffic control tower along the bottom.
- Animate an airplane that enters from the left towing a banner reading "Final Approach". The banner breaks loose at the centre of the screen and stays there, waving slowly like a flag, while the airplane flies out on the right.
- Leave the splash page automatically 2 seconds after the banner has settled, or immediately when the player taps the screen.
- Show the settled end state straight away when the device has reduced motion enabled.
- Add a sunset colour set to the theme and to the design system, which today has Day and Night only.

Out of scope: sound and music, using the splash page as a loading screen, and the main page itself (issue #3).

Depends on: the project scaffold (roadmap milestone 1). Until the main page exists, the splash page hands over to whatever screen the app shows first today.

## Capabilities

### New Capabilities

- `splash-page`: the opening screen of the app: its sunset scene, the banner-tow animation, how it scales, how it behaves with reduced motion, and how the player leaves it.

### Modified Capabilities

None.

## Impact

- `shared/` (`commonMain`, `ui` package): a new splash screen, its timeline logic, and scene drawing on a Compose `Canvas`.
- `ui/theme`: new sunset colour tokens and splash timing constants.
- App start flow: the splash page becomes the first screen and hands over to the next one.
- A small platform interface for the device's reduced-motion setting, implemented in `androidApp`.
- Design system (`docs/design-system`): the sunset palette and the side-view airplane and banner are added as tokens and assets.
- No new third-party dependencies.
