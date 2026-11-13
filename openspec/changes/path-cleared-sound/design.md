# Design

## Context

The engine clears an aircraft inside `GameEngine.extendPath` (through `PathDrawing.extend`), which is an input call, not part of `update`. `GameState.events` holds what happened during the last `update` and is replaced on every update; today it carries `Landed`, `Collided` and `LeftArea`, and nothing in `ui/` consumes it yet. The path flash is drawn from `FlightPath.lockedAt` (`FlightPathDrawing.kt`). There is no audio in the app. `AppHost` already hides platform concerns, with `ads` defaulting to `NoOpAds`, which is the pattern to copy.

## Goals / Non-Goals

**Goals:** a clearance event from the engine, one success sound per event, playback behind `AppHost`, a reproducible MP3.

**Non-Goals:** other sounds, a mute setting (#66), iOS playback, music.

## Decisions

### 1. Clearances are queued and reported by the next `update`

`extendPath` records a `GameEvent.ClearedToLand(aircraftId, zone)` in a pending list when `PathDrawing.extend` turns a `Flying` aircraft into `ClearedToLand`. The next `update` starts its events list with the pending events and empties the list. So every clearance shows up in exactly one `GameState.events`, at most one frame (about 16 ms) after the gesture, and tests need only `kotlin.test`.

Detection is a status transition (before not `ClearedToLand`, after `ClearedToLand`), so points after the cut-off and the re-route case (`beginPath` resets to `Flying`) behave correctly without extra rules. `zone` is the `LandingZone` from `AircraftStatus.ClearedToLand`, so the event says whether it was a runway or a helipad and a later change can give them different sounds.

Rejected:
- *Emitting into `GameState.events` from `extendPath` directly:* the next `update` overwrites it, and the frame loop could miss it.
- *A `SharedFlow` of events on the engine:* adds a second channel next to `GameState`, ordering against the frame clock becomes a concern, and tests would need `kotlinx-coroutines-test`.
- *The UI watching `path.lockedAt` change:* puts a game rule (what counts as a clearance) in the UI.

Clearances made while paused or after game over cannot happen, because input is blocked then; if the queue is somehow non-empty at game over it is dropped with the next `restart`.

### 2. A `SoundPlayer` behind `AppHost`

In `platform/`:

```kotlin
enum class SoundEffect { Success }
interface SoundPlayer { fun play(effect: SoundEffect) }
object SilentSoundPlayer : SoundPlayer { override fun play(effect: SoundEffect) = Unit }
```

`AppHost` gets `val sounds: SoundPlayer get() = SilentSoundPlayer`, like `ads`. `FakeAppHost` gets a `RecordingSoundPlayer` that lists what was played. `play` never blocks and never throws; if the sound is not loaded yet it plays nothing.

#66's sound effects toggle gates this one player (a wrapper that checks the setting), so the game never checks the setting itself.

Rejected: playing audio through a Compose or KMP audio library in `commonMain` — no such dependency exists in the project, and one interface keeps Android APIs out of shared code.

### 3. Mapping events to sounds is a pure function in `ui/game`

`SoundCues.of(events: List<GameEvent>): List<SoundEffect>` maps each `ClearedToLand` to `SoundEffect.Success` and everything else to nothing. The frame loop in `GameScreen.kt`, right after `engine.update`, calls `host.sounds.play` for each cue. The mapping is tested in `commonTest`; the composable only forwards.

Rejected: mapping inside the engine — which sound a moment makes is presentation, and the engine stays free of audio.

### 4. Android: SoundPool with the MP3 in `androidApp/res/raw`

`AndroidAppHost` creates a `SoundPool` with `AudioAttributes` `USAGE_GAME` / `CONTENT_TYPE_SONIFICATION`, loads `R.raw.success` once at start, and plays it at full volume, priority 1, no loop. Max streams is a named constant (`MAX_SOUND_STREAMS = 4`) so quick successive clearances overlap instead of cutting each other off. The pool is released when the activity is destroyed. It follows the media volume, so the device's volume and silent settings apply.

Rejected:
- *`MediaPlayer`:* streams and has start latency, too slow for a cue that must land with the flash.
- *Compose resources (`composeResources/files`):* SoundPool needs a resource id or file descriptor; the bytes live in assets under a generated path, which is awkward to hand to SoundPool. The iOS host will bundle its own copy when it exists.

### 5. The MP3 and its source

Keep `audio/sfx/success.wav` as the source. Produce `app/src/androidApp/src/main/res/raw/success.mp3` with ffmpeg: mono, 44.1 kHz, 128 kbps, leading silence removed, for example

```
ffmpeg -i audio/sfx/success.wav -af "silenceremove=start_periods=1:start_threshold=-50dB" -ac 1 -ar 44100 -b:a 128k success.mp3
```

Write the exact command and ffmpeg version in `audio/README.md`. Encoder padding at the start (about 25 ms) is acceptable unless it is noticeable on a device; then trim it further.

### 6. Design system first

In `docs/design-system/motion-audio.md`, add a row after "Drawing a path": `Cleared to land | Path cut off, flashes white once (solid with reduced motion) | dur-pop | Success chime`. Leave the "Valid landing zone" row as it is: the halo is a different moment and stays silent. No token is needed, because the moment's motion already uses `dur-pop` and `fuselage`.

## Assumptions

- The WAV stays in the repo as the source (`audio/sfx/success.wav`).
- The MP3 lives in `androidApp/src/main/res/raw/`.
- Sound is always on until #66 adds its toggle; either can land first.
- The sound follows the device's media volume; there is no in-game volume.
- Playback is at full volume with no pitch change.
- The sound may lag the flash by up to one frame (the next `update`), which is not noticeable.

## Risks / Trade-offs

- **First frame after start:** SoundPool loads asynchronously; a clearance in the first moments after launch could be silent. Acceptable, as levels start after the splash and menus.
- **Silence at start of the MP3:** mitigated by trimming and a device check (task 5.2).
- **iOS:** silent until an iOS player exists; noted in architecture docs.
