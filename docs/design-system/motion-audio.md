# Motion and audio

Motion is bouncy and short. Things arrive with one overshoot, controls snap, and the world never stops moving a little.

## Easing

- `ease-bounce` for anything arriving: popups, dialogs, the selection ring, the streak badge.
- `ease-snap` for controls changing state: press, toggle, segment.
- `ease-out-soft` for anything leaving: popups rising away, a poof clearing.
- `ease-sway` for ambient loops: windsock, clouds, coach mark bob.

## Moments

| Moment | Motion | Duration | Sound |
|---|---|---|---|
| Aircraft in flight | Propeller flicks, rotor spins | `dur-rotor` per turn, linear | Engine hum, pitch by type |
| Turning | Sprite banks: squeezes to 86% across the wingspan | Follows turn rate | none |
| Aircraft selected | Ring pops in, then rotates slowly | `dur-quick`, then 7s per turn | Soft click |
| Drawing a path | Dashes march to the end dot | `dur-march` per dash | Light pencil tick per segment |
| Cleared to land | Path cut off, flashes white once (solid with reduced motion): the sound is the flash's twin | `dur-pop` | Success chime |
| Tutorial drag demo | The finger travels the dotted path from the aircraft to the landing end, pauses, repeats. With reduced motion: the dotted path with the finger still at its end | `dur-demo-drag`, pause `dur-pop`, `ease-out-soft` | none |
| Valid landing zone | Halo pulses | 1s, alternating | none |
| Touchdown | Squash and stretch, then the sprite is removed | `dur-land` | Ding |
| Safe landing | Star burst, score popup rises | `dur-burst`, popup 1.8s | Ding, higher on a streak |
| Collision | Poof replaces both aircraft, lives chip shakes | `dur-burst`, shake `dur-pop` | Boing |
| Miss | Danger popup at the exit edge | 1.8s | Low whoosh |
| Button press | Face drops 2px onto its lip | `dur-press` | Tick |
| Dialog opens | Drops 40px and settles | `dur-dialog` | none |
| Ambient | Clouds drift, windsock waves | `dur-drift`, `dur-sway` | Light upbeat music |

## Rules

- One orchestrated moment at a time: on a landing, the squash comes first, then the burst and popup together.
- Nothing blocks input. Every animation can be interrupted by the next tap.
- With reduced motion on, keep rotor spin and path marching off, replace bursts with a 400ms still frame, and keep all sounds.
- Sound is playful and never harsh: hums, dings, boings. Every sound has a visual twin, so the game is fully playable muted.
