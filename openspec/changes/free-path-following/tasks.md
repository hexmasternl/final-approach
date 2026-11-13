## 1. Engine

- [ ] 1.1 Rewrite `PathFollower` to move along the path by arc length with the heading along the segment, no turn limit and no lookahead; verify with a `commonTest` that an aircraft of every type stays within tolerance of a path with a 150 degree corner and flies straight on after the end
- [ ] 1.2 Strip the turn-radius flight from `PathSmoother` and the unused fields from `PathSmoothingConfig`, resample at a configured fixed spacing; verify with `commonTest` that a gentle path stays within tolerance and a sharp corner is rounded without a wide loop
- [ ] 1.3 Update `PathDrawing` and `RunwayLeadIn` callers; verify existing landing and lead-in tests still pass and a runway landing after a sharp turn still ends on the centre line

## 2. Docs

- [ ] 2.1 Update `docs/game-design.md`, `docs/architecture.md`, `CLAUDE.md` and `.github/copilot-instructions.md` so turn rate no longer limits turning; verify by searching for "turn rate" and "turning radius"
- [ ] 2.2 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug`; verify both pass
- [ ] 2.3 Human check: draw sharp zig-zags for a jet and a jumbo on a device and confirm they follow the line
