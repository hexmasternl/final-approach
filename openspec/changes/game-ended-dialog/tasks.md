# Tasks

## 1. Engine state

- [x] 1.1 Add `landedByType` to `GameState`, updated on each landing, and verify with `commonTest` unit tests that counts per type, the derived total and the score agree after several landings of mixed types
- [x] 1.2 Verify with a unit test that a landing and a failure in the same update are both counted, and that `update` changes nothing once the game is over

## 2. Dialog

- [x] 2.1 Check the design-system showcase for a Dialog with a stats grid per aircraft type; if missing, add it to the design system (showcase and `components/Dialog.md`) and verify it renders in the showcase
- [x] 2.2 Build the game ended dialog composable from the Dialog component with theme tokens only: type rows from the level config, Total landed, Points, copy by game-over reason; verify in a preview with zero, some and many landings, and with a level that has fewer types
- [x] 2.3 Add the two actions "Try again" (primary) and "Main page" with at least 48dp targets, and verify in a preview that both are visible and labelled
- [x] 2.4 Make the scrim block input, ignore taps outside, and map back press to the main-page action; verify by running the app that nothing behind the dialog responds and that back opens the main page
- [x] 2.5 Apply reduced motion (no drop-in) and verify on a device or emulator with animations disabled

- [x] 2.6 Show `Ads.Banner` (from `level-start-ads`; unit `ca-app-pub-1310013076234295/3427449894` in release, test unit in debug) at full screen width directly under the dialog card on the transparent black scrim, and verify the dialog still lays out correctly when no ad loads and when consent is declined
- [x] 2.7 Depends on `level-start-ads` (SDK, consent, App ID, `com.google.android.gms.permission.AD_ID` permission in the manifest): verify the merged manifest contains the permission before adding the banner

## 3. Wiring

- [x] 3.1 Show the dialog from the game screen exactly when the state is game over (collision or left area) and stop calling `engine.update`; verify by running the app that both failure types open it and the aircraft stand still
- [x] 3.2 Wire "Try again" to the level restart and "Main page" to navigation to the main page, and verify by running the app that restart starts the same level with score and counts at 0 and that the other action shows the main page
- [x] 3.3 Update `docs/game-design.md` (failure and scoring: the game ended dialog) and verify the text matches the behaviour
- [ ] 3.4 Human check: play a level to a collision and to a missed aircraft on a device and confirm the numbers shown match what was landed
