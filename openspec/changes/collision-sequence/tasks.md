# Tasks

## 1. Design system

- [x] 1.1 Add `blackout` (`#03050f`, same in Day and Night), `dur-collision-fade` (1000ms) and `dur-collision-hold` (1000ms) with usage text to `docs/design-system/tokens.json`. Verify: the file parses and the tokens appear in the showcase token tables.
- [x] 1.2 Add a collision variant to the Proximity ring card in `docs/design-system/showcase.html`: red (`danger`) dashed ring, not spinning, with a `danger` chip pairing the warning icon and a word. Regenerate `showcase.pdf` if the tooling allows. Verify: open the showcase and see both variants.
- [x] 1.3 Regenerate `ui/theme` with `docs/design-system/tools/gen-kotlin.cjs` so `FaColors.blackout`, `FaMotion.durCollisionFadeMs` and `FaMotion.durCollisionHoldMs` exist. Verify: `./gradlew :shared:allTests` builds.

## 2. Timeline

- [ ] 2.1 Add `CollisionSequence` in `ui/game` (design decision 1): `advance(deltaMs)`, `blackoutAlpha`, `dialogVisible`, colliding ids, captured ring angle, reduced-motion mode. No literals; durations and easing from `FaMotion`.
- [ ] 2.2 `commonTest` tests: alpha 0 at start, between 0 and 1 at 500 ms, 1 at 1000 ms; no dialog at 1500 ms, dialog at 2000 ms; with reduced motion alpha 1 at 0 ms and dialog at 1000 ms. Verify: `./gradlew :shared:allTests` passes.

## 3. Rendering and wiring

- [ ] 3.1 Give `drawProximityRing` a dash colour parameter and factor single-aircraft body drawing out of `drawAircraft` in `GameScreen.kt`. Verify: game looks unchanged with yellow warning rings.
- [ ] 3.2 In `GameScreen`, create a `CollisionSequence` when the status becomes `GameOver(Collision, …)`, advance it from the frame loop, and drop it when the status leaves game over. Skip the colliding ids in the normal ring layer. Verify: Try again after a collision shows a normal field.
- [ ] 3.3 Add `CollisionOverlay` between `Hud` and the dialog: blackout rect, the two aircraft bodies, red rings at full size with the frozen angle; consumes all pointer input. Show `GameEndedDialog` only when `dialogVisible` for collisions, at once for `LeftArea`. Verify: a `GamePreviews` preview of the overlay at mid-fade and at the end state.
- [ ] 3.4 Update Game over in `docs/game-design.md` with the collision sequence (red rings, 1 s fade to black isolating the two aircraft, 1 s hold, then the dialog; reduced motion; leaving the viewport unchanged). Verify: the doc matches the specs.
- [ ] 3.5 Human check on a device: a collision shows red rings, a 1 s fade, a 1 s hold, then the dialog with the ad banner; taps and the HUD cog do nothing during it; Back goes to the main page; leaving the viewport shows the dialog at once; with reduced motion (Remove animations) the blackout is instant and the dialog follows after 1 s; a tutorial collision restarts the step without a blackout.
