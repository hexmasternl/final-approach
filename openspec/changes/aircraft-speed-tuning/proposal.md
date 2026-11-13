## Why

Every aircraft is too fast to sequence comfortably, and the jumbo is too large. The author gives exact reductions per type, and wants all speeds in one file that is easy to tweak.

GitHub issue: #54

## What Changes

- Default speeds drop to these fractions of the current defaults: helicopter 1/2, light prop 2/3, turboprop 2/3, jet 2/3, jumbo 1/2. In world units per second at the current 1280 x 720 world: helicopter 20, light prop about 36.7, turboprop 50, jet about 66.7, jumbo 65.
- The jumbo's size drops to half: its type radius, and with it the sprite and the collision shape, is halved (44 to 22).
- All default speeds live in one file (`AircraftSpeeds`) that holds a base speed per type, so tuning is one edit.
- The "speed rises from helicopter to jumbo, each 15 percent faster" rule no longer holds for the jumbo, whose new speed (65) is just below the jet's (66.7). The rule is narrowed to helicopter, light prop, turboprop and jet.

Out of scope: landing points, turn behaviour, the world zoom (see `world-zoom-retune`, which scales speeds and radii with the zoom factor on top of these base values), and the rollout speed rules beyond their automatic consequence below.

Consequence to note: the landing speed reference is the light prop's flight speed, so rollouts get slower too (the light prop's speed fell to 2/3).

Depends on: nothing. Overlaps with the speed scaling in `detailed-scenery` and `world-zoom-retune`: the file's values are the zoom-1 values.

Issue #52 (ground speed) is a separate issue and is not part of this change.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `aircraft-types`: new default speeds in one file, a smaller jumbo, and a narrowed speed-ordering rule.

## Impact

- `shared` `domain`: `DefaultAircraftSpecs`, new `AircraftSpeeds` file, `AircraftTypeSpec` (jumbo radius).
- `shared` tests that assert speed ratios or the jumbo size, including spawn and collision tests.
- Docs: `docs/game-design.md` (aircraft roster and speed table).
