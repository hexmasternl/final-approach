# Tasks

The visual reference for every task is `reference/final-approach-v2.html` in this change, with its sources `reference/world.cjs`, `reference/build.cjs` and `reference/tokens.css`.

## 1. Tokens

- [x] 1.1 Add the material, accent and `paper` ramps to `tokens.json` with Day and Night values from `reference/tokens.css`, step roles from design Decision 2 and a usage note per step; keep every existing token name and value; verify with a script that every pre-change token name still resolves to its pre-change value
- [x] 1.2 Add Dawn values for the ramps that differ at dawn, the single tokens `window`, `beacon`, `muzzle`, `shadow`, `shadow-opacity`, and the line ranks `line-hero`, `line-zone`, `line-ui`, `line-ui-sm`, `line-detail`; verify each has a usage note
- [x] 1.3 Add Nunito to the fonts and add the body, caption and row styles in Nunito; move `label`, `readout` and `callsign` to weight 700 or heavier; verify no text style in Inconsolata is lighter than 700 and none in Nunito lighter than 600
- [x] 1.4 Add the v2 size, radius and elevation tokens (strip height 40, lip heights, contact shadow, nested radii for small, medium, large and dialog) and mark the v1-only tokens `"deprecated": "<replacement>"`; verify every deprecated token names an existing replacement
- [x] 1.5 Teach `tools/gen-kotlin.cjs` to read ramps, line ranks and the deprecated marker (emit a KDoc note that names the replacement; not `@Deprecated`, because the v1 world still reads these on purpose); run it and verify `./gradlew :shared:allTests` and `:androidApp:assembleDebug` pass with no hand-written app code changed

## 2. Principles and rules

- [ ] 2.1 Rewrite the principles in `brand-book.md`: three general principles, W1 to W9, U1 to U7, each with its identifier; rewrite the Colour, Edges, depth and shape, States and Type sections for ramps, line ranks, moulded controls and Nunito; verify by grep that "Outline everything", "Flat and bright" and "four fill" no longer occur in `docs/design-system`
- [ ] 2.2 Rewrite the Scenery, Landing zones and layer sections of `world.md`: height tiers with wall and shadow sizes, the single ground-shadow layer, which kinds rotate, flat-ground edging, the transition bands with their widths, mown airfield versus countryside, minimum sizes; verify every rule cites its W identifier
- [ ] 2.3 Update `docs/art-style.md`, `compose.md` and the design-system sections of `CLAUDE.md` and `.github/copilot-instructions.md` for v2, including one line that the app still renders v1 until the port; verify the two instruction files say the same

## 3. Scenery sprites

- [ ] 3.1 Extend the sprite format in `bundle.js` with a height tier, a shadow silhouette and a rotatable flag, and replace the generator's four-fill and colour checks with the checks of design Decision 5; verify each check with one failing sample sprite
- [ ] 3.2 Carry over from `reference/world.cjs`: tree (three sizes), pine (two sizes), bush, rock, hay bale, farmhouse, barn, silo, cow, car, fuel truck, windsock, control tower, terminal, hangar, cloud, with ramp steps as paint keys; verify the generated SVGs match the reference tiles side by side
- [ ] 3.3 Draw the remaining meadow and airfield sprites on W1 to W9: farmer, bird (two wing frames), baggage cart, fence piece, hedge piece, crop-field tiles (wheat, green, ploughed), parked aircraft in `tin` tones; verify each passes the generator checks
- [ ] 3.4 Draw the island sprites: palm, jetty, sailboat, yacht, container vessel, wave, wake, and the beach transition; verify each passes the generator checks
- [ ] 3.5 Draw the savanna sprites: acacia, zebra, elephant, savanna bush and rock variants, waterhole and dirt-road transitions; verify each passes the generator checks
- [ ] 3.6 Regenerate `assets/scenery/*.svg`; verify every sprite in `bundle.js` has an SVG and no orphan SVG remains

## 4. Ground and landing zones

- [ ] 4.1 Rebuild `fa-runway` (two-way and one-way) and `fa-helipad` in `bundle.js` from the reference: shoulder, fringe, markings, lights, same footprint; update `components/Runway.md` and `components/Helipad.md`; verify the asphalt rectangle and helipad circle measure the same as v1
- [ ] 4.2 Add ground pieces to `bundle.js` with their transition bands: mown airfield lawn, meadow patches, tufts and flowers, road, dirt track, taxiway, apron, car park, pond, field; document them in a new `components/Ground.md`; verify no ground piece is stroked in `outline`

## 5. Components

- [ ] 5.1 Rebuild Button, IconButton, Toggle and SegmentedControl in `bundle.css` from `reference/build.cjs` under `fa-` class names reading token variables only, with pressed, focus and disabled states; rewrite their guideline files; verify by grep that `bundle.css` holds no colour or size literal outside token definitions
- [ ] 5.2 Rebuild Chip as the flat tag; add FlightStrip and Sticker; rewrite HudBar, ScoreCounter, SpawnCounter, LivesMeter, NextAircraftHint, StreakBadge and ScorePopup guidelines on them, noting the old names; verify the HUD reference row is 40dp high and only the pause button has a lip
- [ ] 5.3 Rebuild Panel, Dialog (portrait and landscape boarding pass, with the stamp) and CoachMark; rewrite their guidelines; verify the landscape game-over dialog fits 800 by 450dp and 640 by 360dp with system bars and a 50dp banner
- [ ] 5.4 Rebuild LevelCard with a level miniature drawn from the level's own ground pieces, a best-score chip and the locked state; rewrite its guideline; verify the three levels' cards in Day and Night

## 6. Showcase

- [ ] 6.1 Rebuild `showcase.html` for v2: principles, ramps, line ranks, type, scenery by landscape, ground and landing zones, components, and reference screens for all three levels, the main menu, pause, game over and level select, with the Day, Night and Dawn switch; verify no deprecated token is shown
- [ ] 6.2 Have the design-critic agent review the showcase game screens in Day, Night and Dawn against W1 to W9 and U1 to U7 and fix what it finds; verify its final report names no violation and confirms aircraft, paths and zones read first
- [ ] 6.3 Regenerate `showcase.pdf` and rebuild the visuals in `visuals/` that show scenery or UI; verify each PNG was regenerated from its source
- [ ] 6.4 Confirm nothing that must stay changed: verify `git diff development -- docs/design-system/assets/aircraft docs/design-system/assets/icons` is empty and the duration, easing, zIndex and path token values are equal to `development`
- [ ] 6.5 Human check: publish the updated design system to https://claude.ai/artifact/WPS86AMwfU8Ph1ehK5kdpL and confirm it matches `showcase.html`
- [ ] 6.6 Create the follow-up issue for porting the app's world to v2 (scenery sprite drawing, runway and helipad drawing, level backdrops and miniatures, world-facing specs, removal of deprecated tokens); verify it links back to #76

## 7. App UI components

- [x] 7.1 Bundle Nunito (600, 700, 800) in `composeResources`, move the body, caption and row text styles to it in `FaTypography`, and expose the v2 elevation values (lip heights, contact shadow, nested radii) through the theme; verify no text style in Nunito is lighter than 600 and `:shared:allTests` passes
- [x] 7.2 Add the moulded-surface and flat-surface primitives of design Decision 8 in `ui/components`, with pressed, focused and disabled states and reduced-motion handling; rebuild `FaButton` (primary, secondary, ghost, danger; small, medium, large; icon) on them; verify in previews against the reference page and by grep that the files hold no colour, size or duration literal
- [x] 7.3 Rebuild `FaToggle` and `FaSegmentedControl` on the primitives, keeping their semantics (role, state description, 48dp touch target); verify in the settings previews in Day and Night
- [x] 7.4 Rebuild the HUD parts as flight strips and stickers (score, best, spawn counter, lives, next aircraft, streak, score popup, pause button); verify `HudFitTest` passes with the 40dp strip height and that only the pause button draws a lip
- [x] 7.5 Rebuild `FaDialog` as the boarding pass (header band with eyebrow and title, body, perforation, stub with stats and actions; vertical perforation in the compact landscape layout) and the panel surface; verify `FaDialogLayoutTest` passes and the dialog previews fit 640 by 360dp with system bars and a 50dp banner
- [x] 7.6 Move the pause dialog and the game-ended dialog onto the new `FaDialog` with their header tone, eyebrow and, for game ended, the reason stamp; verify every button, label, stat and back behaviour named in the `pause-menu` and `game-ended-dialog` specs is still present and behaves the same
- [x] 7.7 Port the remaining users: level tiles (moulded card, chips, locked state), coach marks, the settings page and the main page; verify each screen's preview in Day and Night and that `level-selection`, `settings-page`, `main-page` and `tutorial-overlays` scenarios still hold
- [ ] 7.8 Run `./gradlew :shared:allTests :androidApp:assembleDebug`; then have the design-critic, android-critic and play-policy-critic agents review the branch and fix what they find; verify each agent's final report names no open finding
- [ ] 7.9 Human check: play one level on a phone in portrait and landscape and confirm the HUD, pause dialog, game-ended dialog, level select and settings read well over the field
