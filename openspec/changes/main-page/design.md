# Design

## Context

No code is scaffolded yet; this change assumes the scaffold from roadmap milestone 1 and a game screen to start. The architecture in `CLAUDE.md` applies: logic separate from rendering, no Android APIs in `commonMain`, tunable values in config, no literals in composables.

The design system already has what this page is made of: Button, Dialog, Cloud, the five aircraft sprites, the sky sunburst backdrop, and a MainMenu reference composition. The splash page change (issue #2) introduces the app-level screen state and the reduced-motion interface; this change builds on both, or introduces them if it lands first.

## Goals / Non-Goals

**Goals:**

- A backdrop whose behaviour (rotation, cloud drift, one aircraft at a time, the 3-second gap) is plain, unit-testable logic.
- Quitting and the version label without any platform API in `commonMain`.
- A page assembled from existing design-system components.

**Non-Goals:**

- A navigation library.
- Reusing the game engine's spawner for menu decoration.
- Quit behaviour on iOS.

## Decisions

**The backdrop is a small state model, separate from the game engine.** `MenuBackdropState` in the `ui` package holds the stripe rotation angle, three cloud offsets and the aircraft slot. `update(deltaSeconds)` advances it; the composable draws it. It takes a seeded random source so tests are deterministic. Rejected: driving the menu with `GameEngine`, which would couple decoration to game rules and to lives, scoring and collisions that do not apply here.

**The aircraft slot is a two-state machine.** Either `Flying(aircraft)` or `Waiting(secondsLeft)`. When a flying aircraft is fully outside the screen bounds (its position beyond the edge by more than its drawn radius), the slot becomes `Waiting(3s)`; when the wait runs out a new aircraft is created. This makes "3 seconds after the previous aircraft left" and "one at a time" true by construction. The first aircraft appears after the same 3-second wait.

**Aircraft fly straight lines that are guaranteed to cross the screen.** A new aircraft gets a random type from all five, a random livery from `path-1` to `path-6`, a random point on a random edge placed just outside the screen, and a heading towards a random point in the central half of the screen. Aiming at the centre region prevents routes that only clip a corner. Speed is one menu speed per type from config, not the gameplay speed.

**Stripes are the design system's sky sunburst, centred on screen.** Rays alternate `bg-sky` and `sky-300` around the screen centre and are drawn large enough to cover the corners at any rotation. One full turn takes 240 seconds by default. Rejected: a moving bitmap, which would need per-density assets.

**Clouds drift on small closed paths.** Each of the three clouds has a fixed anchor and moves on a slow elliptical path a few dp wide, each with its own period and phase so they never move in step. Anchors are fractions of the screen size and keep the whole cloud on screen.

**Draw order, back to front:** stripes, clouds, passing aircraft with their ground shadow, then the buttons and version label as normal composables above the `Canvas`, then the scrim and dialog. Aircraft live only in the `Canvas`, so they are behind the buttons and receive no input by construction.

**Platform concerns go behind one interface.** `commonMain` declares an `AppHost` with the version name and a `quit()` function; `androidApp` implements it from the package info and by finishing the activity. Back presses are delivered from the host to the current screen through the same boundary, so `commonMain` does not depend on an Android-only back handler. On the main page a back press opens the dialog; with the dialog open it closes it. Rejected: reading the version from a constant in shared code, which drifts from the real build.

**The page is built from design-system components.** Take off is the large primary Button with the `play` icon; Settings is a ghost Button with the `settings` icon; the dialog is the Dialog component on `scrim` with a danger Button for Quit and a ghost Button for Cancel. Dialog copy: headline "Quit?", body "Leave the tower for now?". The version label uses the `label` style in `on-sky-muted`, `space-4` from the bottom and right edges, formatted as `V 1.2.0`.

**The Settings button opens a placeholder page.** Until the settings page has its own issue, Settings navigates to a page with a title and a way back, so the flow and the "returning from settings" behaviour can be built and tested now.

**The backdrop keeps updating while the dialog is open.** Only the scrim is drawn over it. With reduced motion on, `update` is not called and the aircraft slot stays empty.

**Timings are configuration.** Rotation period, cloud drift radius and periods, menu aircraft speeds and scale, and the 3-second gap live in a `MenuBackdropConfig`.

## Risks / Trade-offs

- [A continuously animating menu drains battery] → The backdrop is cheap vector drawing; stop the frame loop when the app is in the background.
- [A slow aircraft on a long diagonal keeps the screen empty-looking for a while afterwards] → Menu speeds are tuned so a crossing takes roughly 6 to 10 seconds; the 3-second gap is fixed by the requirement.
- [Back handling differs per platform and Compose Multiplatform version] → Keeping it behind `AppHost` isolates the difference to the host app.
- [The design system's MainMenu composition shows a wordmark and a best score that this page does not have] → Update that composition as part of this change so the reference matches the product.

## Open Questions

- Whether the page should later carry the Final Approach wordmark. Adding it does not change the backdrop or the quit flow.
