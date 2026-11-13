## 1. Domain

- [x] 1.1 Add `domain/AircraftSpeeds.kt` with the five default speeds written as previous value times fraction, and make `DefaultAircraftSpecs` read them with no speed literal left; verify by search and a `commonTest` that reads 20, 36.67, 50, 66.67, 65
- [x] 1.2 Halve the jumbo radius to 22 and check its collision shape follows the sprite; verify with a test that the jumbo shape bounds are half the old ones and other radii are unchanged
- [x] 1.3 Narrow the speed ordering and gap tests to helicopter..jet, and fix any test that depended on the old speeds or the old jumbo size; verify `./gradlew :shared:allTests` passes

## 2. Docs and checks

- [x] 2.1 Update the aircraft roster and speed table in `docs/game-design.md` (new speeds, smaller jumbo, parameter file); verify the table matches the code
- [x] 2.2 Check the rollout length and spawn separation still look right with the slower aircraft (landing speed follows the light prop); verify with the existing rollout tests
- [ ] 2.3 Human check: play each level and confirm the speeds and the smaller jumbo feel right
