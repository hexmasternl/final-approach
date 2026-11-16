# Tasks

## 1. Zoom

- [x] 1.1 Set `LevelDefaults.WORLD_ZOOM` to 2.5 and write `TOUCHDOWN_DISTANCE` as 32 × `WORLD_ZOOM`; verify `WorldZoomTest`, `AircraftTypeSpecTest`, `EngineConfigTest` and `RunwayLeadInTest` pass with the new values
- [x] 1.2 Make `SceneryDefaults.PARKED_AIRCRAFT_SIZE` a fixed 67.5; verify every parked aircraft stands on its apron and is smaller than any aircraft in play

## 2. Layouts

- [x] 2.1 Move every level's runways, helipads, island, paddock and scenery to the middle of the 3200 × 1800 world; verify `LevelLayoutsTest` and `LevelsTest` pass
- [x] 2.2 Lengthen roads and tracks to the world edges and add scenery to the new surroundings of each level; verify every level validates and no standing scenery is in the HUD band

## 3. Drawing

- [x] 3.1 Draw runway and helipad markings and their text at the airfield's scale, and scenery strokes at 0.8 world units per dp; verify the drawing code compiles and uses no literal sizes

## 4. Tests and docs

- [x] 4.1 Update tests whose positions or gaps were written for the former zoom (collisions, warnings, viewport, layouts); verify the domain, engine, input and ambient tests pass
- [x] 4.2 Update `docs/game-design.md`, `docs/architecture.md` and `docs/design-system/world.md`; verify no doc still names a 1600 × 900 world or a zoom of 1.25 as current
- [ ] 4.3 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug` in `app/src`; verify both succeed
- [ ] 4.4 Human check: play every level on a phone and confirm the airfield is clearly smaller with plenty of open space around it, aircraft look and move as before, and landings on every runway and helipad still work
