# Proposal

GitHub issue: #63

## Why

Clearing an aircraft to land is the key action in the game. A short success sound confirms it at once, even when the player's finger hides the white flash on the path. It is also the first sound in the game, so it lays down how sound effects are played.

## What Changes

- Convert `audio/sfx/success.wav` (already on `development`) to a short mono MP3 and add it to the Android app. The WAV stays as the source; the conversion is written down so it can be redone.
- The engine reports each landing clearance as a game event: once each time a drawn path enters a valid landing zone and is cut off (the moment the path flashes white). It reports nothing for a path that does not end in a valid zone: open air, a misaligned runway approach, the wrong end of a one-way runway, or the wrong zone type.
- The game screen plays the success sound once for each clearance event, also when several aircraft are cleared shortly after each other, and again when a re-routed aircraft is cleared again.
- Sound playback goes behind `AppHost` as a small sound player; the Android host plays the MP3 with a low-latency player. `commonMain` stays free of platform APIs.
- The design system's moments table (`motion-audio.md`) gets a "Cleared to land" row with the success sound, before the sound is built. `docs/game-design.md` and `docs/architecture.md` describe the sound and the new `AppHost` member.

## Capabilities

### New Capabilities
- `sound-effects`: which game moments play a sound, how often, and that every sound has a visual twin and is played through the platform. This change adds the first one, the clearance sound.

### Modified Capabilities
- `game-engine`: the engine reports a landing clearance as an event, once per clearance.

## Out of scope

- Any other sound from the moments table (engine hum, select click, pencil tick, touchdown ding, boing, whoosh, button ticks, music).
- A sound effects on/off switch. That is the settings page, #66; its toggle gates the sound player added here. This change ships with sound always on.
- An iOS sound player (iOS is not built yet); shared code falls back to a silent player.
- Volume controls inside the game.

## Dependencies

- `audio/sfx/success.wav` on `development` (present).
- No dependency on other active changes. #66 may land before or after this one; whichever lands second wires its toggle to the sound player.

## Impact

- `shared/commonMain`: a new `GameEvent` for clearances in `engine/`, a `SoundPlayer` interface and `SoundEffect` in `platform/`, `AppHost.sounds` with a silent default, and the game screen mapping events to sounds.
- `androidApp`: `res/raw/success.mp3` and a SoundPool-based player in `AndroidAppHost`.
- `audio/`: the MP3 conversion notes next to the WAV.
- Docs: `docs/design-system/motion-audio.md`, `docs/game-design.md`, `docs/architecture.md`.
- Play Store: an audio resource needs no permission; the play-policy-critic reviews the change because it adds a new resource type.
