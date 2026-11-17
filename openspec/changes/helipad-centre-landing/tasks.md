## 1. Engine

- [x] 1.1 In `engine/PathDrawing.kt`, on a helipad hit smooth `points + hit.entryPoint + zone.area.center` so the path ends on the helipad centre; keep the runway branch as it is and update the KDoc of `extend`

## 2. Tests

- [x] 2.1 `PathDrawingTest`: helicopter paths entering one helipad zone from the north, east, south and west each end exactly on the helipad centre and clear the helicopter to land
- [x] 2.2 `PathDrawingTest`: a helicopter already inside the zone whose first drag point is inside it gets a path that ends on the centre and is cleared to land
- [x] 2.3 `PathDrawingTest`: a path entering at an angle to the line towards the centre has no sharp corner (no turn between consecutive segments above a small bound) on its final leg
- [x] 2.4 `PathDrawingTest` / `ClearanceEventTest`: the path is locked at the moment of entry and further points are ignored; the clearance event fires once
- [x] 2.5 Check that runway clearance tests (`RunwayLeadInTest`, the runway cases in `PathDrawingTest`) pass unchanged, refresh `SeededRunBaselineTest` only if it covers a helipad landing, and run `./gradlew :shared:allTests`

## 3. Docs

- [ ] 3.1 `docs/game-design.md`: the clearance rule says a helipad path continues from the entry point to the pad centre and ends there; the helicopter landing paragraph says the sequence takes place on the centre
- [ ] 3.2 `CLAUDE.md` and `.github/copilot-instructions.md`: next to the runway snap, say a helipad path ends on the pad centre
