## 1. Zoom factor

- [x] 1.1 Set `LevelDefaults.WORLD_ZOOM` to 1.5 (after `detailed-scenery` task 1 has added it) and derive each level world from it; verify with a `commonTest` that every world is 1920 x 1080
- [x] 1.2 Check every scaled default (aircraft radius and speed, warning distance, spawn separation and margins, reach radius, point spacing, HUD band) is its zoom-1 value times `WORLD_ZOOM`; verify with tests for crossing time and warning distance
- [x] 1.3 Re-centre the three layouts in the larger worlds, clear of the HUD band; verify `LevelLayoutsTest` and the level validator pass

## 2. Backdrop

- [x] 2.1 Make the backdrop of each level cover the whole larger world and spread or thin the scenery in the border; verify with previews of the three levels on 800 x 360dp that no edge shows empty background
- [ ] 2.2 Check on 800 x 360dp that runways stay at least 24dp wide and helipads at least 40dp across (the `world-scale` readability rule); if not, say so on the issue before changing the factor

## 3. Docs and checks

- [x] 3.1 Update `docs/game-design.md` and `docs/architecture.md` with the 1.5 factor and 1920 x 1080 worlds; verify they match the code
- [ ] 3.2 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug`; verify both pass
- [ ] 3.3 Human check: play each level on a phone and decide whether the zoom feels right
