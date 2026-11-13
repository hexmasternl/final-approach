# Design

## Context

The engine already supports one-way runways end to end: `RunwayConfig.mode = RunwayMode.OneWay(heading)` gives one landing heading, `LandingZones.of` builds a single threshold zone for it, and `LevelConfigValidator` checks the heading lies along the axis. Headings are compass radians (0 north, `EAST = PI / 2`), world y grows downwards. Today every level uses `RunwayMode.TwoWay`, and `LevelLayoutsTest` and `LevelsTest` assert that.

`drawRunway` in `ui/game/AirfieldDrawing.kt` draws threshold bars and touchdown patches at both ends regardless of mode, a dashed centre line, and a number per landing heading. So a one-way runway today differs from a two-way one only by a missing number: the silhouette is symmetric and the player must know that numbers mark landing ends.

## Goals / Non-Goals

**Goals:**
- Level data change for Meadow Field `09-27` and Island `04-22`.
- A one-way runway that reads as one-way by shape.

**Non-Goals:**
- Engine changes. Spawner changes. Text hints about one-way runways.

## Decisions

1. **Headings.** Meadow Field `09-27`: `RunwayMode.OneWay(3 * PI / 2)` (west, runway 27). Island `04-22`: `RunwayMode.OneWay(5 * PI / 4)` (south-west, runway 22). Use named values (`WEST`, or `EAST + PI`, and `PI / 4 + PI`) next to the existing `EAST` and `SOUTH` constants in `Levels.kt`, not bare literals.
   *Rejected:* renaming the runway ids to `27` and `22`. The id is internal and stable (tests use it); the painted number already comes from the landing heading.

2. **A one-way marking is needed; a single number is not enough.** Principle 4, silhouette before colour, and the fact that most players do not know runway numbering, mean the runway must show its direction by shape. *Rejected:* number only (the issue's baseline) because both ends still carry threshold bars and patches, so the strip looks symmetric. *Rejected:* a yellow X at the far end, because an X is the real-world sign for a closed runway and reads as danger.

3. **What the one-way marking is.** Threshold bars, touchdown patch and number only at the landing end; the far end is plain asphalt. The centre line becomes a row of chevrons pointing in the landing direction, at the same pitch as the dashes. Chevrons are open V strokes in `marking`, stroke `line4` with round caps and joins, arm length `space5`, spacing `space4` (the existing dash and gap tokens), drawn through `FieldScale` like the other markings. If these tokens look wrong in the showcase, the design system update picks new ones and the code follows; no value is invented in code.

4. **Design system first.** Add the one-way variant to `docs/design-system/components/Runway.md` (a `mode="one-way"` input and its construction), a line in `world.md` under Landing zones, and a one-way runway example in `showcase.html` next to the existing runway. Then implement it in `drawRunway`, which branches on `runway.mode`.
   *Rejected:* drawing the chevrons only in Compose, because CLAUDE.md says to add a missing component to the design system before using it.

5. **Rendering stays a pure function of `RunwayConfig`.** `drawRunway` reads `runway.mode` and `runway.landingHeadings`; no game rule moves into the composable. The landing end is the end the landing heading enters from: `center - direction(heading) * length / 2`.

6. **The Meadow Field helipad stays where it is.** It sits on the extended centre line of runway 27, about 150 world units east of the threshold. A straight-in approach therefore flies over the pad. That is allowed: an airplane path through a helipad zone does nothing, and a landed helicopter cannot collide with a flying airplane. Approaches 25 to 30 degrees off the centre line, or a curved path, clear the pad entirely. It adds a little sequencing between helicopters and airplanes, which suits the level.
   *Rejected:* moving the helipad north or south of the centre line, because the issue keeps the layout and the decoration around it is placed by hand; it can follow as its own change if playtesting asks for it.

7. **Room check as a test.** A `commonTest` test walks back from each one-way runway's threshold along the reverse landing heading to the world edge and asserts the segment stays inside the world and below `LevelDefaults.HUD_BAND`, and is at least `runwayZoneDepth` long. Meadow Field has 640 units east of the threshold; the Island diagonal has about 267 units before reaching the HUD band.

## Risks / Trade-offs

- [Meadow Field gets harder for beginners: airplanes from the west must loop round] → This is the intended challenge; the first phase is light props and helicopters at most two at a time, which leaves time to loop.
- [Airplanes on the straight-in to 27 fly over a helicopter that is still airborne near the pad] → Normal sequencing; the proximity warning ring shows it in time.
- [Concurrent changes `larger-level-worlds`, `world-zoom-retune` and `detailed-scenery` edit `Levels.kt` and `LevelLayoutsTest`] → Only two `mode` lines and one test assertion change here; rebase on whichever lands first.

## Assumptions

- The runway ids stay `09-27` and `04-22`.
- The one-way marking is: markings only at the landing end, chevron centre line pointing the landing way, using existing tokens.
- The Meadow Field helipad is not moved.
- No coach mark or text tells the player a runway is one-way; the marking is enough.
- Everything that draws these levels through `drawRunway` (game screen, level backdrop, previews) picks up the new marking automatically, which is wanted.
