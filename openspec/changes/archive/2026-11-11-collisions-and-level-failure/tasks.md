# Tasks

## 1. Domain and config

- [x] 1.1 Add a collision shape (circles in local frame, plus bounding radius) to each aircraft type spec and verify with a unit test that a rotated shape's world-space circles match hand-computed positions
- [x] 1.2 Add `warningDistance`, `leaveMargin` and the maximum simulation step to the level config, remove any collision/separation distance, and verify nothing else references the removed value

## 2. Collision detection

- [x] 2.1 Implement the shape overlap test in `engine/Collisions` with a bounding-circle broad phase, and verify with unit tests: close but not touching is no collision, touching is, rotation changes the result
- [x] 2.2 Add the swept test along the frame movement and sub-stepping in `GameEngine.update`, and verify with a unit test that a head-on fast pass in one large-delta frame is detected
- [x] 2.3 Skip removed aircraft and include rolling-out aircraft, and verify with unit tests for both
- [x] 2.4 Add `GameOver(reason, aircraftIds)` to `GameState`, set it on collision and stop the simulation, and verify with a unit test that the state is `Collision` with both aircraft and that further updates move nothing

## 3. Leaving the viewport

- [x] 3.1 Add the `hasEnteredViewport` flag and the leave check with margin, and verify with unit tests: leaving after entering is game over (`LeftArea`), an entering aircraft is not flagged, an edge touch within the margin is not flagged, a landed aircraft never triggers it

## 4. Proximity warning state

- [x] 4.1 Derive `warnedAircraft` in the engine update and verify with unit tests: on below the distance, off when separated, both aircraft of a pair, all three of a trio

## 5. Design system and ring

- [x] 5.1 Add the proximity ring (dash pattern, margin, `dur-ring-spin`) to the design system, `docs/design-system/` showcase and `tokens.json`, and verify by opening the showcase
- [x] 5.2 Add the matching values to the Compose theme (`FaDimens`, durations) and verify no literal remains in the ring composable
- [ ] 5.3 Draw the ring in the effects layer for every warned aircraft with the spin and the transitions, and verify in a preview or by running that two close aircraft both show a spinning dashed red ring that goes away on separation
- [ ] 5.4 Add the reduced-motion variant and verify by running with reduced motion on that the ring is dashed and static (human device check)

## 6. Docs

- [x] 6.1 Update the failure rules in `docs/game-design.md` and the failures line in `CLAUDE.md` (and `.github/copilot-instructions.md` if it repeats it), and verify that no remaining text calls a distance a collision
- [x] 6.2 Run `./gradlew :shared:allTests` from `app/src` and verify all tests pass
