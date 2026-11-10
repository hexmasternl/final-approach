# Tasks

## 1. Theme and configuration

- [x] 1.1 Add the `SunsetColors` set (`sunsetSkyTop`, `sunsetSkyUpper`, `sunsetSkyLower`, `sunsetSkyHorizon`, `sunsetSun`, `sunsetSilhouette`, `sunsetTowerLight`) to `ui/theme` and verify the module compiles with no colour literal outside the theme
- [x] 1.2 Add `SplashConfig` with airplane speed, banner and tow-line length, wave amplitude and period, and the 2-second hand-over delay, and verify the defaults are used by nothing but the splash code
- [x] 1.3 Add the sunset tokens to the design system (`tokens.json` and the brand book's colour section) and verify the showcase page lists them

## 2. Timeline logic

- [x] 2.1 Implement `SplashTimeline` as a pure function of elapsed time and `SplashConfig` returning phase, airplane position, banner position, tow-line state and wave phase; verify with unit tests in `commonTest` that the airplane starts off-screen left and ends off-screen right
- [x] 2.2 Implement the release rule and verify with a unit test that the banner detaches exactly when its centre reaches the horizontal centre and never moves away from the centre afterwards
- [x] 2.3 Implement the settled state and hand-over time and verify with unit tests that hand-over is due 2 seconds after settling, and that the reduced-motion timeline starts settled with zero wave amplitude

## 3. Scene drawing

- [x] 3.1 Draw the sky bands and sun on a `Canvas` and verify in a preview that the sky fills the screen at phone portrait and landscape sizes
- [x] 3.2 Draw the airport silhouette with the control tower from a normalised path anchored to the bottom edge, and verify in previews at a narrow and a wide size that it keeps its proportions and the tower is the tallest shape
- [x] 3.3 Draw the side-view light prop and the tow line in the design-system style and verify in a preview that it reads as an airplane at splash size
- [x] 3.4 Draw the banner with the text "Final Approach" in the display font and the sliced wave, and verify in a preview that the text stays readable at the peak of the wave
- [x] 3.5 Add the side-view airplane and the banner to the design system's assets with a usage note, and verify they appear in the showcase page

## 4. Screen and app flow

- [x] 4.1 Build the splash screen composable that advances the timeline from the frame clock and draws the scene, and verify by running the app that the airplane tows the banner in, the banner settles at the centre and waves, and the airplane leaves on the right
- [x] 4.2 Make the splash page the first screen and call `onFinished` on the automatic hand-over and on a tap anywhere; verify by running the app that it advances by itself after the banner settles and immediately on a tap
- [x] 4.3 Add the reduced-motion interface in `commonMain` with its implementation in `androidApp`, and verify on a device or emulator with animations disabled that the banner is centred and still, no airplane appears, and the page still advances
- [ ] 4.4 Update `docs/architecture.md` with the screen flow and the reduced-motion interface, and verify the described names match the code

## 5. Integration check

- [ ] 5.1 Human check on a real device: start the app cold on a small and a large phone, in every supported orientation, and confirm the scene fills the screen, the banner is fully visible when settled, and the animation runs smoothly
