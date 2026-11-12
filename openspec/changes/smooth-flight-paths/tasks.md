# Tasks

## 1. Path smoothing

- [x] 1.1 Add a smoothing config data class (tolerance, lookahead, iterations) and verify no magic numbers remain in engine code
- [x] 1.2 Implement path smoothing (resample, Chaikin, minimum radius from turn rate) with endpoints pinned; verify `commonTest` cases for sharp corner, gentle path and pinned endpoints pass
- [x] 1.3 Apply smoothing when a drag completes in the input layer and verify a drawn zig-zag is stored smoothed

## 2. Strict path following

- [x] 2.1 Change `PathFollower` to use monotonic progress and a lookahead point; verify a test with a 150° corner never loops and stays within tolerance
- [x] 2.2 Verify path end continues in last heading and a smoothed path ending on a runway/helipad still lands (tests)
- [x] 2.3 Update `docs/game-design.md` with the smoothing and following behavior and verify the doc matches the spec
