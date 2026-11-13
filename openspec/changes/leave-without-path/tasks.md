# Tasks

## 1. Engine leave rule

- [x] 1.1 In `engine/GameEngine.kt`, make `hasLeft` also require that the aircraft's path is empty, and update its KDoc and the `WorldConfig` KDoc in `domain/LevelConfig.kt` to say only an aircraft without a path leaves. Verify: `./gradlew :shared:allTests` still compiles and existing tests pass.
- [x] 1.2 In `commonTest` `engine/GameEngineTest.kt`, add a test per row of the rule table, using `engine.place` with `hasEnteredViewport = true` unless stated: (a) on a path that runs beyond the leave margin and back in, the level continues to the end of the path; (b) on a path whose last point lies beyond the leave margin, the status becomes `GameOver(LeftArea, [id])` in the update in which the path ends, and not before; (c) without a path, flying out still ends the level with `LeftArea` (the existing test keeps passing); (d) a path that ended inside, then flying straight out, ends the level; (e) an aircraft that has not entered yet, without a path, outside, does not end the level (existing test). Verify: `./gradlew :shared:allTests`.
- [x] 1.3 Add tests for path replacement and removal outside: a tap (`clearPath`, as `PathGesture` does on a down without drag) on an aircraft beyond the margin ends the level at the next `update`; a new path drawn just before leaving keeps it in play while that path lasts. Verify: `./gradlew :shared:allTests`.

## 2. Docs

- [x] 2.1 Update `docs/game-design.md` (the miss rule under the rules and the "Left the area" failure) to say an aircraft is lost when it is beyond the leave margin with no path, including when its path ends there. Verify: read the two passages against the spec delta.
- [ ] 2.2 Update the failure rule in `CLAUDE.md` and `.github/copilot-instructions.md` to the same wording ("an aircraft outside the level with no path"). Verify: both files state the same rule as `docs/game-design.md`.

## 3. Check

- [ ] 3.1 (Human) On a phone wider than 16:9, draw a path from an aircraft into the side band past the world edge and back to a runway; the level continues and the aircraft lands. Draw a path that ends in the side band; the level ends with "An aircraft left without landing." when the aircraft reaches the end.
