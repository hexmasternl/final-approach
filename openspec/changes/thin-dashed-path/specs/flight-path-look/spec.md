## ADDED Requirements

### Requirement: Thin dashed flight path
The flight path SHALL be drawn as a thin, fine dashed line: a coloured dash 3px wide over an outline casing 7px wide, so the border is 2px on each side, with dashes 10px long and gaps 8px long. The values SHALL come from design-system tokens, not from literals.

#### Scenario: Path stroke and border
- **WHEN** the player draws a path for an aircraft
- **THEN** the path shows a 3px livery-coloured dash line with a 2px dark-navy border on each side, much thinner than the previous 8px line with 14px casing

#### Scenario: Dash rhythm
- **WHEN** a path is drawn
- **THEN** its dashes are 10px long with 8px gaps

#### Scenario: Tokens only
- **WHEN** the path drawing code is searched for stroke, dash and gap values
- **THEN** all of them come from `FaDimens` and none is a literal

### Requirement: Fine end dot
The end dot of a flight path SHALL be 12px across with a `line-2` outline in the livery colour, so it matches the thin line.

#### Scenario: End dot size
- **WHEN** a path is drawn
- **THEN** its end dot is 12px across with a `line-2` outline

### Requirement: Dashes march at half speed
On a selected path the dashes SHALL march towards the end dot at half the previous animation speed: one period per 1800ms (`dur-march`) instead of 900ms. With reduced motion the dashes SHALL NOT march.

#### Scenario: March speed
- **WHEN** an aircraft is selected and has a path
- **THEN** its dashes complete one dash-and-gap period every 1800ms

#### Scenario: Reduced motion
- **WHEN** the device's reduced-motion setting is on
- **THEN** the dashes of a selected path do not move
