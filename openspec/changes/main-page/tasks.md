# Tasks

## 1. Platform boundary

- [x] 1.1 Declare `AppHost` in `commonMain` (version name, `quit()`, back-press delivery) with a fake for tests, and verify `commonMain` compiles with no `android.*` or `java.*` import
- [ ] 1.2 Implement `AppHost` in `androidApp` (version from package info, quit by finishing the activity, back presses forwarded) and verify by running the app that the reported version equals the build's `versionName`
- [ ] 1.3 Document `AppHost` in `docs/architecture.md` and verify the described names match the code

## 2. Backdrop logic

- [x] 2.1 Add `MenuBackdropConfig` (rotation period, cloud drift radius and periods, menu aircraft speeds and scale, 3-second gap) and verify no backdrop timing or size literal exists outside it
- [x] 2.2 Implement stripe rotation and cloud drift in `MenuBackdropState.update` and verify with unit tests that the angle advances with delta time and that each of the three clouds stays within its drift radius and on screen
- [x] 2.3 Implement the aircraft slot (`Flying` / `Waiting`) and verify with unit tests, using a seeded random source, that there is never more than one aircraft, that a new one appears exactly 3 seconds after the previous one is fully off screen, and that every route crosses the central region of the screen
- [x] 2.4 Verify with a unit test that over many spawns both airplanes and helicopters appear and entry points vary across edges

## 3. Backdrop drawing

- [x] 3.1 Draw the rotating sunburst stripes on a `Canvas` with `bg-sky` and `sky-300`, and verify in a preview that the corners stay covered at several rotation angles in portrait and landscape
- [x] 3.2 Draw the three clouds and the passing aircraft with spinning propellers or rotor and ground shadow, and verify in a preview that the draw order is stripes, clouds, aircraft

## 4. Main page

- [ ] 4.1 Build the main page with the Take off and Settings buttons and the version label from design-system components, and verify by running the app that both buttons and the version in the bottom-right corner are shown over the animated backdrop
- [ ] 4.2 Wire Take off to start a game and Settings to open a placeholder settings page with a way back, and verify by running the app that each navigates and that returning shows the main page
- [ ] 4.3 Build the quit confirmation dialog and the back handling, and verify by running the app that back opens the dialog, Cancel and a second back close it, Quit closes the app, and the backdrop keeps moving dimmed behind the dialog
- [ ] 4.4 Verify by running the app that an aircraft crossing a button is hidden behind it, that the button still responds, and that tapping an aircraft does nothing
- [ ] 4.5 Apply the reduced-motion setting and verify on a device or emulator with animations disabled that stripes and clouds stand still, no aircraft appear, and all actions work
- [ ] 4.6 Make the main page the screen after the splash page and the return point from a game, and verify the flow by running the app

## 5. Design system

- [ ] 5.1 Update the MainMenu reference composition and its guideline in the design system to match this page (two buttons, version label, no wordmark or best score), and verify the showcase page shows the updated composition

## 6. Integration check

- [ ] 6.1 Human check on a real device: watch the page for two minutes on a small and a large phone and confirm the rotation is barely noticeable, the clouds stay in place, aircraft arrive one at a time about 3 seconds apart, and the buttons remain readable throughout
