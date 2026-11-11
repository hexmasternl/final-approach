# Spec Delta

## Purpose

The wordmark is the game's name typeset as its logo: "Final Approach" in the display font, outlined, dropped and tilted. Any screen that needs the name where a logo would go uses it.

## ADDED Requirements

### Requirement: Wordmark text and lines
The wordmark SHALL always read "Final Approach" on two lines: "Final" on the first line and "Approach" on the second, with the second line indented by 0.35 times the font size.

#### Scenario: Two lines
- **WHEN** the wordmark is shown
- **THEN** "Final" is on the first line and "Approach" on the second, starting further right than "Final" by 0.35 times the font size

### Requirement: Wordmark colours
The wordmark SHALL draw "Final" in the `fuselage` colour and "Approach" in the `sun` colour, in the display font at weight 700.

#### Scenario: Line colours
- **WHEN** the wordmark is shown in the Day theme
- **THEN** "Final" is filled in `fuselage` and "Approach" in `sun`, both in DynaPuff bold

### Requirement: Wordmark outline and drop
Every letter of the wordmark SHALL have an `outline`-coloured stroke painted behind its fill, and a hard, unblurred `outline`-coloured copy of the outlined letters offset straight down behind it. At regular size the stroke SHALL be 12dp wide and the drop 7dp; at small size the stroke SHALL be 8dp wide and the drop 4dp.

#### Scenario: Regular outline and drop
- **WHEN** the regular wordmark is shown
- **THEN** each letter has a 12dp `outline` stroke behind the fill and a 7dp hard `outline` drop below it, with no blur

#### Scenario: Small outline and drop
- **WHEN** the small wordmark is shown
- **THEN** each letter has an 8dp `outline` stroke behind the fill and a 4dp hard `outline` drop below it

### Requirement: Wordmark tilt
The wordmark SHALL be rotated 3 degrees anticlockwise as a whole, and the space it takes in a layout SHALL include its rotated stroke and drop, so no part of it is clipped.

#### Scenario: Tilted and whole
- **WHEN** the wordmark is shown
- **THEN** both lines are tilted 3 degrees anticlockwise and no part of a letter, its stroke or its drop is cut off

### Requirement: Wordmark sizes
The wordmark SHALL come in a regular size, set in `display-xl` (72sp on a 68sp line height), and a small size set at 36sp on a 34sp line height. Apart from scale, stroke and drop, both sizes SHALL look the same.

#### Scenario: Regular size
- **WHEN** the regular wordmark is shown
- **THEN** its letters are 72sp on a 68sp line height

#### Scenario: Small size
- **WHEN** the small wordmark is shown
- **THEN** its letters are 36sp on a 34sp line height, with the same lines, colours, tilt and indent as the regular size

### Requirement: Wordmark is one shared component
The wordmark SHALL be one reusable component that any screen can place in either size. Every colour, font, size, stroke width, offset, indent and angle it uses SHALL come from the theme or from the component's own named defaults, never from literals in its drawing code or at the call site.

#### Scenario: Reused on another screen
- **WHEN** a screen other than the main page needs the name
- **THEN** it places the same wordmark component in the size it needs, without copying its drawing

#### Scenario: No literals
- **WHEN** the wordmark's code is reviewed
- **THEN** it contains no colour, size, stroke, offset or angle literal outside its named defaults, and its colours follow the active theme

### Requirement: Wordmark is announced once as a heading
Screen readers SHALL announce the wordmark once, as the heading "Final Approach", and SHALL NOT announce its separate lines or drawing layers.

#### Scenario: Screen reader
- **WHEN** a screen reader reaches the wordmark
- **THEN** it reads "Final Approach" once and reports it as a heading
