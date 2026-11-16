# Spec Delta

## Purpose

Defines what the Final Approach design system contains and guarantees: its principles, tokens, construction rules for the world and for UI components, its showcase, and what stays stable for the app generated from it.

## ADDED Requirements

### Requirement: Principles
The design system SHALL state three general principles (toy scale, silhouette before colour, the field stays clear), nine world rules W1 to W9 and seven UI rules U1 to U7, each with a name, a one-paragraph rule and its identifier, and SHALL NOT state "outline everything", "flat and bright" or a four-fill budget.

#### Scenario: Reading the brand book
- **WHEN** `brand-book.md` and `world.md` are read
- **THEN** W1 to W9 and U1 to U7 are each present once with their identifier, and the phrases "Outline everything", "Flat and bright" and "four fill colours" do not occur

### Requirement: One light for the world
Every scenery sprite SHALL be lit from the top-left: a lit tone towards the north-west, a shade tone towards the south-east, and a hard, unblurred shadow cast down-right. Only airborne things SHALL have a soft shadow.

#### Scenario: Shadow direction
- **WHEN** any scenery sprite is shown in the showcase at any rotation its kind allows
- **THEN** its shadow lies down-right of it and has a hard edge

#### Scenario: Airborne and grounded shadows differ
- **WHEN** an aircraft flies over a tree in a showcase game screen
- **THEN** the aircraft's shadow is soft and further from its sprite than the tree's hard shadow is from the tree

### Requirement: Three tones per material
A scenery sprite SHALL use at most three main materials, each drawn in flat tones of that material's ramp, and SHALL NOT use a gradient, a texture or a blur. On top of its main materials a sprite MAY name at most two trim materials for small parts such as a chimney or an awning. Glass, lamps and other single trim tokens do not count as a material. A sprite that carries other things, such as a vessel with containers, is exempt from the count.

#### Scenario: Generator check
- **WHEN** the generator processes the scenery sprites
- **THEN** it fails for a sprite with more than three main materials, for a sprite that names more than two trim materials, and for a paint that is not a colour token of the design system

### Requirement: Navy outside, tinted inside
The silhouette of a scenery sprite SHALL be stroked in `outline`. Every line inside the silhouette SHALL be a darker step of the material it lies on.

#### Scenario: Silhouette
- **WHEN** the generator processes a scenery sprite that has height
- **THEN** it fails if the sprite has no stroke in `outline`

#### Scenario: Inner lines
- **WHEN** the design-critic agent reviews the scenery sprites in the showcase
- **THEN** it finds no line inside a silhouette drawn in `outline`, apart from small parts that stand on a sprite with an outline of their own, such as a chimney or a door frame

### Requirement: Line ranks
The design system SHALL define five line ranks: `line-hero` (4) for aircraft only, `line-zone` (3) for landing zones and clouds, `line-ui` (2.5) for controls, panels, HUD strips and scenery silhouettes, `line-ui-sm` (2) for small parts, and `line-detail` (1.25) for tinted inner lines and keylines.

#### Scenario: Only aircraft use the heaviest line
- **WHEN** the showcase is rendered
- **THEN** no shape other than an aircraft sprite is stroked at `line-hero`

### Requirement: Standing scenery shows a south wall
Buildings, trees and the control tower SHALL be drawn with their top face above a visible south-facing wall or trunk, in one of three height tiers that set the wall height and the shadow length, and SHALL NOT be rotated. Scenery that moves or turns (vehicles, animals, people, boats) SHALL be drawn strictly top-down.

#### Scenario: A building
- **WHEN** the farmhouse, barn, terminal, hangar or control tower is shown
- **THEN** a roof and a south wall with at least one door or window are both visible

#### Scenario: Rotation
- **WHEN** a level places a standing sprite with a rotation
- **THEN** the design system's rules name this as not allowed, and the sprite's data marks it as not rotatable

### Requirement: Flat ground and transitions
Flat ground (lawn, roads, fields, water, sand, aprons, taxiways) SHALL have no `outline` stroke and SHALL be edged in a darker step of its own material. Where two ground materials meet, a transition band SHALL lie between them: shoulder and rough grass around a runway, shallows and sand around water, a kerb and verge along a road.

#### Scenario: A pond
- **WHEN** a pond is shown on the lawn
- **THEN** from the water outwards there are shallows, a sand band and a darker grass band, and no navy line

### Requirement: Mown airfield, unmown countryside
Mowing stripes SHALL appear only inside the airfield, parallel to its main runway, in two adjacent grass steps. Ground outside the airfield SHALL be unstriped, with detail (tufts, flowers, patches) in clusters and none on runways, helipads or their approach ends.

#### Scenario: Meadow Field
- **WHEN** the Meadow Field game screen is shown
- **THEN** stripes are visible only inside the airfield boundary and run along the runway

### Requirement: Landing zones keep their footprint
Runway and helipad art SHALL keep its v1 footprint and world size, and SHALL be the only ground shapes stroked in `outline`. A runway SHALL show a shoulder, a rough-grass fringe, threshold bars, its numbers, a centre line, aiming blocks, touchdown marks and edge lights. A helipad SHALL show a pad with corner lights, the `marking-warn` ring and the `marking` H. Taxiways SHALL be a lighter asphalt with a yellow centre line.

#### Scenario: Same size
- **WHEN** the v2 runway and helipad are compared with v1
- **THEN** the asphalt rectangle of the runway and the circle of the helipad have the same world dimensions

### Requirement: Aircraft own the top of the range
Scenery SHALL NOT use the `fuselage` white, a path colour or `sun-500`. Scenery whites SHALL be cream (`wall-100`, `hide-100`). The control tower cab and the windsock MAY keep their `cherry` and `tangerine` accent.

#### Scenario: Generator check
- **WHEN** a scenery sprite uses `fuselage`, `path-1` to `path-6` or `sun-500`
- **THEN** the generator fails and names the sprite

### Requirement: Colour ramps
Every world material and every accent SHALL be a ramp of three to six tones with fixed roles (300 lit, 500 base, 700 shade or lip, 900 tinted line, 100 or 200 highlight), each with a Day value and, where it differs, Night and Dawn values and a usage note. A `paper` ramp SHALL define UI surfaces.

#### Scenario: Token file
- **WHEN** `tokens.json` is read
- **THEN** each of grass, leaf, pine, wood, roof, wall, tin, stone, concrete, asphalt, water, straw, sun, sky, cherry, tangerine, mint and paper has at least three steps, and every step has a usage note

### Requirement: Night and Dawn from the same sprites
Night and Dawn SHALL be produced by token values alone, with no sprite drawn differently. Windows SHALL take the `window` token, which is glass by day and a warm light at night. Panels, strips and aircraft SHALL stay light in Night and Dawn.

#### Scenario: Theme switch
- **WHEN** the showcase is switched between Day, Night and Dawn
- **THEN** every scenery sprite keeps its shapes and changes only its colours, and building windows are lit in Night

### Requirement: Moulded controls
A pressable control SHALL be built from a face in its base tone, a highlight along the top edge in its light tone, a lip below the face in its 700 tone and one `line-ui` outline around all of it, with inner corners following the nested-radius rule. Pressing SHALL lower the face onto the lip.

#### Scenario: Primary button
- **WHEN** a primary button is shown
- **THEN** its lip is `sun-700`, not `outline`, and the outline is 2.5dp

### Requirement: A lip means pressable
Only pressable elements (buttons, icon buttons, level cards, the toggle knob, the selected segment) SHALL have a lip. Readouts, chips, tags, stickers and coach marks SHALL be flat.

#### Scenario: The HUD
- **WHEN** the HUD is shown during play
- **THEN** the score, next-aircraft and lives readouts are flat strips 40dp high, and the pause button is the only element with a lip

### Requirement: Tinted inner lines in UI
Dividers, keylines, perforations and chip edges SHALL use a darker step of the surface they lie on. `outline` SHALL be used in UI only for the outer edge of an element and for text and icons.

#### Scenario: A dialog
- **WHEN** a dialog with a stats block is shown
- **THEN** the lines between its stat cells and its perforation are `paper` steps, not navy

### Requirement: Disabled state
A disabled control SHALL be drawn in `paper` tones with no lip movement and no shine, at full opacity, with its label in `ink-3`.

#### Scenario: Disabled button on the field
- **WHEN** a disabled button is shown over the lawn
- **THEN** it is opaque and its label is readable

### Requirement: Component set
The design system SHALL specify and show, with construction, sizes, states, do and don't, and a Compose note for each: Button, IconButton, Toggle, SegmentedControl, Chip, FlightStrip, LivesMeter, NextAircraftHint, Sticker (streak badge and score popup), Panel, Dialog in portrait and landscape, LevelCard including its locked state, and CoachMark.

#### Scenario: Guidelines and showcase agree
- **WHEN** a component's guideline file and its showcase section are compared
- **THEN** every variant and state the guideline names is shown, and no shown variant is missing from the guideline

### Requirement: Dialog as a boarding pass
A dialog SHALL have a tinted header band with an eyebrow label and the title, a body, a perforation, and a stub holding stats and actions. In the landscape layout the perforation SHALL be vertical with the stub on the right.

#### Scenario: Landscape game over
- **WHEN** the game-over dialog is shown at 800 by 450dp
- **THEN** it fits inside the screen with the title and reason left of a vertical perforation and the stats and two buttons right of it

### Requirement: Type
Display type, titles, button labels and the score SHALL be DynaPuff. Body text SHALL be Nunito, never lighter than weight 600. Labels, readouts and callsigns SHALL be Inconsolata, never lighter than weight 700.

#### Scenario: Body text
- **WHEN** a panel or dialog body is shown
- **THEN** its running text is set in Nunito

### Requirement: What stays unchanged
The aircraft sprites, the icon set, the path colours, the flight path, selection ring and proximity ring tokens, the motion and easing tokens, the layer order, the touch sizes and the voice rules SHALL be the same as before this change.

#### Scenario: Unchanged assets
- **WHEN** this change's branch is compared with `development` before it
- **THEN** no file under `assets/aircraft` or `assets/icons` differs, and the duration, easing, zIndex and path token values are equal

### Requirement: App icon
The app icon SHALL show the sun-yellow jet of the game in close-up at `line-hero`, with its soft shadow, over a v2 runway on the mown lawn. The nose and the wing roots SHALL lie inside the central 66% of the icon, so every launcher mask keeps them; wing tips and tail MAY bleed off the edge. The icon SHALL be generated from the design system's own sprite and tokens, as a full-bleed icon, a rounded in-app icon, and a separate foreground and background for Android.

#### Scenario: Under a circle mask
- **WHEN** the icon is shown under a circular launcher mask
- **THEN** the cockpit, the nose and both wing roots are visible

#### Scenario: Android layers match the source
- **WHEN** the icon script is run
- **THEN** the Android launcher foreground and background are rendered from the same SVG sources as the design system's icon, and the foreground has a transparent ground

### Requirement: The app keeps building
Every token name that exists before this change SHALL still exist after it with the same value. Tokens that v2 no longer uses SHALL be marked deprecated with the name of their replacement.

#### Scenario: Regenerating the theme
- **WHEN** the generator is run after this change and the shared module is built and tested
- **THEN** the build and all tests pass without any change to hand-written app code

### Requirement: Showcase
`showcase.html` SHALL show the principles, tokens, every scenery sprite, landing zones, every component and reference screens (game screen, main menu, pause and game-over dialogs, level select) in v2, switchable between Day, Night and Dawn, and SHALL NOT show deprecated tokens. `showcase.pdf` SHALL match it.

#### Scenario: Design review
- **WHEN** the design-critic agent reviews the showcase game screens in Day, Night and Dawn
- **THEN** it reports that aircraft, flight paths and landing zones read before any scenery, and names no sprite that breaks W1 to W9
