# Design

## Context

The game engine, failure states (#9) and HUD (#10) are not built yet; this change assumes them. `CLAUDE.md` applies: game rules stay out of composables, no Android APIs in `commonMain`, no literals where a token belongs. The Dialog component (`docs/design-system/components/Dialog.md`) already covers modals with a headline, one sentence of body, optional stats and one to three actions, primary first. It freezes the simulation and drops in over `dur-dialog` with `ease-bounce`.

## Goals / Non-Goals

**Goals:**

- The dialog only displays state; counts and totals come from the engine.
- Reuse the Dialog component and the restart and main-page navigation that the HUD options panel (#10) needs.

**Non-Goals:**

- A win or level-completed dialog.
- Persisting scores.

## Decisions

**The engine counts landings per type.** `GameState` gets `landedByType: Map<AircraftType, Int>` next to the score, updated by the engine when a landing is detected. The total landed is derived (`values.sum()`), not stored. The dialog reads the frozen state at game over. Rejected: counting in the UI by watching state changes, which puts game rules in a composable and loses counts on recomposition.

**The dialog is driven by the game-over state.** It is visible exactly when `GameState` reports game over (reason collision or left area, from #9). The reason selects the body copy. Rejected: a separate "show dialog" flag in the UI, which could drift from the engine state.

**Rows come from the level config.** The per-type rows use the aircraft types that the level is configured to spawn, the same list the HUD uses, in roster order (light prop, turboprop, jet, jumbo, helicopter). Zero counts are shown. Rejected: only types that landed, because a row that appears and disappears hides what was possible.

**Layout follows the Dialog component.** Headline in `display-lg`, body in `body-lg` `ink-muted`. Stats are a two-column grid of `surface-sunken` wells: one well per type with the aircraft sprite or its name as `label` and the count in `score`, followed by a Total landed well and a Points well. Type names are paired with the sprite, never colour alone. Actions: primary Button "Try again" (restart), ghost Button "Main page" (return). Spacing, radius and outlines come from `FaDimens` and `FaColors`; no literals. If the showcase has no stats grid per aircraft type, add it to the design system first (see `CLAUDE.md`).

**Copy.** Headline "Game over". Body for a collision: "Two aircraft got too close." Body for a missed aircraft: "An aircraft left without landing." Both are short, name the cause and do not blame. Strings live with the other UI strings, not in the composable logic.

**Restart and return reuse existing navigation.** Restart calls the same level restart as the options panel in #10 and resets score and counts by creating fresh engine state. Return navigates to the main page. Rejected: duplicating reset logic in the dialog.

**Modal behaviour.** The scrim consumes pointer input so the field behind gets none, and the frame clock does not call `engine.update` once the game is over. Back press is delivered through `AppHost` (as on the main page) and maps to the return action. Reduced motion, read through the shared reduced-motion interface from the splash page change, replaces the drop-in with an instant appearance.

**A banner ad sits directly under the dialog.** The scrim is a transparent black backdrop. An AdMob banner (unit "after-level-finished" `ca-app-pub-1310013076234295/3427449894` in release, Google's test banner unit in debug) is anchored under the dialog card at the full screen width, as an anchored adaptive banner. It comes from the `Ads.Banner` composable that the `level-start-ads` change adds to `AppHost`, so shared code stays free of the SDK. It never covers a landing zone or a button, requests nothing before consent allows ads (`level-start-ads`), and when no ad loads the dialog looks as it does without a banner.

## Assumptions

Each can be overruled by the issue author.

- "Return back to the main page" goes to the main page, not to level selection (#14), even once levels exist.
- Total points is the score at game over; the dialog adds no bonus.
- Rows show the aircraft types configured for the level, including zeros.
- Button labels "Try again" and "Main page"; headline "Game over"; the body copy above.
- The dialog cannot be dismissed by tapping outside; back means return to the main page.
- No "new highscore" message.

## Risks / Trade-offs

- [The failure states (#9) and score (#10) do not exist yet and their names may differ] → the tasks use the state names from those issues; adjust when they land.
- [A game ending the same frame as a landing] → the engine applies the landing before the failure check in one update, so the counts include it; covered by a unit test.
- [The interstitial ad (#13, `level-start-ads`) hooks into restart] → "Try again" goes through the same restart call, so the ads change can intercept it; this change only shows the banner.
