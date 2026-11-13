# Tasks

## 1. Design system and docs

- [x] 1.1 Add the "Cleared to land" row to the moments table in `docs/design-system/motion-audio.md` (design decision 6). Verify the "Valid landing zone" row is unchanged and the new row names the white flash as the sound's visual twin.
- [x] 1.2 Describe the success sound in `docs/game-design.md` (when it plays, when it does not, one per clearance, kept with reduced motion). Verify it matches the `sound-effects` spec.

## 2. Engine clearance event

- [x] 2.1 Add `GameEvent.ClearedToLand(aircraftId, zone)` and the pending-clearance queue in `GameEngine` (design decision 1): record on the `Flying` to `ClearedToLand` transition in `extendPath`, prepend and clear in `update`, clear on restart. Verify with `GameEngineTest` cases for each `game-engine` scenario: one event then none, no event for points after the cut-off, two aircraft before one update, re-route cleared again, and no event for open air, misaligned runway, wrong end of a one-way runway, airplane at a helipad, helicopter at a runway.
- [x] 2.2 Check that `Scoring.apply` and the `landings` count ignore the new event. Verify existing `ScoringTest` and `GameEngineTest` still pass and a test asserts a clearance adds no score.

## 3. Sound player behind AppHost

- [x] 3.1 Add `SoundEffect`, `SoundPlayer` and `SilentSoundPlayer` in `platform/`, and `AppHost.sounds` defaulting to `SilentSoundPlayer` (design decision 2). Add a `RecordingSoundPlayer` to `FakeAppHost`. Verify `./gradlew :shared:allTests` passes and `commonMain` has no `android.*` import.
- [x] 3.2 Add `SoundCues.of(events)` in `ui/game` (design decision 3) with a `commonTest` test: one `Success` per `ClearedToLand`, nothing for `Landed`, `Collided`, `LeftArea`, two cues for two clearances. Verify the tests pass.
- [x] 3.3 In `GameScreen.kt`, after each `engine.update`, play every cue from `SoundCues.of(state.events)` through `host.sounds`. Verify no other game logic is added to the composable.
- [x] 3.4 Document `sounds` in the platform boundary list of `docs/architecture.md`, including the silent default on platforms without a player and that #66's toggle gates it. Verify it matches the code from 3.1 to 3.3.

## 4. The MP3

- [ ] 4.1 Convert `audio/sfx/success.wav` to `app/src/androidApp/src/main/res/raw/success.mp3` with ffmpeg (design decision 5): mono, 44.1 kHz, 128 kbps, leading silence trimmed. Write the exact command and ffmpeg version in `audio/README.md`. Verify with `ffprobe` that the file is mono, 44100 Hz, about 128 kbps, and that its first audible sample is within about 30 ms of the start. **Not done by the autocoder run (no ffmpeg on the machine): `res/raw/success.wav` is a copy of the source for now and `audio/README.md` has the command; a human converts it.**

## 5. Android playback

- [x] 5.1 Implement `sounds` in `AndroidAppHost` with a `SoundPool` (design decision 4): game audio attributes, `MAX_SOUND_STREAMS` as a named constant, `R.raw.success` loaded once, released when the activity is destroyed. Verify `./gradlew :androidApp:assembleDebug` succeeds.
- [ ] 5.2 Human, on a device: clear an airplane to a runway and a helicopter to a helipad; the sound plays once each, together with the flash. Clear two aircraft in quick succession; two sounds play without cutting off. Re-route a cleared aircraft and clear it again; it plays again. Draw into open air, a misaligned approach, the wrong end of a one-way runway, and the wrong zone type; nothing plays. Turn reduced motion on; the sound still plays. Turn the media volume off; the clearance is still visible.

## 6. Checks

- [ ] 6.1 Run the play-policy-critic agent on the change (new resource type). Verify it reports no open findings, or file them as issues.
- [x] 6.2 Run `./gradlew :shared:allTests` and `./gradlew :androidApp:assembleDebug` from `app/src`, and `openspec validate path-cleared-sound --strict`. Verify all pass.
