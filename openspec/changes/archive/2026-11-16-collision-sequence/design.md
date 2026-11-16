# Design

## Context

On a collision `GameEngine` stops and reports `GameStatus.GameOver(Collision, [a, b])`; `update` does nothing after that, and input is already ignored (`GameEngine` rejects input once the status is game over). `GameScreen` shows `GameEndedDialog` as soon as the status is game over. Proximity rings are drawn in the main `Canvas` from `ProximityRings` with `drawProximityRing`, always in `colors.warning`, spun by a `clock` that keeps running after game over. The `Hud` is a separate composable above the `Canvas`, and the dialog sits above the `Hud`.

The theme in `ui/theme` is generated from `docs/design-system/tokens.json` by `docs/design-system/tools/gen-kotlin.cjs`. There is no 1000 ms duration and no opaque blackout colour yet: `scrim` is translucent.

## Goals / Non-Goals

**Goals:** a 2000 ms sequence (1000 ms fade, 1000 ms hold) between collision and dialog that isolates the two colliding aircraft with red dashed rings. Timing deterministic and unit-tested. No engine change.

**Non-Goals:** sequence for leaving the viewport, tutorial, sound, crash effects, dialog changes.

## Decisions

### 1. A pure timeline class driven by the existing frame loop
Add `CollisionSequence` in `ui/game`: plain Kotlin, created when the status becomes `GameOver(Collision, ids)` and dropped when the status leaves game over (restart). It takes `advance(deltaMs)` from the `withFrameNanos` loop in `GameScreen` and exposes `elapsedMs`, `blackoutAlpha` (`FaMotion.easeSway` over `durCollisionFadeMs`, or 1 at once with reduced motion), and `dialogVisible` (true from `durCollisionFadeMs + durCollisionHoldMs`, or `durCollisionHoldMs` with reduced motion). It also stores the two aircraft ids and the ring angle captured at the collision.

*Rejected:* `Animatable` / `animateFloatAsState` plus `delay` in a `LaunchedEffect`. It works but runs on a second clock next to the frame loop and is awkward to test. *Rejected:* timing in `GameEngine`. The engine owns game rules and stays frozen; this is presentation only.

### 2. A separate overlay above the HUD
Compose a `CollisionOverlay` between `Hud` and the dialog in `GameScreen`. It is a full-screen `Canvas` that draws, in order: a rect in `colors.blackout` at `blackoutAlpha`, then the two colliding aircraft bodies (same drawing as the aircraft layer, no ground shadow, which would not show on the blackout), then their red rings. It consumes all pointer events, so the HUD cog and the field cannot be tapped. The dialog is shown only when `dialogVisible`; for `LeftArea` it is shown at once as today.

*Rejected:* drawing the blackout in the main `Canvas`. The HUD is a composable above that `Canvas`, so it would stay uncovered.

To avoid duplicating aircraft drawing, factor the per-aircraft body drawing out of `drawAircraft` into a function both use.

### 3. Ring colour and frozen angle
Give `drawProximityRing` a `dashColor` parameter (`colors.warning` for warnings, `colors.danger` for collisions). Collision rings are drawn at scale 1 and alpha 1 with the angle captured in `CollisionSequence` at the collision (0 with reduced motion, as today). The normal ring layer skips the two colliding ids, so no yellow ring sits under the red one.

*Rejected:* keeping the rings spinning. The game is frozen; a still tableau reads as "this is the moment it happened", and dashes plus isolation already carry the meaning without colour.

### 4. New design-system tokens
- `blackout`: `#03050f`, opaque, same in Day and Night. A near-black navy, in line with "never pure black". *Rejected:* `scrim` (translucent, does not isolate) and `#000000`.
- `dur-collision-fade`: 1000 ms. `dur-collision-hold`: 1000 ms. Two tokens rather than one shared `1000ms`, because they are tuned independently.
- Easing: existing `ease-sway` (symmetric ease-in-out). *Rejected:* `ease-out-soft`, which darkens most of the way in the first ~200 ms and feels like a cut.

Add them to `tokens.json`, regenerate the Kotlin theme, and add a collision variant (red ring, `danger` chip with the warning icon and a word) to the Proximity ring card in `showcase.html`.

### 5. Back press
`BackHandler` already sends the player to the main page whenever the status is game over. That stays, so Back during the sequence leaves at once.

## Assumptions

- Rings freeze during the sequence (not spinning).
- A tap does not skip the sequence.
- Reduced motion: blackout at once, dialog after the 1000 ms hold (1000 ms total, not 2000 ms).
- The blackout is fully opaque at the end of the fade and stays behind the dialog.
- The ring turns red instantly (no colour crossfade); a colliding aircraft that had no ring gets one at full size at once.
- Back during the sequence goes to the main page.
- Tutorial collisions get no sequence (they never reach game over).

## Risks / Trade-offs

- [The app goes to the background mid-sequence] → the frame loop stops, so the sequence resumes where it was. Acceptable.
- [Two aircraft drawn twice, once under the blackout and once above] → negligible cost for two sprites.
