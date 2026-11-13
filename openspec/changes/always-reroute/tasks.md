## 1. Engine

- [ ] 1.1 Change `PathDrawing.begin` so a `ClearedToLand` aircraft gets an empty unlocked path and status `Flying`, keeping position and heading; verify with a `commonTest` for an airplane and a helicopter, and that `RollingOut` is unchanged
- [ ] 1.2 Check `GameEngine.clearPath`, `beginPath`, `extendPath`, `endPath` and `select` with a cleared aircraft; verify with an engine test: clear, touch, drag a new path to another zone, and see it land there
- [ ] 1.3 Test that a tap on a cleared aircraft cancels the landing and it flies on straight, and that a re-routed path entering a zone clears it again

## 2. UI and docs

- [ ] 2.1 Check that the white-flash and the locked-path drawing in `ui/game/FlightPathDrawing.kt` stop when the path is removed; verify with a preview or on a device that no locked path remains after a cancel
- [ ] 2.2 Update the core rules in `docs/game-design.md` (a new path always replaces the old one until touchdown); verify the doc matches
- [ ] 2.3 Run `./gradlew :shared:allTests`; verify it passes
- [ ] 2.4 Human check: clear an airplane and a helicopter, then re-route each before it lands
