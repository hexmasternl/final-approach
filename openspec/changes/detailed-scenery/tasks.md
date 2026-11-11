# Tasks

## 1. Design system: rules and tokens

- [ ] 1.1 Add `line-fine` (1.5px) and the colour tokens `sand-300`, `soil-500`, `soil-700`, `roof-500`, `foam`, `stone-300` (Day and Night, each with a usage note) to `tokens.json`; verify `node docs/design-system/tools/gen-kotlin.cjs` emits them into `FaDimens` and `FaColors`, and add their Dawn values to `DawnColors` by hand
- [ ] 1.2 Write the scenery detail rules in `world.md` (reference phone, minimum sizes, `line-2` outline, `line-fine` details, four-fill budget, no path colours, no airborne shadow, only flat ground under the HUD band, parked aircraft rules, world zoom), amend `brand-book.md` (strokes, hairline exception) and `components/Scenery.md` (new kinds, construction); verify by reading that no rule contradicts `CLAUDE.md` principles

## 2. Design system: scenery sprites

- [ ] 2.1 Draw the meadow SVGs in `docs/design-system/assets/scenery/` (cow, farmer with pitchfork, bird wings up and wings down, tree with leaf clusters and shadow, hedge, fence, farmhouse, barn, crop-field tile) on the stroke and size rules from 1.2; verify each passes the four-fill budget and uses only tokens from 1.1
- [ ] 2.2 Draw the airfield SVGs (terminal with windows and roof edge, tower with cab, hangar with doors and roof ribs, windsock, car, fuel truck, baggage cart, parked aircraft); verify the parked aircraft is 0.75 of the light prop and has no livery colour
- [ ] 2.3 Draw the island and savanna SVGs (palm, jetty, sailboat, yacht and container vessel with decks, acacia, bush, rock, zebra, elephant) and the wave and wake strokes; verify each passes the same budget
- [ ] 2.4 Add all scenery sprites to `bundle.js` and a Scenery section to `showcase.html` (Day and Night, with the size and stroke rules), regenerate `showcase.pdf`; verify every SVG in `assets/scenery/` appears in both themes
- [ ] 2.5 Extend `gen-kotlin.cjs` with a scenery pass that emits `ui/sprites/ScenerySprites.kt` (paint roles and path parts per sprite); verify regenerating twice produces no diff and the file holds every sprite from 2.4

## 3. World zoom

- [ ] 3.1 Add `LevelDefaults.WORLD_ZOOM = 1.25` and express `HUD_BAND`, `SPAWN_SEPARATION`, `WARNING_DISTANCE`, `SPAWN_MARGIN`, `LEAVE_MARGIN` as base value times the factor; verify with a unit test that each equals its former value × 1.25
- [ ] 3.2 Scale `DefaultAircraftSpecs` speeds and radii and the `EngineConfig` reach radius and minimum point spacing by `WORLD_ZOOM`; verify `AircraftTypeSpecTest` checks the 15% speed order and sprite size = former size × 1.25, and that collision shapes still match sprites (`CollisionShapeTest`)
- [ ] 3.3 Grep `domain/` and `engine/` for other world-unit defaults, including any added by `smooth-flight-paths`, `redraw-flight-path` or the changes for issues #29 and #30, and apply the factor where they describe aircraft or the finger; verify the list of touched values is noted in the pull request
- [ ] 3.4 Add a `WorldViewport` test on an 800 × 360dp screen: each aircraft type's drawn size equals its pre-zoom drawn size, the pick radius is 72dp, runways are at least 24dp wide and helipads at least 40dp across
- [ ] 3.5 Update `docs/game-design.md` (speed table, world size, zoom factor) and `docs/architecture.md` (zoom factor and which values follow it); verify the numbers match `DefaultAircraftSpecs` and `LevelDefaults`

## 4. Scenery data and layouts

- [ ] 4.1 Add the new `SceneryKind`s, mark the flat ones as ground, and add the validator rules (non-ground scenery clear of the HUD band, parked aircraft inside an apron); verify with `LevelConfigValidatorTest` cases that pass and fail each rule
- [ ] 4.2 Re-lay Meadow Field in 1600 × 900: airfield centred and clear of the HUD band, paddock, crop fields, hedges, fences, farmhouse, barn, road, pond, forest edge, parked vehicles and aircraft on the apron; verify `LevelsTest`/`LevelLayoutsTest` pass with unchanged runway and helipad counts, headings and accepted aircraft
- [ ] 4.3 Re-lay Island in 1600 × 900 with the island centred, a wider sea, palms, jetty, apron vehicles and parked aircraft; verify the same tests and that every zone lies on the island
- [ ] 4.4 Re-lay Savanna in 1600 × 900 and move the acacias, bushes and rocks from `LevelBackdrop` into level data, adding the dirt road, waterhole, zebras and elephant; verify the same tests
- [ ] 4.5 Add an engine test that a seeded run of each level gives identical spawns, landings, collisions and score with its scenery list emptied

## 5. Ambient detail

- [ ] 5.1 Turn the bird into a flock of three with a wing phase in `AmbientLife`, and move ambient sizes (cow, farmer, bird, boat) into the ambient config in world units at the minimum sizes of 1.2; verify `AmbientLifeTest`: three birds share one crossing, the wing phase alternates no faster than every 0.25 s, nothing spawns under reduced motion, cows still stay fenced
- [ ] 5.2 Re-tune bird and boat speeds and margins for the larger world; verify with a test that a bird and each boat kind cross the world in the same time as before

## 6. Drawing in Compose

- [ ] 6.1 Add `drawScenerySprite` in `ui/sprites` mapping paint roles to `FaColors` and strokes to `line-2` / `line-fine`; verify by a Compose preview of every sprite next to its SVG in Day and Night
- [ ] 6.2 Draw `SceneryItem`s through the sprites in `AirfieldDrawing.kt` (buildings, windsock, vehicles, parked aircraft, plants, animals) and fields, roads, ponds, beach and waves procedurally from tokens; verify with a grep that `AirfieldDrawing.kt` and `LevelBackdrop.kt` hold no colour, stroke-width or size literal
- [ ] 6.3 Draw cows, farmer, flock and boats with deck and wake from the sprites in `LevelBackdrop.kt`; verify in a preview of each level that the draw order is field, scenery, ambient, zone glows, paths, aircraft and that nothing covers a zone
- [ ] 6.4 Add a debug preview that renders each level with `DayColors`, `NightColors` and, for the savanna, `DawnColors`; verify it produces the screenshots used in 7.1

## 7. Review and device checks

- [ ] 7.1 Run the `design-critic` agent on screenshots of each level in Day and Night (and the savanna in Dawn) and fix what it reports; verify its final report names every scenery element and lists no violation
- [ ] 7.2 Human check on a real phone of about 800 × 360dp and on a large phone: play each level for two minutes; confirm aircraft, runways, paths and landing zones read clearly, aircraft are easy to pick, landings are not noticeably harder, and frame rate holds
- [ ] 7.3 Human check with the system's reduced-motion setting on: confirm no wing, bird, boat or cow moves
