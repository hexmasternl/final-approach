# Audio sources

`sfx/success.wav` is the source of the "cleared to land" success sound (stereo, 44.1 kHz, 16-bit PCM, about 1.5 s).

## Android resource

The Android app plays the sound from `app/src/androidApp/src/main/res/raw/`.

**Current state:** the autocoder run that added the sound had no ffmpeg, so `res/raw/success.wav` is a plain copy of the source (264 KB). `SoundPool` plays PCM WAV directly.

**To do (human):** replace it with the small MP3 the `path-cleared-sound` design asks for, then delete `res/raw/success.wav` (the resource name `success` stays the same):

```
ffmpeg -i audio/sfx/success.wav -af "silenceremove=start_periods=1:start_threshold=-50dB" -ac 1 -ar 44100 -b:a 128k app/src/androidApp/src/main/res/raw/success.mp3
```

Record the ffmpeg version used here, and check with `ffprobe` that the file is mono, 44100 Hz, about 128 kbps and starts within about 30 ms of the first audible sample.
