# Art and Audio Style

The short brief. The design system in `docs/design-system/` is the full reference: `brand-book.md` for the principles, `world.md` for the world, `components/` for every component. This is version 2.

## Visual
- Top-down view with exaggerated, toy-like scale.
- A lit and shaded world. One sun from the top-left: every material is drawn in three flat tones (lit, base, shade), and everything standing on the ground casts a hard shadow down-right. No gradients or textures.
- Navy outside, tinted inside. Silhouettes have a dark-navy outline; lines inside a shape are a darker tone of its own material.
- Line weight is rank: aircraft carry the heaviest outline, landing zones the next, scenery and UI a thinner one.
- Buildings and trees show a south wall or trunk and never rotate. Vehicles, animals and boats are strictly top-down.
- Flat ground has no outline. Edges are transitions: a runway has a shoulder and rough grass, water has shallows and sand, a road has a kerb and a verge. Only the airfield is mown in stripes.
- Aircraft own the brightest white and the most saturated colours. Scenery whites are cream. Only airborne things have a soft shadow.
- Rounded shapes; aircraft have big cockpit windows and chunky propellers and rotors.
- Palette: grass green, sky-blue UI, asphalt grey with white markings, warm yellow, orange and red aircraft accents, in ramps of light, base and shade.
- Path line: dashed, chunky, coloured per aircraft, with an end dot.

## Animation
- Spinning propellers/rotors; banking tilt when turning.
- Squash-and-stretch on landing, star burst on success, comic "poof" on collision.
- Ambient drifting clouds, waving windsock.

## UI
- Moulded controls: a face with a highlight along the top, a lip in a darker tone of the same colour, and one thin navy outline. Pressing lowers the face onto its lip.
- A lip means pressable. Readouts are flat: HUD values are flight strips, tags are tinted chips, celebrations are tilted stickers.
- Dialogs are boarding passes: a tinted header, the message, a perforation, and a stub with stats and actions.
- A disabled control is flat and paper-coloured, never faded.
- Type: DynaPuff for titles, button labels and scores; Nunito for running text; Inconsolata for labels, readouts and callsigns.
- HUD: score, streak, next-aircraft hint, lives and one pause button.

## Audio
Playful engine hums, "ding" on landing, boing on collision, light upbeat music.

## Assets
Prefer Compose-drawn vector shapes (resolution independent, cross-platform); bitmaps only when needed.
